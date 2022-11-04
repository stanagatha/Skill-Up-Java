package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.model.FixedTermDeposit;

public interface IFixedTermDepositService {

    FixedTermDeposit save(FixedTermDeposit fixedTermDeposit);
    FixedTermDepositDto createDeposit(FixedTermDepositRequestDto depositRequestDto);

}
