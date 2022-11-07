package org.alkemy.wallet.service;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.model.Currency;
import org.springframework.transaction.annotation.Transactional;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);

    public List<TransactionDto> getAllByUser(long userId);

    @Transactional
    TransactionDto send(TransactionSendMoneyDto transactionSendMoneyDto, Currency currency);
}
