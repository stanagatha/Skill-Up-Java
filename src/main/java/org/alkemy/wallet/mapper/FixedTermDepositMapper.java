package org.alkemy.wallet.mapper;

import org.alkemy.wallet.dto.FixedTermDepositDto;
import org.alkemy.wallet.model.FixedTermDeposit;
import org.springframework.stereotype.Component;

@Component
public class FixedTermDepositMapper {

    public FixedTermDeposit fixedTermDepositDtoToFixedTermDeposit(FixedTermDepositDto fixedTermDepositDto) {
        if (fixedTermDepositDto == null)
            return null;

        FixedTermDeposit fixedTermDeposit = new FixedTermDeposit();
        fixedTermDeposit.setFixedTermDepositsId(fixedTermDepositDto.getFixedTermDepositsId());
        fixedTermDeposit.setAmount(fixedTermDepositDto.getAmount());
        fixedTermDeposit.setUserId(fixedTermDepositDto.getUserId());
        fixedTermDeposit.setAccountId(fixedTermDepositDto.getAccountId());
        fixedTermDeposit.setInterest(fixedTermDepositDto.getInterest());
        fixedTermDeposit.setCreationDate(fixedTermDepositDto.getCreationDate());
        fixedTermDeposit.setClosingDate(fixedTermDepositDto.getClosingDate());

        return fixedTermDeposit;
    }

    public FixedTermDepositDto fixedTermDepositToFixedTermDepositDto(FixedTermDeposit fixedTermDeposit) {
        if (fixedTermDeposit == null )
            return null;

        FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();
        fixedTermDepositDto.setFixedTermDepositsId(fixedTermDeposit.getFixedTermDepositsId());
        fixedTermDepositDto.setAmount(fixedTermDeposit.getAmount());
        fixedTermDepositDto.setUserId(fixedTermDeposit.getUserId());
        fixedTermDepositDto.setAccountId(fixedTermDeposit.getAccountId());
        fixedTermDepositDto.setInterest(fixedTermDeposit.getInterest());
        fixedTermDepositDto.setCreationDate(fixedTermDeposit.getCreationDate());
        fixedTermDepositDto.setClosingDate(fixedTermDeposit.getClosingDate());

        return fixedTermDepositDto;
    }

}
