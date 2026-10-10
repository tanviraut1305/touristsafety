package org.example.demo.Service;

import com.opencsv.CSVReader;
import org.example.demo.Entity.crimerecord;
import org.example.demo.Repository.CrimeRecordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class CrimeDataImporter implements CommandLineRunner {

    private final CrimeRecordRepository crimeRecordRepository;

    public CrimeDataImporter(CrimeRecordRepository crimeRecordRepository) {
        this.crimeRecordRepository = crimeRecordRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (crimeRecordRepository.count() > 0) {
            System.out.println("Crime data already exists. Skipping import.");
            return;
        }

        ClassPathResource resource =
                new ClassPathResource("data/CRIMEDATASET.csv");

        try (
                Reader reader =
                        new InputStreamReader(resource.getInputStream());
                CSVReader csvReader = new CSVReader(reader)
        ) {

            // Skip header
            csvReader.readNext();

            String[] row;
            int importedCount = 0;

            while ((row = csvReader.readNext()) != null) {

                if (row.length < 20) {
                    System.out.println("Skipping invalid row.");
                    continue;
                }

                crimerecord crime = new crimerecord();

                crime.setMurder(Integer.parseInt(row[0]));
                crime.setAttemptToMurder(Integer.parseInt(row[1]));
                crime.setRobbery(Integer.parseInt(row[2]));
                crime.setDacoity(Integer.parseInt(row[3]));
                crime.setTheft(Integer.parseInt(row[4]));
                crime.setHouseBreaking(Integer.parseInt(row[5]));
                crime.setVehicleTheft(Integer.parseInt(row[6]));
                crime.setAssault(Integer.parseInt(row[7]));
                crime.setCrimesAgainstWomen(Integer.parseInt(row[8]));
                crime.setCyberCrime(Integer.parseInt(row[9]));
                crime.setKidnapping(Integer.parseInt(row[10]));
                crime.setRape(Integer.parseInt(row[11]));
                crime.setMolestation(Integer.parseInt(row[12]));
                crime.setChainSnatching(Integer.parseInt(row[13]));
                crime.setPickpocketing(Integer.parseInt(row[14]));
                crime.setMissingPerson(Integer.parseInt(row[15]));
                crime.setCheating(Integer.parseInt(row[16]));
                crime.setDrugRelatedCrime(Integer.parseInt(row[17]));

                crime.setLatitude(Double.parseDouble(row[18]));
                crime.setLongitude(Double.parseDouble(row[19]));

                crimeRecordRepository.save(crime);

                importedCount++;
            }

            System.out.println(
                    "Crime data imported successfully: "
                            + importedCount
                            + " records."
            );
        }
    }
}