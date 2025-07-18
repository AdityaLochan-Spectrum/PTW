package com.example.PTW.RFI.Repositoary;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PTW.RFI.Entity.RFIProcessing;
import com.example.PTW.RFI.Entity.RfiRemarkId;

import jakarta.persistence.LockModeType;





@Repository
public interface RFIProcessingRepository extends JpaRepository<RFIProcessing, RfiRemarkId> {
       @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select max(p.id.remarkSerialNo) from RFIProcessing p where p.id.rfiId = :rfiId")
    Long findMaxSerialForUpdate(@Param("rfiId") Long rfiId);


    List<RFIProcessing> findByRfiTransaction_PendingWith(String pendingWith);
    

}
