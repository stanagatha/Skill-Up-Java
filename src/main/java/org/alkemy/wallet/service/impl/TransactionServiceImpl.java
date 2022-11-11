package org.alkemy.wallet.service.impl;

import java.util.*;

import org.alkemy.wallet.dto.TransactionDto;
import org.alkemy.wallet.dto.TransactionSendMoneyDto;
import org.alkemy.wallet.exception.BadRequestException;
import org.alkemy.wallet.dto.TransactionRequestDto;
import org.alkemy.wallet.exception.ForbiddenException;
import org.alkemy.wallet.exception.NotFoundException;
import org.alkemy.wallet.mapper.TransactionMapper;
import org.alkemy.wallet.model.*;
import org.alkemy.wallet.model.Currency;
import org.alkemy.wallet.repository.IAccountRepository;
import org.alkemy.wallet.repository.ITransactionRepository;
import org.alkemy.wallet.repository.IUserRepository;
import org.alkemy.wallet.service.IAccountService;
import org.alkemy.wallet.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final ITransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final IAccountService accountService;
    private final IUserRepository userRepository;
    private final IAccountRepository accountRepository;
    private final MessageSource messageSource;

    @Autowired
    public TransactionServiceImpl(ITransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  IAccountService accountService,
                                  IUserRepository userRepository,
                                  IAccountRepository accountRepository, MessageSource messageSource) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.messageSource = messageSource;
    }

    private String message(String message){
        return messageSource.getMessage(message,null,Locale.US);
    }

    @Override
    @Transactional
    public TransactionDto save(TransactionRequestDto transactionDto) {

        if (transactionDto.getAmount() <=0)
            throw new BadRequestException(message("amount.invalid"));

        List<TypeTransaction> validTransactionTypes = Arrays.stream(TypeTransaction.values()).collect(Collectors.toList());
        if (!validTransactionTypes.contains(transactionDto.getTypeTransaction()))
            throw new BadRequestException(message("transaction.invalid"));

        if (transactionDto.getAccountId() == null)
            throw new BadRequestException(message("account.mandatory"));

        Optional<Account> account = accountRepository.findById(transactionDto.getAccountId());
        if (account.isEmpty())
            throw new NotFoundException(message("account.not-found"));

        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();
        if (account.get().getUser().getId() != loggedUserId && transactionDto.getTypeTransaction() != TypeTransaction.INCOME)
            throw new ForbiddenException(message("account.not-allow"));
        if (transactionDto.getTypeTransaction() == TypeTransaction.PAYMENT &&
            account.get().getBalance() < transactionDto.getAmount()){
            throw new BadRequestException(message("account.no-enough"));
        }

        Transaction transaction = new Transaction();
        transaction.setTypeTransaction(transactionDto.getTypeTransaction());
        transaction.setTransactionDate(new Date());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setDescript(transactionDto.getDescription());
        transaction.setAccount(account.get());
        transactionRepository.save(transaction);

        Double sum = transaction.getAmount();
        if (transaction.getTypeTransaction() == TypeTransaction.PAYMENT)
            sum *= -1;
        account.get().setBalance(account.get().getBalance() + sum);

        return transactionMapper.transactionToTransactionDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDto findById(Long id) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty())
            throw new NotFoundException(messageSource.getMessage("transaction.invalid-id",new Long[]{id},Locale.US));

        Account account = transaction.get().getAccount();
        if (account.getUser().getId() != loggedUserId)
            throw new ForbiddenException(message("transaction.not-allow"));

        return transactionMapper.transactionToTransactionDto(transaction.get());
    }

    @Override
    public Page<TransactionDto> getAllByUser(long userId, Integer pageNumber) {
        if(pageNumber == null || pageNumber < 0)
            throw new BadRequestException(message("page.invalid.number"));

        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedUser = userRepository.findByEmail(loggedUserEmail);
        Long loggedUserId = loggedUser.getId();

        if (loggedUserId != userId)
            throw new ForbiddenException(message("transaction.unable"));

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new NotFoundException(messageSource.getMessage("user.invalid-id",new Long[]{userId},Locale.US));

        List<Account> accounts= accountRepository.findAllByUser(user.get());
        List<Transaction> transactions= new ArrayList<>();
        accounts.forEach(acc -> {
            transactions.addAll(transactionRepository.findAllByAccount(acc));
        });

        Page<Transaction> transactionPage = new PageImpl<>(transactions, PageRequest.of(pageNumber,10), transactions.size());

        if((transactionPage.getTotalPages() - 1) < pageNumber)
            throw new BadRequestException(message("page.wrong-number"));

        return transactionPage.map(transaction -> transactionMapper.transactionToTransactionDto(transaction));

    }
    
    @Override
    @Transactional
    public TransactionDto send(TransactionSendMoneyDto transactionSendMoneyDto, Currency currency) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Account originAccount = accountRepository.findByCurrencyAndUser(currency, user);
        Account destinationAccount = accountRepository.findById(transactionSendMoneyDto.getDestinationAccountId()).orElse(null);
        if (user == null || originAccount == null || destinationAccount == null){
            throw new NotFoundException(message("not-found.error"));
        }
        if (originAccount == destinationAccount){
            throw new IllegalArgumentException(message("account.not-same"));
        }
        if (destinationAccount.getCurrency() != currency){
            throw new IllegalArgumentException(message("account.diff-currency"));
        }
        if (transactionSendMoneyDto.getAmount() <= 0){
            throw new IllegalArgumentException(message("amount.invalid"));
        }
        if (transactionSendMoneyDto.getAmount() > originAccount.getTransactionLimit()){
            throw new IllegalArgumentException(message("amount.above-limit"));
        }

        TransactionRequestDto originTransactionDto = new TransactionRequestDto();
        originTransactionDto.setAmount(transactionSendMoneyDto.getAmount());
        originTransactionDto.setDescription(transactionSendMoneyDto.getDescription());
        originTransactionDto.setAccountId(originAccount.getId());
        originTransactionDto.setTypeTransaction(TypeTransaction.PAYMENT);
        TransactionDto transactionDto = save(originTransactionDto);

        TransactionRequestDto destinyTransactionDto = new TransactionRequestDto();
        destinyTransactionDto.setAmount(transactionSendMoneyDto.getAmount());
        destinyTransactionDto.setDescription(transactionSendMoneyDto.getDescription());
        destinyTransactionDto.setAccountId(destinationAccount.getId());
        destinyTransactionDto.setTypeTransaction(TypeTransaction.INCOME);
        save(destinyTransactionDto);

        return transactionDto;
    }

	@Override
	@Transactional
	public TransactionDto edit(long id, String description) {
        if (description == null)
            throw new BadRequestException(message("transaction.mand-descript"));

        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        long loggedUserId = userRepository.findByEmail(loggedUserEmail).getId();

        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty())
            throw new NotFoundException(message("transaction.not-found"));

        Account account = transaction.get().getAccount();
        if (account.getUser().getId() != loggedUserId)
            throw new ForbiddenException(message("transaction.not-allow-mod"));

        transaction.get().setDescript(description);
        return transactionMapper.transactionToTransactionDto(transaction.get());
	}
}