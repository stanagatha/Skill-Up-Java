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
        fixedTermDeposit.setId(fixedTermDepositDto.getId());
        fixedTermDeposit.setAmount(fixedTermDepositDto.getAmount());
        fixedTermDeposit.setInterest(fixedTermDepositDto.getInterest());
        fixedTermDeposit.setCreationDate(fixedTermDepositDto.getCreationDate());
        fixedTermDeposit.setClosingDate(fixedTermDepositDto.getClosingDate());

        return fixedTermDeposit;
    }

    public FixedTermDepositDto fixedTermDepositToFixedTermDepositDto(FixedTermDeposit fixedTermDeposit) {
        if (fixedTermDeposit == null )
            return null;

        FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();
        fixedTermDepositDto.setId(fixedTermDeposit.getId());
        fixedTermDepositDto.setAmount(fixedTermDeposit.getAmount());
        fixedTermDepositDto.setInterest(fixedTermDeposit.getInterest());
        fixedTermDepositDto.setCreationDate(fixedTermDeposit.getCreationDate());
        fixedTermDepositDto.setClosingDate(fixedTermDeposit.getClosingDate());

        return fixedTermDepositDto;
    }

}
