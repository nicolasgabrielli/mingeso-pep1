package cl.usach.mingesopep1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "file_upload_type_2")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUploadEntityType2 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String supplier;
    private float fat;
    private float total_solids;
}
