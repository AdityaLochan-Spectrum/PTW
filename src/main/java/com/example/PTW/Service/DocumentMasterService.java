package com.example.PTW.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PTW.Model.DocumentMaster;
import com.example.PTW.Repo.DocumentMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor          // generates constructor with repository param
public class DocumentMasterService {

    private final DocumentMasterRepository repo;

    /* Create or update */
    public DocumentMaster save(DocumentMaster doc) {
        return repo.save(doc);
    }

    /* Fetch by primary key */
    public DocumentMaster get(String documentKey) {
        return repo.findById(documentKey)
                   .orElseThrow(() ->
                       new IllegalArgumentException("Document not found: " + documentKey));
    }

    /* List all (consider pagination in real apps) */
    public List<DocumentMaster> list() {
        return repo.findAll();
    }

    /* Delete */
    public void delete(String documentKey) {
        repo.deleteById(documentKey);
    }
}

