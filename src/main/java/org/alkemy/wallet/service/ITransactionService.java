package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);

}
