package org.alkemy.wallet.model;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "CURRENCY", nullable = false)
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Column(name = "TRANSACTION_LIMIT", nullable = false)
  private Double transactionLimit;

  @Column(name = "BALANCE", nullable = false)
  private Double balance;

  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable= false)
  private User user;

  @Column(name = "CREATION_DATE")
  private Date creationDate;

  @Column(name = "UPDATE_DATE")
  private Date updateDate;

  @Column(name = "SOFT_DELETE")
  private Boolean softDelete;

}
