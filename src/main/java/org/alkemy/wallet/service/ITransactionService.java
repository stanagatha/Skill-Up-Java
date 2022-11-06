package org.alkemy.wallet.service;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);

    public List<TransactionDto> getAllByUser(long userId);
}
