package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionDto;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);

    public List<TransactionDto> getAllByUser(long userId);
}
