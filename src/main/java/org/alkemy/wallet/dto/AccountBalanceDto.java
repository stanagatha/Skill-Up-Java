package org.alkemy.wallet.dto;

import lombok.Data;
import org.alkemy.wallet.model.Currency;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class AccountBalanceDto implements Serializable {

    private Map<Currency, Double> balances;
    private List<FixedTermDepositDto> fixedTermDepositList;

}
