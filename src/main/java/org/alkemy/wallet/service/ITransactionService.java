package org.alkemy.wallet.service;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.model.Currency;
import org.springframework.transaction.annotation.Transactional;

public interface ITransactionService {

    TransactionDto save(TransactionRequestDto transaction);

    List<TransactionDto> getAllByUser(long userId);

    TransactionDto send(TransactionSendMoneyDto transactionSendMoneyDto, Currency currency);

    TransactionDto edit(long id, String description);

    TransactionDto findById(Long id);

}
