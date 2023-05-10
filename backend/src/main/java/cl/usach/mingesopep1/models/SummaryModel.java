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
    // Data from database
    private String supplierCode;
    private String supplierCategory;
    private ArrayList<FileUploadEntity> fileUploads;
    private ArrayList<FileUploadEntityType2> fileUploadsType2;

    // Summary data
    private float categoryPayment;
    private float fatPayment;
    private float totalSolidsPayment;
    private float shiftPayment;
    private float discountKgsPayment;
    private float discountFatPayment;
    private float discountTotalSolidsPayment;
    private float discountRetention;
    private float totalPayment;
}
