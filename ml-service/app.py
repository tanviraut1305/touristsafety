
from pathlib import Path

import joblib
import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(
    title="Tourist Safety ML Service",
    description="Predicts a safety score using nearby crime data",
    version="1.0.0"
)

# Load the trained model package
BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / "corrected_safety_model.pkl"

try:
    model_data = joblib.load(MODEL_PATH)

    if not isinstance(model_data, dict):
        raise ValueError("The model file must contain a dictionary.")

    model = model_data["model"]
    features = model_data["features"]

    if not callable(getattr(model, "predict", None)):
        raise ValueError("Loaded model does not support prediction.")

    if not isinstance(features, (list, tuple)) or not features:
        raise ValueError("Model features are missing or invalid.")

    print("Model loaded successfully.")
    print("Model type:", type(model).__name__)
    print("Model features:", features)

except Exception as exc:
    raise RuntimeError(
        f"Could not load the trained model: {exc}"
    ) from exc


class SafetyFeatures(BaseModel):
    crime_count_500m: float = Field(ge=0)
    crime_severity_500m: float = Field(ge=0)
    crime_density_500m: float = Field(ge=0)
    nearby_record_count: float = Field(ge=0)


@app.get("/")
def home():
    return {
        "message": "Tourist Safety ML Service is running",
        "model_loaded": True
    }


@app.get("/health")
def health():
    return {
        "status": "UP",
        "model_loaded": model is not None
    }


@app.post("/predict")
def predict_safety(data: SafetyFeatures):
    try:
        # Match input values to the model's expected feature names.
        feature_values = {
            "crime_count_500m": data.crime_count_500m,
            "crime_severity_500m": data.crime_severity_500m,
            "crime_density_500m": data.crime_density_500m,
            "nearby_record_count": data.nearby_record_count
        }

        missing_features = [
            name for name in features
            if name not in feature_values
        ]

        if missing_features:
            raise ValueError(
                f"Unsupported model features: {missing_features}"
            )

        input_data = np.array(
            [[feature_values[name] for name in features]],
            dtype=float
        )

        if not np.isfinite(input_data).all():
            raise ValueError("Input contains invalid numeric values.")

        prediction = model.predict(input_data)
        safety_score = float(prediction[0])

        if not np.isfinite(safety_score):
            raise ValueError("The model returned an invalid score.")

        if not 0 <= safety_score <= 100:
            raise ValueError(
                f"Predicted score outside 0-100: {safety_score}"
            )

        return {"safety_score": safety_score}

    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Safety prediction failed: {str(exc)}"
        ) from exc