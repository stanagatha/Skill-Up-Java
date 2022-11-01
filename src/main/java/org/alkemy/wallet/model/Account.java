package org.alkemy.wallet.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private String currency;
  @Column(name = "transaction_limit", nullable = false)
  private double transactionLimit;
  @Column(nullable = false)
  private double balance;
  //User field.
  @Column(name = "creation_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;
  @Column(name = "update_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updDate;
  @Column(name = "status")
  private Boolean softDelete;

}
