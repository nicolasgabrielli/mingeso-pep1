package cl.usach.mingesopep1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import cl.usach.mingesopep1.entities.FileUploadEntity;
import cl.usach.mingesopep1.entities.FileUploadEntityType2;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryModel {
    private String supplierCode;
    private String supplierCategory;
    private ArrayList<FileUploadEntity> fileUploads;
    private ArrayList<FileUploadEntityType2> fileUploadsType2;
}
