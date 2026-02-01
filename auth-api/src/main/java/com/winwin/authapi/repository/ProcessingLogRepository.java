package com.winwin.authapi.repository;

import com.winwin.authapi.model.ProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessingLogRepository extends JpaRepository<ProcessingLog, UUID> {
}
