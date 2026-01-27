package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.ProformaDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProformaDetailRepository extends JpaRepository<ProformaDetail, Long> {
}
