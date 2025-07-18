package com.example.PTW.RFI.Controller;


import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PTW.RFI.Service.RfiPdfService;
@RestController
@RequestMapping("/api/rfi")
@RequiredArgsConstructor
public class RfiPdfController {

    private final RfiPdfService service;

    /** JSON for a single RFI (e.g. /api/rfi/1234) */
    @GetMapping(value = "/view/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> one(@PathVariable Long id) {
        return service.findById(id);
    }

    /** JSON list of all RFIs (optional) */
    @GetMapping(value = "/viewall",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> all() {
        return service.findAll();
    }
}
