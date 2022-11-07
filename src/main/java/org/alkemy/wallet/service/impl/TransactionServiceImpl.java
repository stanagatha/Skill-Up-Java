package org.alkemy.wallet.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
<<<<<<< HEAD
import org.alkemy.wallet.exception.BadRequestException;
=======
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.exception.NotFoundException;
>>>>>>> develop
import org.alkemy.wallet.mapper.TransactionMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.model.TypeTransaction;
import org.alkemy.wallet.model.User;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private ITransactionRepository transactionRepository;
    private TransactionMapper transactionMapper;
    private IAccountService accountService;
    private IUserRepository userRepository;
    private IAccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionDto save(TransactionDto transactionDto) {
        if(transactionDto.getAmount() <=0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        transactionDto.setTransactionDate(new Date(Calendar.getInstance().getTimeInMillis()));
        Account account = accountService.findById(transactionDto.getAccountId());
        Transaction transaction = transactionMapper.transactionDtoToTransaction(transactionDto);
        transaction.setAccountId(account);
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionDto> getAllByUser(long userId) {
        return userRepository.findById(userId).map(user -> {
            List<Account> accounts= accountRepository.findAllByUser(user);
            List<Transaction> transactions= new ArrayList<>();
            accounts.forEach(acc -> {
                transactions.addAll(transactionRepository.findAllByAccountId(acc));
            });
            return transactionMapper.toTransactionsDto(transactions);
        }).orElse(null);
    }
    @Override
    @Transactional
    public TransactionDto send(TransactionSendMoneyDto transactionSendMoneyDto, Currency currency) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByCurrencyAndUser(currency, user);
        Account destinationAccount = accountRepository.findById(transactionSendMoneyDto.getDestinationAccountId()).orElse(null);
        if (user == null || account == null || destinationAccount == null){
            throw new NotFoundException("Not found");
        }
        if (account == destinationAccount){
            throw new IllegalArgumentException("Cannot be the same account");
        }
        if (destinationAccount.getCurrency() != currency){
            throw new IllegalArgumentException("Cannot be different types of currency");
        }
        if (transactionSendMoneyDto.getAmount() <= 0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (transactionSendMoneyDto.getAmount() > account.getTransactionLimit()){
            throw new IllegalArgumentException("Amount must be less than the limit");
        }
        //la idea es usar el metodo "save" creado arriba para no repetir codigo. Esto hay que cambiarlo.
        Transaction  transaction= new Transaction();
        transaction.setAccountId(account);
        transaction.setTypeTransaction(TypeTransaction.PAYMENT);
        transaction.setDescript(transactionSendMoneyDto.getDescript());
        transaction.setTransactionDate(new Date());
        transaction.setAmount(transactionSendMoneyDto.getAmount());
        account.setBalance(account.getBalance() - transactionSendMoneyDto.getAmount());


        Transaction destinationTransaction = new Transaction();
        destinationTransaction.setAccountId(destinationAccount);
        destinationTransaction.setTypeTransaction(TypeTransaction.INCOME);
        destinationTransaction.setDescript(transactionSendMoneyDto.getDescript());
        destinationTransaction.setTransactionDate(new Date());
        destinationTransaction.setAmount(transactionSendMoneyDto.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + transactionSendMoneyDto.getAmount());

        accountRepository.save(account);
        accountRepository.save(destinationAccount);
        transactionRepository.save(destinationTransaction);
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
    }

	@Override
	public TransactionDto edit(long userId, long id, String description) {
		return userRepository.findById(userId).map(user -> {
			List<Account> accounts = accountRepository.findAllByUser(user);
			transactionRepository.findById(id).ifPresent(t -> {
				if(!accounts.contains(t.getAccountId())){
					throw new BadRequestException("transaction does not belong to current user: " + user.getEmail());
				}
				t.setDescript(description);
				transactionRepository.save(t);
			});
			return transactionRepository.findById(id).map(t -> {
				return transactionMapper.transactionToTransactionDto(t);
			}).orElseThrow(() -> {
				return new BadRequestException("Transaction not found");
			});
		}).orElseThrow(() -> {
			return new BadRequestException("User not found");
		});
	}

}