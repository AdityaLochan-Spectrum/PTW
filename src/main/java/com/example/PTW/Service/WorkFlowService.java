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
public class WorkFlowService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // public void insertWorkflowProgression(BigDecimal PTWId, String loginNumber) {
    // // First get the next available tracking_sr_no
    // BigDecimal nextTrackingNo = jdbcTemplate.queryForObject(
    // "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM
    // [qbo].WorkflowProgressionTracking",
    // BigDecimal.class);

    // String sql = "INSERT INTO [qbo].WorkflowProgressionTracking " +
    // "(wpt_tracking_sr_no, wpt_wf_id, wpt_process_id, wpt_start_date, wpt_EDC, " +
    // "wpt_input_key1, wpt_input_key2, wpt_input_key3, wpt_input_value1, " +
    // "wpt_input_value2, wpt_input_value3, wpt_lead_time, wpt_prime_resp_employee,
    // " +
    // "wpt_secondary_resp_employee, wpt_reference_employee, wpt_dim1, wpt_dim2, " +
    // "wpt_dim3, wpt_process_description, wpt_wf_description, wpt_wf_EDC, " +
    // "wm_version, wm_revision, wpt_process_version, wpt_ReminderDate, " +
    // "wpt_EscalationDate, wpt_Priority, wpt_TransactionError_Ind) " +
    // "VALUES (?, 'W000170', 'P000730', GETDATE(), GETDATE(), " +
    // "'PTWId', '', '', ?, " + // PTWId value
    // "'', '', 10.00, 'qbuser', NULL, 'qbuser', " + // Changed
    // wpt_secondary_resp_employee to NULL
    // "'Bank', 'West', 'quickbpm', " + // Dimension values
    // "'Task Plan', 'PTWTaskTemplate', " +
    // "GETDATE(), 1, 1, 1, " +
    // "NULL, NULL, 1, NULL)";

    // // Execute the SQL with parameters
    // jdbcTemplate.update(sql, nextTrackingNo, PTWId);

    // System.out.println("Inserted record into WorkflowProgressionTracking with: "
    // +
    // "\nTracking No: " + nextTrackingNo +
    // "\nPTWId: " + PTWId +
    // "\nDimensions: Bank, West, quickbpm" +
    // "\nSecondary Resp Employee: NULL");
    // }

    public void insertWorkflowProgression(BigDecimal PTWId, String loginNumber) {
        try {
            // 1. Get next tracking number
            BigDecimal nextTrackingNo = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(wpt_tracking_sr_no), 0) + 1 FROM [qbo].WorkflowProgressionTracking",
                BigDecimal.class
            );
    
            if (nextTrackingNo == null) {
                throw new IllegalStateException("Failed to retrieve next tracking number.");
            }
    
            // 2. Fetch dimension values
            String dimensionSql = "SELECT Dimension1, Dimension2, Dimension3 FROM [qbo].EmployeeMaster WHERE em_emp_code = ?";
            Map<String, Object> dimResult = jdbcTemplate.queryForMap(dimensionSql, loginNumber);
    
            String dim1 = dimResult.get("Dimension1") != null ? dimResult.get("Dimension1").toString() : null;
            String dim2 = dimResult.get("Dimension2") != null ? dimResult.get("Dimension2").toString() : null;
            String dim3 = dimResult.get("Dimension3") != null ? dimResult.get("Dimension3").toString() : null;
    
            // Validate dimensions
            if (dim1 == null || dim2 == null || dim3 == null) {
                throw new IllegalArgumentException("One or more dimension values are missing for employee: " + loginNumber);
            }
    
            // 3. Insert query with exactly 7 parameter placeholders
            String sql = "INSERT INTO [qbo].WorkflowProgressionTracking " +
                    "(wpt_tracking_sr_no, wpt_wf_id, wpt_process_id, wpt_start_date, wpt_EDC, " +
                    "wpt_input_key1, wpt_input_key2, wpt_input_key3, wpt_input_value1, " +
                    "wpt_input_value2, wpt_input_value3, wpt_lead_time, wpt_prime_resp_employee, " +
                    "wpt_secondary_resp_employee, wpt_reference_employee, wpt_dim1, wpt_dim2, " +
                    "wpt_dim3, wpt_process_description, wpt_wf_description, wpt_wf_EDC, " +
                    "wm_version, wm_revision, wpt_process_version, wpt_ReminderDate, " +
                    "wpt_EscalationDate, wpt_Priority, wpt_TransactionError_Ind) " +
                    "VALUES (?, 'W000170', 'P000730', GETDATE(), GETDATE(), " +
                    "'PTWId', '', '', ?, '', '', 10.00, ?, NULL, ?, ?, ?, ?, " +
                    "'Task Plan', 'PTWTaskTemplate', GETDATE(), 1, 1, 1, NULL, NULL, 1, NULL)";
    
            // 4. Parameters (match 7 placeholders)
            int rows = jdbcTemplate.update(sql,
                nextTrackingNo,       // 1: wpt_tracking_sr_no
                PTWId,                // 2: wpt_input_value1
                loginNumber,          // 3: wpt_prime_resp_employee
                loginNumber,          // 4: wpt_reference_employee
                dim1,                 // 5: wpt_dim1
                dim2,                 // 6: wpt_dim2
                dim3                  // 7: wpt_dim3
            );
    
            // 5. Logging
            if (rows > 0) {
                System.out.println("✅ Inserted WorkflowProgressionTracking with:\n" +
                    "Tracking No: " + nextTrackingNo +
                    "\nPTWId: " + PTWId +
                    "\nPrime: " + loginNumber +
                    "\nRef: " + loginNumber +
                    "\nDims: " + dim1 + ", " + dim2 + ", " + dim3);
            } else {
                System.err.println("⚠️ Insert failed: no rows affected.");
            }
    
        } catch (Exception e) {
            System.err.println("❌ Error inserting WorkflowProgressionTracking: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to insert WorkflowProgressionTracking", e);
        }
    }
       
    public void insertWorkflowTranTrack(BigDecimal PTWId, String loginNumber) {
        try {
            // 1. Format current date-time for Workflow_StartDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String workflowStartDate = LocalDateTime.now().format(formatter);
    
            // 2. Fetch dimensions from EmployeeMaster
            String dimensionSql = "SELECT Dimension1, Dimension2, Dimension3 FROM [qbo].EmployeeMaster WHERE em_emp_code = ?";
            Map<String, Object> dimResult = jdbcTemplate.queryForMap(dimensionSql, loginNumber);
    
            String dim1 = dimResult.get("Dimension1") != null ? dimResult.get("Dimension1").toString() : null;
            String dim2 = dimResult.get("Dimension2") != null ? dimResult.get("Dimension2").toString() : null;
            String dim3 = dimResult.get("Dimension3") != null ? dimResult.get("Dimension3").toString() : null;
    
            if (dim1 == null || dim2 == null || dim3 == null) {
                throw new IllegalArgumentException("One or more dimensions are missing for employee: " + loginNumber);
            }
    
            // 3. Prepare insert query
            String sql = """
                INSERT INTO qbo.WorkflowTranTrack (
                    wf_id, InputKey1Name, InputKey1Value, InputKey2Name, InputKey2Value,
                    InputKey3Name, InputKey3Value, Workflow_ID, Reference_Employee,
                    Dimension1, Dimension2, Dimension3, Status, Workflow_StartDate, TimeZone
                ) VALUES (
                    NULL, 'PTWId', ?, '', '', '', '', 'W000170', ?,
                    ?, ?, ?, 'CLOSED', ?, 'India Standard Time'
                )
            """;
    
            // 4. Execute insert with parameters
            jdbcTemplate.update(sql,
                PTWId,           // InputKey1Value
                loginNumber,     // Reference_Employee
                dim1,            // Dimension1
                dim2,            // Dimension2
                dim3,            // Dimension3
                workflowStartDate // Workflow_StartDate
            );
    
            System.out.println("✅ Inserted into WorkflowTranTrack:\n" +
                "PTWId: " + PTWId +
                "\nReference_Employee: " + loginNumber +
                "\nDimensions: " + dim1 + ", " + dim2 + ", " + dim3 +
                "\nStartDate: " + workflowStartDate);
        } catch (Exception e) {
            System.err.println("❌ Error inserting into WorkflowTranTrack: " + e.getMessage());
            throw new RuntimeException("WorkflowTranTrack insert failed", e);
        }
    }
    

    // public void insertWorkflowTranTrack(BigDecimal PTWId) {

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy
    // HH:mm:ss");
    // String workflowStartDate = LocalDateTime.now().format(formatter); // or use
    // your own LocalDateTime

    // String sql = "INSERT INTO qbo.WorkflowTranTrack " +
    // "(wf_id, InputKey1Name, InputKey1Value, InputKey2Name, InputKey2Value, " +
    // "InputKey3Name, InputKey3Value, Workflow_ID, Reference_Employee, " +
    // "Dimension1, Dimension2, Dimension3, Status, Workflow_StartDate, TimeZone) "
    // +
    // "VALUES ('', 'PTWId', ?, '', '', '', '', 'W000170', 'qbuser', ?, ?, ?, " +
    // "'CLOSED', ?, 'India Standard Time')";

    // // Execute the SQL with parameters
    // jdbcTemplate.update(sql, PTWId, "Bank", "West", "quickbpm",
    // workflowStartDate);

    // System.out.println("Inserted record into WorkflowTranTrack with UEventID: " +
    // PTWId);

    // }
}
