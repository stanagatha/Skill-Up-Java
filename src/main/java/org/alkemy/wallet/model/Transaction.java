package org.alkemy.wallet.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "TRANSACTIONS")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TRANSACTION_ID")
    private long transactionId;
    @Column(name = "AMOUNT", nullable = false)
    private double amount;
    @Column(name = "TYPE_TRANSACTION", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TypeTransaction type_transaction;
    @Column(name = "DESCRIP", nullable = true)
    private String descrip;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account accountId;
    public Transaction() {
    }

    public Transaction(long id, double amount, TypeTransaction type_transaction, String descrip, Date transactionDate) {
        this.id = id;
        this.amount = amount;
        this.type_transaction = type_transaction;
        this.descrip = descrip;
        this.transactionDate = transactionDate;
    }
}
