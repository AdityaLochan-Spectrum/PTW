package com.example.PTW.Repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PTW.Model.PTWTaskMaster;

public interface PtwmasterRepo extends JpaRepository<PTWTaskMaster,BigDecimal>{

    List<PTWTaskMaster> findByPendingWith(String pendingWith);

    @Query(value = """
        SELECT TOP 1 ptw.*
        FROM PTWTaskMaster ptw
        JOIN qbo.WorkflowProgressionTracking wpt 
            ON wpt.wpt_input_value1 = ptw.PTWId
        WHERE wpt.wpt_ADC IS NULL 
          AND wpt.wpt_input_key1 = 'PTWId'
          AND ptw.PTWId = :ptwId
          AND wpt.wpt_prime_resp_employee = :employee
        ORDER BY wpt.wpt_tracking_sr_no ASC
        """, nativeQuery = true)
    Optional<PTWTaskMaster> findTopPendingTaskByPTWIdAndEmployee(
        @Param("ptwId") Integer ptwId,
        @Param("employee") String employee
    );

}
