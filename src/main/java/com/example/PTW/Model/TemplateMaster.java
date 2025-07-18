package com.example.PTW.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table
public class TemplateMaster {
  @Id
    @Column(name = "TemplateId", nullable = false, precision = 9, scale = 0)
    private Long templateId;

    @Column(name = "TemplateCode", nullable = false, length = 50)
    private String templateCode;
}

