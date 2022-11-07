package org.alkemy.wallet.service;

import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.model.Currency;
import org.springframework.transaction.annotation.Transactional;

public interface ITransactionService {

    public TransactionDto save(TransactionDto transaction);
    public List<TransactionDto> getAllByUser(long userId);
<<<<<<< HEAD
    public TransactionDto edit(long userId, long id, String desctiption);
=======


    @Transactional
    TransactionDto send(TransactionSendMoneyDto transactionSendMoneyDto, Currency currency);

    public TransactionDto edit(long userId, long id, String description);
>>>>>>> 2354b035b8729c99d70906bcc933d61edbdbf245

}
