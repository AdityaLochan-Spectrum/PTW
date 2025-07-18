package com.example.PTW.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.PTW.Model.TemplateMaster;
import com.example.PTW.Service.TemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    @Autowired
    private  TemplateService templateService;

    
    @GetMapping("/getAll")
    public List<TemplateMaster> getAllTemplateMasters() {
        return templateService.getAllTemplates();
    }

    @GetMapping("/by-code/{templateCode}")
    public List<TemplateMaster> getTemplatesByCode(@PathVariable String templateCode) {
        return templateService.getTemplatesByCode(templateCode);
    }
}
