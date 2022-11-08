package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.dto.FixedTermDepositRequestDto;
import org.alkemy.wallet.dto.FixedTermDepositSimulateDto;
import org.alkemy.wallet.model.FixedTermDeposit;

public interface IFixedTermDepositService {

    void saveFixedDeposit(FixedTermDeposit fixedTermDeposit);

    FixedTermDepositDto createDeposit(FixedTermDepositRequestDto depositRequestDto);

    FixedTermDepositSimulateDto simulateDeposit(FixedTermDepositRequestDto depositRequestDto);
}
