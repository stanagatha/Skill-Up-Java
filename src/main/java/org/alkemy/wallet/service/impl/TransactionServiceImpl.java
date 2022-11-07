package org.alkemy.wallet.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.mapper.TransactionMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.ITransactionService;
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
		if (transactionDto.getAmount() <= 0) {
			throw new BadRequestException("Amount must be greater than 0");
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
			List<Account> accounts = accountRepository.findAllByUser(user);
			List<Transaction> transactions = new ArrayList<>();
			accounts.forEach(acc -> {
				transactions.addAll(transactionRepository.findAllByAccountId(acc));
			});
			return transactionMapper.toTransactionsDto(transactions);
		}).orElseThrow(() -> {
			return new BadRequestException("User not found, cannot list transactions");
		});
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