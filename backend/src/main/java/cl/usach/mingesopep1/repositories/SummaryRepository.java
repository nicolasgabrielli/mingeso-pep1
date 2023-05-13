package cl.usach.mingesopep1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.usach.mingesopep1.entities.SummaryEntity;

@Repository
public interface SummaryRepository extends JpaRepository<SummaryEntity, Long>{
    
}
