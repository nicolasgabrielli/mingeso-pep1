package cl.usach.mingesopep1.repositories;

import cl.usach.mingesopep1.entities.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long>{
}
