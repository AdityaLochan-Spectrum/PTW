package com.example.PTW.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PTW.Model.TemplateMaster;

public interface TemplateMasterRepo extends JpaRepository<TemplateMaster,Long>{
    List<TemplateMaster> findByTemplateCode(String templateCode);

    List<TemplateMaster> findAll();
}
