package org.example.demo.Repository;

import org.example.demo.Entity.crimerecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrimeRecordRepository
        extends JpaRepository<crimerecord, Long> {
}