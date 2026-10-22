
from pathlib import Path
import math

import joblib
import numpy as np
import pandas as pd
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field


# --------------------------------------------------
# 1. Configuration
# --------------------------------------------------

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "corrected_safety_model.pkl"
DATASET_PATH = BASE_DIR / "crimedataset.csv"

RADIUS_METERS = 500
EARTH_RADIUS_KM = 6371.0
ROUTE_SAMPLING_INTERVAL_METERS = 250
MIN_DATA_COVERAGE_PERCENT = 70.0

app = FastAPI(
    title="TouristSafe Safety Prediction API",
    description=(
        "Estimates location and route safety from a trained "
        "model and available crime records."
    ),
    version="1.2.0",
)


# --------------------------------------------------
# 2. Crime columns and provisional severity weights
# --------------------------------------------------
# These are fallback weights only. If the saved model package
# contains severity_weights, those weights take priority.
# Confirm these values against your model-training process.

DEFAULT_SEVERITY_WEIGHTS = {
    "Murder": 10.0,
    "Attempt to Murder": 9.0,
    "Robbery": 7.0,
    "Dacoity": 9.0,
    "Theft": 3.0,
    "House Breaking": 5.0,
    "Vehicle Theft": 4.0,
    "Assault": 5.0,
    "Crimes Against Women": 8.0,
    "Cyber Crime": 4.0,
    "Kidnapping": 9.0,
    "Rape": 10.0,
    "Molestation": 8.0,
    "Chain Snatching": 5.0,
    "Pickpocketing": 2.0,
    "Missing Person": 2.0,
    "Cheating": 3.0,
    "Drug-related Crime": 5.0,
}


# --------------------------------------------------
# 3. Load the model and CSV dataset
# --------------------------------------------------

if not MODEL_PATH.is_file():
    raise FileNotFoundError(
        f"Model file not found: {MODEL_PATH}"
    )

if not DATASET_PATH.is_file():
    raise FileNotFoundError(
        f"CSV dataset not found: {DATASET_PATH}"
    )

model_package = joblib.load(MODEL_PATH)

if not isinstance(model_package, dict):
    raise ValueError(
        "The model file must contain a dictionary with "
        "'model' and 'features' entries."
    )

model = model_package.get("model")
FEATURES = list(model_package.get("features", []))

if model is None or not callable(getattr(model, "predict", None)):
    raise ValueError(
        "The saved model is missing or does not support predict()."
    )

if not FEATURES:
    raise ValueError(
        "The model package does not contain its feature names."
    )

# Prefer saved weights when they are available.
SEVERITY_WEIGHTS = model_package.get(
    "severity_weights",
    DEFAULT_SEVERITY_WEIGHTS,
)

if not isinstance(SEVERITY_WEIGHTS, dict):
    raise ValueError(
        "severity_weights must be a dictionary."
    )

CRIME_COLUMNS = list(SEVERITY_WEIGHTS.keys())

CATEGORY_THRESHOLDS = model_package.get(
    "category_thresholds",
    {"safe": 75, "moderate": 50},
)

SUPPORTED_FEATURES = {
    "crime_count_500m",
    "crime_severity_500m",
    "crime_density_500m",
    "nearby_record_count",
}

unsupported = set(FEATURES) - SUPPORTED_FEATURES
if unsupported:
    raise ValueError(
        f"Unsupported model features: {sorted(unsupported)}"
    )

# Read CSV, not Excel.
df = pd.read_csv(DATASET_PATH, encoding="utf-8-sig")
df.columns = df.columns.astype(str).str.strip()

required_columns = {"Latitude", "Longitude"}
missing_coordinates = required_columns - set(df.columns)

if missing_coordinates:
    raise ValueError(
        "CSV is missing coordinate columns: "
        f"{sorted(missing_coordinates)}"
    )

missing_crimes = set(CRIME_COLUMNS) - set(df.columns)
if missing_crimes:
    raise ValueError(
        "CSV is missing crime columns: "
        f"{sorted(missing_crimes)}. "
        "Check the CSV column names and model severity weights."
    )

numeric_columns = list(
    dict.fromkeys(
        CRIME_COLUMNS + ["Latitude", "Longitude"]
    )
)

for column in numeric_columns:
    df[column] = pd.to_numeric(df[column], errors="coerce")

df = df.dropna(subset=numeric_columns).copy()

df = df[
    df["Latitude"].between(-90, 90)
    & df["Longitude"].between(-180, 180)
].copy()

if df.empty:
    raise ValueError(
        "No valid geographic records were found in the CSV."
    )

if (df[CRIME_COLUMNS] < 0).any().any():
    raise ValueError(
        "Crime count columns contain negative values."
    )

print("TouristSafe model loaded.")
print("Dataset records:", len(df))
print("Model features:", FEATURES)
print("Search radius:", RADIUS_METERS, "metres")
print("Route sampling interval:", ROUTE_SAMPLING_INTERVAL_METERS)
print("Minimum route coverage:", MIN_DATA_COVERAGE_PERCENT, "%")


