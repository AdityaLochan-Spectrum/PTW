package com.example.PTW.RFI.Service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ApprovalUpdater {

    private final JdbcTemplate jdbc;

    private static final String SQL = """
            UPDATE [qbo].WorkflowProgressionTracking
               SET wpt_output1 = ?,
                   wpt_output2 = '',
                   wpt_output3 = '',
                   wpt_ADC     = GETDATE(),
                   wpt_DelayRemarks   = '',
                   wpt_DelayReasonCode= ''
             WHERE wpt_process_id   = ?
               AND wpt_input_key1   = 'RFIID'
               AND wpt_input_value1 = ?
            """;

    public void apply(String processId, String output1, Long rfiId) {
        jdbc.update(SQL, output1, processId, rfiId);
    }
}