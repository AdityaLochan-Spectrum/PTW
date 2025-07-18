package com.example.PTW.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PTW.Model.DocumentMaster;

public interface DocumentMasterRepository extends JpaRepository<DocumentMaster, String> {
    
}
