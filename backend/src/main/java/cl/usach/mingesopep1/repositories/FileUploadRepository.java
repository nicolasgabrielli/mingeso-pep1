package cl.usach.mingesopep1.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.usach.mingesopep1.entities.FileUploadEntity;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long>{
    
}
