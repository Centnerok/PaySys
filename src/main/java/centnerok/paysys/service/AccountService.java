package centnerok.paysys.service;

import java.util.List;

import centnerok.paysys.model.dto.AccountResponse;
import centnerok.paysys.model.dto.BalanceResponse;
import centnerok.paysys.model.dto.DepositRequest;
import centnerok.paysys.model.dto.TransactionResponse;
import centnerok.paysys.model.dto.TransferRequest;

public interface AccountService {
    AccountResponse createAccount(Long userId);

    TransactionResponse makeTransfer(TransferRequest request);

    BalanceResponse getBalance(Long accountId);

    TransactionResponse makeDeposit(Long accountId, DepositRequest request);

    List<TransactionResponse> getTransactions(Long accountId);
}
