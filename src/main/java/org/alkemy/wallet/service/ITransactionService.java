package org.alkemy.wallet.service;

import org.alkemy.wallet.model.Transaction;

public interface ITransactionService {

    public Transaction save(Transaction transaction);
}
