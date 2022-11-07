package org.alkemy.wallet.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TRANSACTIONS")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "TYPE_TRANSACTION", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TypeTransaction typeTransaction;

    @Column(name = "DESCRIPT")
    private String descript;

    @Column(name = "TRANSACTION_DATE")
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private Account accountId;

}
