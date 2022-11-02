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
    private TypeTransaction typeTransaction;
    @Column(name = "DESCRIPT", nullable = true)
    private String descript;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account accountId;
    public Transaction() {
    }

    public Transaction(long tansactionId, double amount, TypeTransaction type_transaction, String descript, Date transactionDate) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.typeTransaction = typeTransaction;
        this.descript = descript;
        this.transactionDate = transactionDate;
    }
}
