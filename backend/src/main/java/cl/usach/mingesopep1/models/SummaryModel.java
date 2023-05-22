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
    private int supplierCode;
    private String supplierCategory;
    private String supplierName;
    private Boolean supplierRetention;
    private ArrayList<FileUploadEntity> fileUploads;
    private ArrayList<FileUploadEntityType2> fileUploadsType2;

    // Summary data
    private float milkPayment;
    private float categoryPayment;
    private float fatPayment;
    private float totalSolidsPayment;
    private float shiftPayment;
    private float discountKgsPayment;
    private float discountFatPayment;
    private float discountTotalSolidsPayment;
    private float taxRetention;
    private float totalPayment;
    private float finalPayment;

    // Variations
    private float milkVariation;
    private float fatVariation;
    private float totalSolidsVariation;

}
