package centnerok.paysys.service.account;

import centnerok.paysys.exception.InsufficientFundsException;
import centnerok.paysys.exception.InvalidDepositException;
import centnerok.paysys.exception.InvalidTransferException;
import centnerok.paysys.exception.ResourceNotFoundException;
import centnerok.paysys.mapper.AccountMapper;
import centnerok.paysys.mapper.TransactionMapper;
import centnerok.paysys.model.dto.*;
import centnerok.paysys.model.entity.Account;
import centnerok.paysys.model.entity.LedgerEntry;
import centnerok.paysys.model.entity.Transaction;
import centnerok.paysys.model.entity.User;
import centnerok.paysys.model.enums.TransactionStatus;
import centnerok.paysys.model.enums.TransactionType;
import centnerok.paysys.repository.AccountRepository;
import centnerok.paysys.repository.LedgerEntryRepository;
import centnerok.paysys.repository.TransactionRepository;
import centnerok.paysys.repository.UserRepository;
import centnerok.paysys.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LedgerEntryRepository ledgerRepository;

    private AccountMapper accountMapper = new AccountMapper();

    private TransactionMapper transactionMapper = new TransactionMapper();

    private AccountServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AccountServiceImpl(
                accountRepository,
                transactionRepository,
                ledgerRepository,
                transactionMapper,
                userRepository,
                accountMapper
        );
    }

    @Test
    void shouldThrowExceptionWhenUserDidntExists() {
        Long userId = 12L;

        String expected = "User with id=" + userId + " not found";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createAccount(userId)
        );

        assertEquals(
                expected,
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnAccountResponseWhenCreateUser() {
        Long userId = 12L;

        User user = new User();
        user.setId(userId);

        Account newAccount = new Account();
        newAccount.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        AccountResponse response = service.createAccount(userId);

        assertEquals(BigDecimal.ZERO, response.balance());
    }

    @Test
    void shouldThrowExceptionWhenSenderIsAlsoRecipient() {
        TransferRequest request = new TransferRequest(
                1L,
                1L,
                BigDecimal.TEN
        );

        String expected = "Impossible to transfer funds from one account to same account";

        InvalidTransferException exception = assertThrows(
                InvalidTransferException.class,
                () -> service.makeTransfer(request)
        );

        assertEquals(
                expected,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenRequestAmountIsNonPositive() {
        TransferRequest request = new TransferRequest(
                1L,
                3L,
                BigDecimal.valueOf(-10)
        );

        String expected = "Request amount must be positive";

        InvalidTransferException exception = assertThrows(
                InvalidTransferException.class,
                () -> service.makeTransfer(request)
        );

        assertEquals(
                expected,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesntExists() {
        Long accountId = 1L;

        String expected = "Account with id=" + accountId + " not found";

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.findAccountById(accountId)
        );

        assertEquals(
                expected,
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFundsOnBalance() {
        TransferRequest request = new TransferRequest(
                1L,
                3L,
                BigDecimal.valueOf(12)
        );

        String expected = "Insufficient funds in the senders account with id=" + request.senderAccountId();

        Account sender = new Account();
        sender.setId(request.senderAccountId());
        sender.setBalance(BigDecimal.ZERO);

        Account recipient = new Account();
        recipient.setId(request.recipientAccountId());

        when(accountRepository.findById(request.senderAccountId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(request.recipientAccountId())).thenReturn(Optional.of(recipient));

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> service.makeTransfer(request)
        );

        assertEquals(
                expected,
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnTransactionResponseWhenTransfer() {
        TransferRequest request = new TransferRequest(
                1L,
                3L,
                BigDecimal.valueOf(12)
        );

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setType(TransactionType.TRANSFER);

        Account sender = new Account();
        sender.setId(request.senderAccountId());
        sender.setBalance(BigDecimal.valueOf(12));

        Account recipient = new Account();
        recipient.setId(request.recipientAccountId());

        when(accountRepository.findById(request.senderAccountId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(request.recipientAccountId())).thenReturn(Optional.of(recipient));

        TransactionResponse response = service.makeTransfer(request);

        assertEquals(
                TransactionType.TRANSFER,
                response.type()
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                response.status()
        );

        assertEquals(
                request.amount(),
                response.amount()
        );
    }

    @Test
    void shouldChangeBalanceAfterTransfer() {
        TransferRequest request = new TransferRequest(
                1L,
                3L,
                BigDecimal.valueOf(12)
        );

        BigDecimal expectedSenderBalance = new BigDecimal(0);
        BigDecimal expectedRecipientBalance = new BigDecimal(12);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setType(TransactionType.TRANSFER);

        Account sender = new Account();
        sender.setId(request.senderAccountId());
        sender.setBalance(BigDecimal.valueOf(12));

        Account recipient = new Account();
        recipient.setId(request.recipientAccountId());
        recipient.setBalance(BigDecimal.ZERO);

        when(accountRepository.findById(request.senderAccountId())).thenReturn(Optional.of(sender));
        when(accountRepository.findById(request.recipientAccountId())).thenReturn(Optional.of(recipient));

        service.makeTransfer(request);

        assertEquals(
                expectedSenderBalance,
                sender.getBalance()
        );

        assertEquals(
                expectedRecipientBalance,
                recipient.getBalance()
        );
    }

    @Test
    void shouldReturnBalance() {
        Long accountId = 10L;

        Account account = new Account();
        account.setId(accountId);
        account.setBalance(BigDecimal.TEN);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BalanceResponse response = service.getBalance(accountId);

        assertEquals(
                account.getBalance(),
                response.balance()
        );
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountSmallerThanZero() {
        Long accountId = 1L;

        DepositRequest request = new DepositRequest(BigDecimal.ZERO);

        String expected = "Deposit amount must be greater than 0";

        InvalidDepositException exception = assertThrows(
                InvalidDepositException.class,
                () -> service.makeDeposit(accountId, request)
        );

        assertEquals(
                expected,
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnTransactionResponseWhenDeposit() {
        Long accountId = 1L;

        DepositRequest request = new DepositRequest(BigDecimal.TEN);

        Account account = new Account();
        account.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        TransactionResponse response = service.makeDeposit(accountId, request);

        assertEquals(
                TransactionType.DEPOSIT,
                response.type()
        );

        assertEquals(
                TransactionStatus.SUCCESS,
                response.status()
        );

        assertEquals(
                request.amount(),
                response.amount()
        );
    }

    @Test
    void shouldChangeBalanceAfterDeposit() {
        Long accountId = 1L;

        DepositRequest request = new DepositRequest(BigDecimal.TEN);

        Account account = new Account();
        account.setId(accountId);
        account.setBalance(BigDecimal.ZERO);

        BigDecimal expected = request.amount().add(account.getBalance());

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        service.makeDeposit(accountId, request);

        assertEquals(
                expected,
                account.getBalance()
        );
    }
}
