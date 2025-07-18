package com.example.PTW.exceptions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends  JpaRepository<ErrorLog,Long> {

}
