package centnerok.paysys.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import centnerok.paysys.model.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
