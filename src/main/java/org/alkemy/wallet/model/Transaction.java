package org.alkemy.wallet.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "AMOUNT", nullable = false)
    private double amount;
    @Column(name = "TYPE_TRANSACTION", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private String type_transaction;
    @Column(name = "DESCRIP", nullable = true)
    private String descrip;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    public Transaction() {
    }

    public Transaction(long id, double amount, String type_transaction, String descrip, Date transactionDate) {
        this.id = id;
        this.amount = amount;
        this.type_transaction = type_transaction;
        this.descrip = descrip;
        this.transactionDate = transactionDate;
    }
}