# --------------------------------------------------
# 4. Request models
# --------------------------------------------------

class LocationRequest(BaseModel):
    latitude: float = Field(ge=-90, le=90)
    longitude: float = Field(ge=-180, le=180)


class RouteRequest(BaseModel):
    # Each coordinate must be [longitude, latitude],
    # matching OpenRouteService coordinate order.
    coordinates: list[list[float]] = Field(min_length=2)


# --------------------------------------------------
# 5. Geographic distance calculation
# --------------------------------------------------

def haversine_distances(latitude, longitude, data):
    """Return distances in metres from one point to data rows."""

    lat1 = math.radians(latitude)
    lon1 = math.radians(longitude)

    lat2 = np.radians(data["Latitude"].to_numpy(dtype=float))
    lon2 = np.radians(data["Longitude"].to_numpy(dtype=float))

    delta_lat = lat2 - lat1
    delta_lon = lon2 - lon1

    a = (
        np.sin(delta_lat / 2) ** 2
        + math.cos(lat1)
        * np.cos(lat2)
        * np.sin(delta_lon / 2) ** 2
    )

    a = np.clip(a, 0.0, 1.0)
    central_angle = 2 * np.arctan2(
        np.sqrt(a),
        np.sqrt(1.0 - a),
    )

    return EARTH_RADIUS_KM * central_angle * 1000


# --------------------------------------------------
# 6. Build model input features for one location
# --------------------------------------------------

def calculate_features(latitude, longitude):
    distances = haversine_distances(latitude, longitude, df)
    nearby = df.loc[distances <= RADIUS_METERS]

    # Do not invent a score when no records are nearby.
    if nearby.empty:
        return None

    crime_count = float(
        nearby[CRIME_COLUMNS].to_numpy(dtype=float).sum()
    )

    crime_severity = 0.0
    for crime in CRIME_COLUMNS:
        crime_severity += (
            float(nearby[crime].sum())
            * float(SEVERITY_WEIGHTS[crime])
        )

    radius_km = RADIUS_METERS / 1000
    area_km2 = math.pi * radius_km**2
    crime_density = crime_count / area_km2
    nearby_record_count = int(len(nearby))

    feature_values = {
        "crime_count_500m": crime_count,
        "crime_severity_500m": crime_severity,
        "crime_density_500m": crime_density,
        "nearby_record_count": nearby_record_count,
    }

    model_input = pd.DataFrame(
        [[feature_values[name] for name in FEATURES]],
        columns=FEATURES,
    )

    return feature_values, model_input


# --------------------------------------------------
# 7. Model prediction and categories
# --------------------------------------------------

def predict_score(model_input):
    try:
        prediction = float(model.predict(model_input)[0])
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=(
                "Safety prediction failed. Check that feature "
                "calculations match the model's training process."
            ),
        ) from exc

    if not math.isfinite(prediction):
        raise HTTPException(
            status_code=500,
            detail="The model returned an invalid score.",
        )

    # This assumes the trained model's output is intended
    # to represent a 0-100 score.
    return round(float(np.clip(prediction, 0, 100)), 2)


def get_safety_category(score):
    safe_threshold = float(
        CATEGORY_THRESHOLDS.get("safe", 75)
    )
    moderate_threshold = float(
        CATEGORY_THRESHOLDS.get("moderate", 50)
    )

    if score >= safe_threshold:
        return "Safe", "Green"

    if score >= moderate_threshold:
        return "Moderate", "Yellow"

    return "Unsafe", "Red"


# --------------------------------------------------
# 8. Sample a route approximately every 250 metres
# --------------------------------------------------

def sample_route_coordinates(
    coordinates,
    spacing_m=ROUTE_SAMPLING_INTERVAL_METERS,
):
    if len(coordinates) < 2:
        raise ValueError(
            "A route requires at least two coordinates."
        )

    validated = []

    for coordinate in coordinates:
        if len(coordinate) != 2:
            raise ValueError(
                "Each coordinate must be [longitude, latitude]."
            )

        longitude, latitude = coordinate

        if not (
            math.isfinite(longitude)
            and math.isfinite(latitude)
        ):
            raise ValueError("Coordinates must be finite numbers.")

        if not (
            -180 <= longitude <= 180
            and -90 <= latitude <= 90
        ):
            raise ValueError("Coordinates are outside valid ranges.")

        validated.append([longitude, latitude])

    sampled = []

    for i in range(len(validated) - 1):
        lon1, lat1 = validated[i]
        lon2, lat2 = validated[i + 1]

        segment = pd.DataFrame({
            "Latitude": [lat2],
            "Longitude": [lon2],
        })

        distance = float(
            haversine_distances(lat1, lon1, segment)[0]
        )

        steps = max(1, math.ceil(distance / spacing_m))

        for j in range(steps):
            fraction = j / steps
            sampled.append([
                lon1 + (lon2 - lon1) * fraction,
                lat1 + (lat2 - lat1) * fraction,
            ])

    sampled.append(validated[-1])
    return sampled


