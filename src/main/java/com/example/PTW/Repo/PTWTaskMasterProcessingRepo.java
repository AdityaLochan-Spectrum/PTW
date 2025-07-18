package com.example.PTW.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.PTW.Model.PTWTaskMasterProcessing;
import com.example.PTW.Model.PTWTaskMasterProcessingId;

public interface PTWTaskMasterProcessingRepo  extends JpaRepository<PTWTaskMasterProcessing, PTWTaskMasterProcessingId> {
     @Query(value = "SELECT * FROM PTWTaskMasterProcessing", nativeQuery = true)
    List<PTWTaskMasterProcessing> findAllNative();

    List<PTWTaskMasterProcessing> findByProcess(String process);
}
