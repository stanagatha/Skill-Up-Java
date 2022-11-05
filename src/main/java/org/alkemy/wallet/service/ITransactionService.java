package org.alkemy.wallet.service;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);

    public TransactionDto getAll(long userId);
}
