package com.example.PTW.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.PTW.Model.RoleMappings;

@Service
public class WFSPlanToIssuer {
         @Autowired
    private JdbcTemplate jdbcTemplate;
 
    
// public void insertWorkflowProgression(BigDecimal PTWId) {
//     // First get the next available tracking_sr_no
//     BigDecimal nextTrackingNo = jdbcTemplate.queryForObject(
//         "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking", 
//         BigDecimal.class);

//         String sql =
//         "INSERT INTO [qbo].WorkflowProgressionTracking " +
//         "(wpt_tracking_sr_no, wpt_wf_id, wpt_process_id, wpt_start_date, wpt_EDC, " +
//         " wpt_input_key1, wpt_input_key2, wpt_input_key3, wpt_input_value1, " +
//         " wpt_input_value2, wpt_input_value3, wpt_lead_time, wpt_prime_resp_employee, " +
//         " wpt_secondary_resp_employee, wpt_reference_employee, wpt_dim1, wpt_dim2, " +
//         " wpt_dim3, wpt_process_description, wpt_wf_description, wpt_wf_EDC, " +
//         " wm_version, wm_revision, wpt_process_version, wpt_ReminderDate, " +
//         " wpt_EscalationDate, wpt_Priority, wpt_TransactionError_Ind) " +
//         "VALUES ( ?, 'W000170', 'P000759', GETDATE(), GETDATE(), " +  
//         "        'PTWId', '', '',  ?, " +                              
//         "        '', '', 10.00, 'admin', NULL, 'qbuser', " +
//         "        'Bank', 'West', 'quickbpm', " +
//         "        'PTW Issuer', 'PTWTaskTemplate', " +
//         "        NULL, 1, 1, 1, " +
//         "        NULL, NULL, 1, 'E')";
    

//     // Execute the SQL with parameters
//     jdbcTemplate.update(sql, nextTrackingNo, PTWId);

//     System.out.println("Inserted record into WorkflowProgressionTracking with: " +
//                       "\nTracking No: " + nextTrackingNo +
//                       "\nPTWId: " + PTWId +
//                       "\nDimensions: Bank, West, quickbpm" +
//                       "\nSecondary Resp Employee: NULL");
// }
// public void insertWorkflowProgression(BigDecimal PTWId, String loginNumber) {
//     try {
//         // 1. Get next tracking number
//         BigDecimal nextTrackingNo = jdbcTemplate.queryForObject(
//             "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking", 
//             BigDecimal.class
//         );

//         if (nextTrackingNo == null) {
//             throw new IllegalStateException("Failed to fetch next tracking number.");
//         }

//         // 2. Fetch dimension values from EmployeeMaster
//         String dimensionSql = "SELECT Dimension1, Dimension2, Dimension3 FROM [qbo].EmployeeMaster WHERE em_emp_code = ?";
//         Map<String, Object> dimResult = jdbcTemplate.queryForMap(dimensionSql, loginNumber);

//         String dim1 = dimResult.get("Dimension1") != null ? dimResult.get("Dimension1").toString() : null;
//         String dim2 = dimResult.get("Dimension2") != null ? dimResult.get("Dimension2").toString() : null;
//         String dim3 = dimResult.get("Dimension3") != null ? dimResult.get("Dimension3").toString() : null;

//         if (dim1 == null || dim2 == null || dim3 == null) {
//             throw new IllegalArgumentException("One or more dimension values are missing for employee: " + loginNumber);
//         }

//         // 3. Determine wpt_prime_resp_employee and wpt_reference_employee
//         String wptPrimeRespEmployee;
//         String wptReferenceEmployee;

//         if ("8766425964".equals(loginNumber)) {
//             wptPrimeRespEmployee = "9908442275";
//             wptReferenceEmployee = "8766425964";
//         } else {
//             wptPrimeRespEmployee = "8766425964";
//             wptReferenceEmployee = "9597161175";
//         }

//         // 4. Prepare insert query
//         String sql =
//             "INSERT INTO [qbo].WorkflowProgressionTracking " +
//             "(wpt_tracking_sr_no, wpt_wf_id, wpt_process_id, wpt_start_date, wpt_EDC, " +
//             " wpt_input_key1, wpt_input_key2, wpt_input_key3, wpt_input_value1, " +
//             " wpt_input_value2, wpt_input_value3, wpt_lead_time, wpt_prime_resp_employee, " +
//             " wpt_secondary_resp_employee, wpt_reference_employee, wpt_dim1, wpt_dim2, " +
//             " wpt_dim3, wpt_process_description, wpt_wf_description, wpt_wf_EDC, " +
//             " wm_version, wm_revision, wpt_process_version, wpt_ReminderDate, " +
//             " wpt_EscalationDate, wpt_Priority, wpt_TransactionError_Ind) " +
//             "VALUES (?, 'W000170', 'P000759', GETDATE(), GETDATE(), " +
//             "        'PTWId', '', '', ?, '', '', 10.00, ?, NULL, ?, ?, ?, ?, " +
//             "        'PTW Issuer', 'PTWTaskTemplate', NULL, 1, 1, 1, NULL, NULL, 1, 'E')";

//         // 5. Execute the SQL with parameters
//         jdbcTemplate.update(sql,
//             nextTrackingNo,        // 1: wpt_tracking_sr_no
//             PTWId,                 // 2: wpt_input_value1
//             wptPrimeRespEmployee,  // 3: wpt_prime_resp_employee
//             wptReferenceEmployee,  // 4: wpt_reference_employee
//             dim1,                  // 5: wpt_dim1
//             dim2,                  // 6: wpt_dim2
//             dim3                   // 7: wpt_dim3
//         );

//         // 6. Log success
//         System.out.println("✅ Inserted WorkflowProgressionTracking with:\n" +
//             "Tracking No: " + nextTrackingNo +
//             "\nPTWId: " + PTWId +
//             "\nPrime Resp: " + wptPrimeRespEmployee +
//             "\nReference: " + wptReferenceEmployee +
//             "\nDims: " + dim1 + ", " + dim2 + ", " + dim3
//         );

//     } catch (Exception e) {
//         System.err.println("❌ Error inserting WorkflowProgressionTracking: " + e.getMessage());
//         e.printStackTrace();
//         throw new RuntimeException("Failed to insert WorkflowProgressionTracking", e);
//     }
// }


public void insertWorkflowProgression(BigDecimal PTWId, String loginNumber) {
    try {
        // 1. Get next tracking number
        BigDecimal nextTrackingNo = jdbcTemplate.queryForObject(
            "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking",
            BigDecimal.class
        );

        if (nextTrackingNo == null) {
            throw new IllegalStateException("Failed to fetch next tracking number.");
        }

        // 2. Fetch dimension values from EmployeeMaster
        String dimensionSql = "SELECT Dimension1, Dimension2, Dimension3 FROM [qbo].EmployeeMaster WHERE em_emp_code = ?";
        Map<String, Object> dimResult = jdbcTemplate.queryForMap(dimensionSql, loginNumber);

        String dim1 = dimResult.get("Dimension1") != null ? dimResult.get("Dimension1").toString() : null;
        String dim2 = dimResult.get("Dimension2") != null ? dimResult.get("Dimension2").toString() : null;
        String dim3 = dimResult.get("Dimension3") != null ? dimResult.get("Dimension3").toString() : null;

        if (dim1 == null || dim2 == null || dim3 == null) {
            throw new IllegalArgumentException("One or more dimension values are missing for employee: " + loginNumber);
        }

        // 3. Determine responsible employees using RoleMappings
        String wptPrimeRespEmployee = RoleMappings.getIssuerForInitiator(loginNumber);
        String wptReferenceEmployee = loginNumber;

        // 4. Prepare insert SQL
        String sql =
            "INSERT INTO [qbo].WorkflowProgressionTracking " +
            "(wpt_tracking_sr_no, wpt_wf_id, wpt_process_id, wpt_start_date, wpt_EDC, " +
            " wpt_input_key1, wpt_input_key2, wpt_input_key3, wpt_input_value1, " +
            " wpt_input_value2, wpt_input_value3, wpt_lead_time, wpt_prime_resp_employee, " +
            " wpt_secondary_resp_employee, wpt_reference_employee, wpt_dim1, wpt_dim2, " +
            " wpt_dim3, wpt_process_description, wpt_wf_description, wpt_wf_EDC, " +
            " wm_version, wm_revision, wpt_process_version, wpt_ReminderDate, " +
            " wpt_EscalationDate, wpt_Priority, wpt_TransactionError_Ind) " +
            "VALUES (?, 'W000170', 'P000759', GETDATE(), GETDATE(), " +
            "        'PTWId', '', '', ?, '', '', 10.00, ?, NULL, ?, ?, ?, ?, " +
            "        'PTW Issuer', 'PTWTaskTemplate', NULL, 1, 1, 1, NULL, NULL, 1, 'E')";

        // 5. Execute insert
        jdbcTemplate.update(sql,
            nextTrackingNo,
            PTWId,
            wptPrimeRespEmployee,
            wptReferenceEmployee,
            dim1,
            dim2,
            dim3
        );

        // 6. Log success
        System.out.println("✅ Inserted WorkflowProgressionTracking with:\n" +
            "Tracking No: " + nextTrackingNo +
            "\nPTWId: " + PTWId +
            "\nPrime Resp: " + wptPrimeRespEmployee +
            "\nReference: " + wptReferenceEmployee +
            "\nDims: " + dim1 + ", " + dim2 + ", " + dim3
        );

    } catch (Exception e) {
        System.err.println("❌ Error inserting WorkflowProgressionTracking: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Failed to insert WorkflowProgressionTracking", e);
    }
}




//    
}
