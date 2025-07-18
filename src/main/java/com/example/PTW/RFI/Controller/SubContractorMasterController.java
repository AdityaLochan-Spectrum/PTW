package com.example.PTW.RFI.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PTW.RFI.Entity.SubContractorMaster;
import com.example.PTW.RFI.Service.SubContractorMasterService;

@RestController
@RequestMapping("/api/subcontractors")

public class SubContractorMasterController {
    @Autowired
    private  SubContractorMasterService controller;

     @GetMapping("/all")
    public List<SubContractorMaster> getAll() {
        return controller.findAll();
    }
}
