package org.alkemy.wallet.dto;

import lombok.Data;
import org.alkemy.wallet.model.Currency;

import java.util.Date;

@Data
public class FixedTermDepositRequestDto {
    private Double amount;
    private Date closingDate;
    private Currency currency;
}