package org.alkemy.wallet.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.model.Account;
import org.alkemy.wallet.model.Transaction;
import org.alkemy.wallet.model.User;
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
    private final IAccountRepository accountRepository;
    private final IUserRepository userRepository;

    @Autowired
    public TransactionServiceImpl(ITransactionRepository transactionRepository, IAccountRepository accountRepository, IUserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository= accountRepository;
        this.userRepository= userRepository;
    }

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
    public TransactionDto getAll(long userId) {
        return (TransactionDto) userRepository.findById(userId).map(user -> {
            List<Account> accounts= accountRepository.findAllByUser(user);
            List<Transaction> transactions= new ArrayList<>();
            accounts.forEach(acc -> {
                transactions.addAll(transactionRepository.findAllByAccountId(acc));
            });
            System.out.println("Get all, no users");
            transactions.forEach(System.out::println);;
            //implemente mapper;
            return null;
        }).orElse(null);
    }

}