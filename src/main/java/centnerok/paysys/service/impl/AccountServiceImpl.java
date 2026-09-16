package centnerok.paysys.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import centnerok.paysys.exception.InsufficientFundsException;
import centnerok.paysys.exception.InvalidDepositException;
import centnerok.paysys.exception.InvalidTransferException;
import centnerok.paysys.exception.ResourceNotFoundException;
import centnerok.paysys.mapper.AccountMapper;
import centnerok.paysys.mapper.TransactionMapper;
import centnerok.paysys.model.dto.AccountResponse;
import centnerok.paysys.model.dto.BalanceResponse;
import centnerok.paysys.model.dto.DepositRequest;
import centnerok.paysys.model.dto.TransactionResponse;
import centnerok.paysys.model.dto.TransferRequest;
import centnerok.paysys.model.entity.Account;
import centnerok.paysys.model.entity.LedgerEntry;
import centnerok.paysys.model.entity.Transaction;
import centnerok.paysys.model.enums.TransactionStatus;
import centnerok.paysys.model.enums.TransactionType;
import centnerok.paysys.repository.AccountRepository;
import centnerok.paysys.repository.LedgerEntryRepository;
import centnerok.paysys.repository.TransactionRepository;
import centnerok.paysys.repository.UserRepository;
import centnerok.paysys.service.AccountService;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private final LedgerEntryRepository ledgerRepository;

    private final AccountMapper accountMapper;

    private final TransactionMapper transactionMapper;

    public AccountServiceImpl(
        AccountRepository accountRepository, 
        TransactionRepository transactionRepository,
        LedgerEntryRepository ledgerRepository,
        TransactionMapper transactionMapper,
        UserRepository userRepository,
        AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.ledgerRepository = ledgerRepository;
        this.transactionMapper = transactionMapper;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional 
    @Override
    public AccountResponse createAccount(Long userId) {
        Account account = new Account();
        account.setUser(userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with id=" + userId + " not found")));
        Account savedAccount = accountRepository.save(account);
        log.info("Created account with id={} for user with id={}", savedAccount.getId(), userId);
        return accountMapper.mapAccountToResponse(savedAccount);
    }

    @Transactional 
    @Override
    public TransactionResponse makeTransfer(TransferRequest request) {
        if (request.senderAccountId().equals(request.recipientAccountId())) {
            throw new InvalidTransferException("Impossible to transfer funds from one account to same account");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransferException("Request amount must be positive");
        }

        Account sender = findAccountById(request.senderAccountId());

        Account recipient = findAccountById(request.recipientAccountId());

        if (sender.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the senders account with id=" + request.senderAccountId());
            
        } 

        sender.setBalance(sender.getBalance().subtract(request.amount()));
        recipient.setBalance(recipient.getBalance().add(request.amount()));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(transaction);

        LedgerEntry senderLedger = new LedgerEntry();
        LedgerEntry recipientLedger = new LedgerEntry();

        senderLedger.setTransaction(transaction);
        senderLedger.setAccount(sender);
        senderLedger.setAmount(request.amount().negate());

        recipientLedger.setTransaction(transaction);
        recipientLedger.setAccount(recipient);
        recipientLedger.setAmount(request.amount());

        ledgerRepository.save(senderLedger);
        ledgerRepository.save(recipientLedger);

        log.info("Transfer transaction is SUCCESS: id={}, sender={}, recipient={} amount={}"
            , transaction.getId(), request.senderAccountId(), request.recipientAccountId(), request.amount());

        return transactionMapper.mapTransactionToResponse(transaction, request.amount());
    }

    @Transactional(readOnly = true)
    @Override
    public BalanceResponse getBalance(Long accountId) {
        Account account = findAccountById(accountId);

        return new BalanceResponse(account.getBalance());
    }

    @Transactional 
    @Override
    public TransactionResponse makeDeposit(Long accountId, DepositRequest request) {
        if (request.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDepositException("Deposit amount must be greater than 0");
        }

        Account account = findAccountById(accountId);

        account.setBalance(account.getBalance().add(request.amount()));

        Transaction transaction = new Transaction(
            null,
            TransactionType.DEPOSIT,
            TransactionStatus.SUCCESS,
            null
        );

        LedgerEntry ledger = new LedgerEntry(
            null,
            transaction,
            account,
            request.amount(),
            null
        );

        transactionRepository.save(transaction);
        ledgerRepository.save(ledger);

        log.info("Deposit completed: amount={} account={}", request.amount(), accountId);

        return transactionMapper.mapTransactionToResponse(transaction, request.amount());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionResponse> getTransactions(Long accountId) {
        findAccountById(accountId);
        
        return ledgerRepository.findByAccountId(accountId)
            .stream()
            .map(transactionMapper::mapLedgerToResponse)
            .toList();
    }

    public Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account with id=" + accountId + " not found"));
    }

}
