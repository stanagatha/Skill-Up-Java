package org.alkemy.wallet.service.impl;

import org.alkemy.wallet.model.FixedTermDeposit;
import org.alkemy.wallet.repository.IFixedTermDepositRepository;
import org.alkemy.wallet.service.IFixedTermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FixedTermDepositImpl implements IFixedTermDepositService {

    private final IFixedTermDepositRepository iFixedTermDepositRepository;

    @Autowired
    public FixedTermDepositImpl(IFixedTermDepositRepository iFixedTermDepositRepository) {
        this.iFixedTermDepositRepository = iFixedTermDepositRepository;
    }

    @Override
    public void saveFixedDeposit(FixedTermDeposit fixedTermDeposit){
        iFixedTermDepositRepository.save(fixedTermDeposit);
    }

}
