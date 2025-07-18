package com.example.PTW.Controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PTW.Service.PTWPDF;

@RestController
@RequestMapping("/api/ptw-view")
public class PTWFullViewPDF {
    @Autowired
    private PTWPDF viewService;

     @GetMapping("/getBy/{ptwId}")
    public ResponseEntity<Map<String, Object>> getByPtwId(@PathVariable BigDecimal ptwId) {
        try {
            Map<String, Object> result = viewService.getPTWById(ptwId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(viewService.getAllPTWs());
    }

}
