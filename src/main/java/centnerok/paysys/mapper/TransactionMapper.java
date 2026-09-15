package centnerok.paysys.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import centnerok.paysys.model.dto.TransactionResponse;
import centnerok.paysys.model.entity.LedgerEntry;
import centnerok.paysys.model.entity.Transaction;

@Component 
public class TransactionMapper {

    public TransactionResponse mapTransactionToResponse(Transaction transaction, BigDecimal amount) {
        return new TransactionResponse(
            transaction.getType(),
            transaction.getStatus(),
            amount,
            transaction.getCreatedAt()
        );
    }

    public TransactionResponse mapLedgerToResponse(LedgerEntry ledger) {
        return new TransactionResponse(
            ledger.getTransaction().getType(),
            ledger.getTransaction().getStatus(),
            ledger.getAmount(),
            ledger.getTransaction().getCreatedAt()
        );
    }
}
