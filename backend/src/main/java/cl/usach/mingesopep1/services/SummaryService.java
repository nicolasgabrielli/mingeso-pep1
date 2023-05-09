package cl.usach.mingesopep1.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.usach.mingesopep1.entities.FileUploadEntity;
import cl.usach.mingesopep1.entities.FileUploadEntityType2;
import cl.usach.mingesopep1.entities.SupplierEntity;
import cl.usach.mingesopep1.models.SummaryModel;


@Service
public class SummaryService {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private FileUploadService fileUploadService;

    // Taxes for payments > 950.000
    private float retentionTaxes = (float) 0.13; // 13%

    public ArrayList<SummaryModel> createSummary() {
        List<SupplierEntity> suppliers;
        List<FileUploadEntity> fileUploads;
        List<FileUploadEntityType2> fileUploadsType2;
        // Getting data from database
        try {
            suppliers = supplierService.getAllSuppliers();
            fileUploads = fileUploadService.getAllFiles();
            fileUploadsType2 = fileUploadService.getAllFilesType2();
        } catch (Exception e){
            throw new RuntimeException("No se pudo obtener la información de la base de datos. Error: " + e.getMessage());
        }
        //Make an empty summary list
        ArrayList<SummaryModel> summaries = new ArrayList<SummaryModel>();
        SummaryModel summary;
        // Make a summary for each supplier
        for (int i = 0; i < suppliers.size(); i++){
            summary = new SummaryModel();
            summary.setSupplierCode(suppliers.get(i).getCode());
            summary.setSupplierCategory(suppliers.get(i).getCategory());

            for (int j = 0; j < fileUploads.size(); j++){
                if(fileUploads.get(j).getSupplier().equals(suppliers.get(i).getCode())){
                    summary.getFileUploads().add(fileUploads.get(j));
                }
            }

            for (int j = 0; j < fileUploadsType2.size(); j++){
                if(fileUploadsType2.get(j).getSupplier().equals(suppliers.get(i).getCode())){
                    summary.getFileUploadsType2().add(fileUploadsType2.get(j));
                }
            }
            summaries.add(summary);
        }
        return summaries;
    }

    public float categoryPayment() {
        float categoryPayment = 0;

        return categoryPayment;
    }

    public float fatPayment() {
        float fatPayment = 0;

        return fatPayment;
    }

    public float totalSolidsPayment() {
        float totalSolidsPayment = 0;

        return totalSolidsPayment;
    }

    public float shiftPayment() {
        float shiftPayment = 0;

        return shiftPayment;
    }

    public float discountKgsPayment() {
        float variationKgsPayment = 0;

        return variationKgsPayment;
    }

    public float discountFatPayment() {
        float variationFatPayment = 0;

        return variationFatPayment;
    }

    public float discountTotalSolidsPayment() {
        float variationTotalSolidsPayment = 0;

        return variationTotalSolidsPayment;
    }
}
