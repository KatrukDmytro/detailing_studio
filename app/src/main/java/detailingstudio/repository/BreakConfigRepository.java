package detailingstudio.repository;

import detailingstudio.model.BreakConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakConfigRepository extends JpaRepository<BreakConfig, Long> {
}
