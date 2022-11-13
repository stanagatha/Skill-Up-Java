package org.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.alkemy.wallet.model.TypeTransaction;

import java.io.Serializable;
import java.util.Date;

@Data
public class TransactionDto implements Serializable {

    private Long id;
    private Double amount;
    private TypeTransaction typeTransaction;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date transactionDate;
    private Long accountId;

}
