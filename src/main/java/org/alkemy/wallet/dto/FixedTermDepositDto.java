package org.alkemy.wallet.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FixedTermDepositDto implements Serializable {

    private Long fixedTermDepositsId;
    private Double amount;
    private User userId;
    private Account accountId;
    private Double interest;
    private Date creationDate;
    private Date closingDate;

}
