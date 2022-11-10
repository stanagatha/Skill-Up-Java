package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.model.Currency;
import org.springframework.data.domain.Page;

public interface ITransactionService {

    TransactionDto save(TransactionRequestDto transaction);

    Page<TransactionDto> getAllByUser(long userId, Integer pageNumber);

    TransactionDto send(TransactionSendMoneyDto transactionSendMoneyDto, Currency currency);

    TransactionDto edit(long id, String description);

    TransactionDto findById(Long id);

}
