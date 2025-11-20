/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.exception.model;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

  String getCode();

  String getMessage();

  HttpStatus getStatus();
}
