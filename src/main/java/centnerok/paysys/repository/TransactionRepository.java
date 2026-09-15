package centnerok.paysys.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import centnerok.paysys.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
