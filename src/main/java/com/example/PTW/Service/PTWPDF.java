package com.example.PTW.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PTWPDF {

     @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getPTWById(BigDecimal ptwId) {
        String sql = "SELECT * FROM PTWFullView WHERE PTWId = ?";
        return jdbcTemplate.queryForMap(sql, ptwId);
    }

    public List<Map<String, Object>> getAllPTWs() {
        return jdbcTemplate.queryForList("SELECT * FROM PTWFullView");
    }

}