# --------------------------------------------------
# 9. Home and health endpoints
# --------------------------------------------------

@app.get("/")
def home():
    return {
        "message": "TouristSafe Safety Prediction API is running.",
        "docs": "/docs",
        "health_endpoint": "/health",
        "location_endpoint": "POST /predict",
        "route_endpoint": "POST /predict-route",
        "dataset_records": int(len(df)),
        "search_radius_meters": RADIUS_METERS,
        "sampling_interval_meters": ROUTE_SAMPLING_INTERVAL_METERS,
        "minimum_route_coverage_percent": MIN_DATA_COVERAGE_PERCENT,
    }


@app.get("/health")
def health():
    return {
        "status": "healthy",
        "model_loaded": model is not None,
        "dataset_records": int(len(df)),
        "model_features": FEATURES,
    }


# --------------------------------------------------
# 10. Predict one location
# --------------------------------------------------

@app.post("/predict")
def predict_location(request: LocationRequest):
    result = calculate_features(
        request.latitude,
        request.longitude,
    )

    if result is None:
        raise HTTPException(
            status_code=422,
            detail=(
                "No dataset records were found within 500 metres. "
                "A score cannot be estimated from these records."
            ),
        )

    feature_values, model_input = result
    score = predict_score(model_input)
    category, color = get_safety_category(score)

    return {
        "latitude": request.latitude,
        "longitude": request.longitude,
        "safety_score": score,
        "category": category,
        "color": color,
        "search_radius_meters": RADIUS_METERS,
        "features": feature_values,
        "nearby_records": feature_values["nearby_record_count"],
        "limitations": (
            "This is a dataset-based estimate, not a guarantee "
            "of actual or current safety."
        ),
    }


# --------------------------------------------------
# 11. Predict safety along a route
# --------------------------------------------------

@app.post("/predict-route")
def predict_route(request: RouteRequest):
    try:
        sampled_points = sample_route_coordinates(
            request.coordinates
        )
    except (ValueError, TypeError, OverflowError) as exc:
        raise HTTPException(
            status_code=422,
            detail=str(exc),
        ) from exc

    scores = []
    scored_points = []
    uncovered_points = 0

    for longitude, latitude in sampled_points:
        result = calculate_features(latitude, longitude)

        if result is None:
            uncovered_points += 1
            continue

        feature_values, model_input = result
        score = predict_score(model_input)
        category, color = get_safety_category(score)

        scores.append(score)
        scored_points.append({
            "longitude": longitude,
            "latitude": latitude,
            "safety_score": score,
            "category": category,
            "color": color,
            "nearby_records": feature_values["nearby_record_count"],
        })

    total_points = len(sampled_points)
    scored_count = len(scores)

    if scored_count == 0:
        raise HTTPException(
            status_code=422,
            detail={
                "message": (
                    "No sampled route points had records within "
                    "500 metres. No route score can be estimated."
                ),
                "total_sampled_points": total_points,
                "scored_points": 0,
                "uncovered_points": uncovered_points,
                "data_coverage_percent": 0.0,
            },
        )

    average_score = round(float(np.mean(scores)), 2)
    worst_score = round(float(np.min(scores)), 2)

    coverage_percent = round(
        scored_count / total_points * 100,
        2,
    )

    coverage_sufficient = (
        coverage_percent >= MIN_DATA_COVERAGE_PERCENT
    )

    if coverage_sufficient:
        category, color = get_safety_category(average_score)
        coverage_message = (
            "The route meets the configured data coverage "
            "threshold. This remains a dataset-based estimate."
        )
    else:
        category, color = "Insufficient data", "Gray"
        coverage_message = (
            "There is not enough data coverage to classify "
            "the entire route. The numeric score reflects only "
            "sampled points with nearby records."
        )

    worst_category, worst_color = get_safety_category(worst_score)

    return {
        "route_safety_score": average_score,
        "worst_sampled_point_score": worst_score,
        "worst_sampled_point_category": worst_category,
        "worst_sampled_point_color": worst_color,
        "category": category,
        "color": color,
        "coverage_sufficient": coverage_sufficient,
        "minimum_required_coverage_percent": MIN_DATA_COVERAGE_PERCENT,
        "sampling_interval_meters": ROUTE_SAMPLING_INTERVAL_METERS,
        "search_radius_meters": RADIUS_METERS,
        "total_sampled_points": total_points,
        "scored_points": scored_count,
        "uncovered_points": uncovered_points,
        "data_coverage_percent": coverage_percent,
        "points": scored_points,
        "coverage_message": coverage_message,
        "limitations": (
            "This is a dataset-based estimate, not a guarantee "
            "of actual or current safety. Uncovered points are "
            "excluded from the score and are not considered safe "
            "or unsafe. The route score is an unweighted mean of "
            "covered sampled points."
        ),
    }
