package com.example.PTW.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PTW.Model.TemplateMaster;
import com.example.PTW.Repo.TemplateMasterRepo;

@Service
public class TemplateService {
    @Autowired
    private  TemplateMasterRepo templateRepository;

    public List<TemplateMaster> getTemplatesByCode(String templateCode) {
        return templateRepository.findByTemplateCode(templateCode);
    }

    public List<TemplateMaster> getAllTemplates() {
        return templateRepository.findAll();
    }
}

