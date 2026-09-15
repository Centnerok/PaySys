package centnerok.paysys.mapper;

import org.springframework.stereotype.Component;

import centnerok.paysys.model.dto.AccountResponse;
import centnerok.paysys.model.entity.Account;

@Component 
public class AccountMapper {
    public AccountResponse mapAccountToResponse(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getBalance(),
            account.getCreatedAt()
        );
    }
}
