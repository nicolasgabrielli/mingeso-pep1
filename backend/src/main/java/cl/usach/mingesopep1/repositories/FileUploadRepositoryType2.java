package cl.usach.mingesopep1.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.usach.mingesopep1.entities.FileUploadEntityType2;

@Repository
public interface FileUploadRepositoryType2 extends JpaRepository<FileUploadEntityType2, Long>{
    
}
