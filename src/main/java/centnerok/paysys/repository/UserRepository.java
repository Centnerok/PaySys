package centnerok.paysys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import centnerok.paysys.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
