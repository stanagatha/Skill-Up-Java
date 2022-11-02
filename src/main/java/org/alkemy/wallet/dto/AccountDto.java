package org.alkemy.wallet.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class AccountDto {

  private long id;
  private String currency;
  private double transactionLimit;
  private double balance;
  //User field.
  private Date creationDate;
  private Date updDate;
  private Boolean softDelete;

}
