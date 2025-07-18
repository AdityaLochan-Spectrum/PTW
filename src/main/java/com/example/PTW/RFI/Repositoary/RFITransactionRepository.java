package com.example.PTW.RFI.Repositoary;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PTW.RFI.Entity.RFITransaction;




public interface RFITransactionRepository extends JpaRepository<RFITransaction, Long>{
    
    List<RFITransaction> findByDocStatus(String docStatus);

    List<RFITransaction> findByPendingWith(String pendingWith);


    

}
