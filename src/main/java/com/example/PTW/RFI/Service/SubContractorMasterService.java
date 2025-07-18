package com.example.PTW.RFI.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.PTW.RFI.Entity.SubContractorMaster;
import com.example.PTW.RFI.Repositoary.SubContractorMasterRepository;
@Service
public class SubContractorMasterService {
    @Autowired
      private  SubContractorMasterRepository repository;

    public List<SubContractorMaster> findAll() {
        return repository.findAll();
    }
}
