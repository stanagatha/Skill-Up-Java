package org.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FixedTermDepositSimulateDto implements Serializable {

    private Double amount;
    private Double interest;
    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date closingDate;
    private Double totalAmount;
}
