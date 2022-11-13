package org.alkemy.wallet.dto;

import lombok.Data;
import org.alkemy.wallet.model.TypeTransaction;

@Data
public class TransactionRequestDto {

    private Double amount;
    private String description;
    private Long accountId;
    private TypeTransaction typeTransaction;

}
