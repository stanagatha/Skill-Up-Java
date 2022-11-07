package org.alkemy.wallet.dto;

import lombok.Data;

@Data
public class TransactionSendMoneyDto {
    private Double amount;
    private String descript;
    private Long destinationAccountId;
}
