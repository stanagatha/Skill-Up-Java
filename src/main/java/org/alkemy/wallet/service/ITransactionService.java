package org.alkemy.wallet.service;

import org.alkemy.wallet.dto.TransactionDto;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);

   TransactionDto findById(Long id);

}
