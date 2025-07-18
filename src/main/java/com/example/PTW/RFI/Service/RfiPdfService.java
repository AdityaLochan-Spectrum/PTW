package com.example.PTW.RFI.Service;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class RfiPdfService {

    private final JdbcTemplate jdbc;

    /** One row – key = column name, value = column value */
 public Map<String, Object> findById(Long rfiId) {
    String sql = "SELECT * FROM dbo.fullviewRFIPDF WHERE RFIID = ?";
    try {
        return jdbc.queryForMap(sql, rfiId);
    } catch (EmptyResultDataAccessException e) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "RFIID " + rfiId + " not found");
    } catch (DataAccessException e) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "SQL Error: " + e.getMessage());
    }
}


    /** All rows (use with caution if the table is large) */
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM dbo.fullviewRFIPDF");
    }
}
