package org.alkemy.wallet.service;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
<<<<<<< HEAD
=======
import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Transaction;
>>>>>>> b4b8dbf (add getAll method)

public interface ITransactionService {

    public Transaction save(Transaction transaction);
    public TransactionDto getAll(long userId);

    public List<TransactionDto> getAllByUser(long userId);
}
