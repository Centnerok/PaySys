package centnerok.paysys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import centnerok.paysys.model.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findByAccountId(Long accountId);
}
