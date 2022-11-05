package org.alkemy.wallet.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.mapper.TransactionMapper;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

	private final ITransactionRepository transactionRepository;
	private final TransactionMapper transactionMapper;
	private final IAccountService accountService;
	private final IUserRepository userRepository;
	private final IAccountRepository accountRepository;

	@Override
	@Transactional
	public TransactionDto save(TransactionDto transactionDto) {
		if (transactionDto.getAmount() <= 0) {
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
			List<Account> accounts = accountRepository.findAllByUser(user);
			List<Transaction> transactions = new ArrayList<>();
			accounts.forEach(acc -> {
				transactions.addAll(transactionRepository.findAllByAccountId(acc));
			});
			return transactionMapper.toTransactionsDto(transactions);
		}).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public TransactionDto edit(long userId, long id, String description) {
		return userRepository.findById(userId).map(user -> {
			List<Account> accounts = accountRepository.findAllByUser(user);
			transactionRepository.findById(id).ifPresent(t -> {
				if(!accounts.contains(t.getAccountId())){
					throw new IllegalArgumentException("transaction does not belong to current user: " + user.getEmail());
				}
				t.setDescript(description);
				transactionRepository.save(t);
			});
			return transactionRepository.findById(id).map(t -> {
				return transactionMapper.transactionToTransactionDto(t);
			}).orElseThrow(IllegalArgumentException::new);
		}).orElseThrow(IllegalArgumentException::new);
	}

}