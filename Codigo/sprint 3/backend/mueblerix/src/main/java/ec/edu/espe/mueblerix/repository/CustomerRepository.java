package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdentification(String identification);
    boolean existsByIdentification(String identification);
}
