package org.alkemy.wallet.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class TransactionDto implements Serializable {

    private long id;
    private double amount;
    private String type_transaction;
    private String descrip;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date transactionDate;
}
