package org.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class TransactionDto implements Serializable {

    private Long id;
    private Double amount;
    private String typeTransaction;
    private String descript;
    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date transactionDate;
}
