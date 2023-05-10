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
        ArrayList<SummaryModel> summaries = new ArrayList<SummaryModel>();
        SummaryModel summary;
        // Making a summary for each supplier
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

    public SummaryModel calculateSummary(SummaryModel summary) {
        
        int last_file = summary.getFileUploads().size() - 1;
        int last_file_type2 = summary.getFileUploadsType2().size() - 1;
        // Getting the last row in the file table
        FileUploadEntity file = summary.getFileUploads().get(last_file);
        FileUploadEntityType2 file_type2 = summary.getFileUploadsType2().get(last_file_type2);
        
        // Getting the kgs_milk from the last file uploaded
        int kgs_milk = file.getKgs_milk();

        // Setting Total Payment
        float payment = 0;

        summary.setCategoryPayment(categoryPayment(kgs_milk, summary.getSupplierCategory()));
        summary.setFatPayment(fatPayment(kgs_milk, file_type2.getFat()));
        summary.setTotalSolidsPayment(totalSolidsPayment(kgs_milk, file_type2.getTotal_solids()));
        payment = sumPayments(summary.getCategoryPayment(), summary.getFatPayment(), summary.getTotalSolidsPayment());
        
        summary.setShiftPayment(shiftPayment(summary, payment));
        summary.setDiscountKgsPayment(discountKgsPayment(summary, payment));
        summary.setDiscountFatPayment(discountFatPayment());
        summary.setDiscountTotalSolidsPayment(discountTotalSolidsPayment());
        summary.setDiscountRetention(discountRetention(payment));
        summary.setTotalPayment(totalPayment());
        return summary;
    }

    public ArrayList<SummaryModel> calculateSummaries(ArrayList<SummaryModel> summaries) {
        for (int i = 0; i < summaries.size(); i++){
            summaries.set(i, calculateSummary(summaries.get(i)));
        }
        return summaries;
    }

    public float categoryPayment(int kgs_milk, String category) {
        float categoryPayment;
        if (category.equals("A")){
            categoryPayment = kgs_milk * 700;
        }
        else if (category.equals("B")){
            categoryPayment = kgs_milk * 550;
        }
        else if (category.equals("C")){
            categoryPayment = kgs_milk * 400;
        }
        else if (category.equals("D")){
            categoryPayment = kgs_milk * 250;
        }        
        else {      // Error Case
            categoryPayment = 0;
        }
        return categoryPayment;
    }

    public float fatPayment(int kgs_milk, float fat) {
        float fatPayment;
        if (fat >= 0 && fat <= 20){
            fatPayment = kgs_milk * 30;
        }
        else if (fat >= 21 && fat <= 45){
            fatPayment = kgs_milk * 80;
        }
        else if (fat >= 46) {
            fatPayment = kgs_milk * 120;
        }
        else {      // Error Case
            fatPayment = 0;
        }
        return fatPayment;
    }

    public float totalSolidsPayment(int kgs_milk, float total_solids) {
        float totalSolidsPayment;
        if (total_solids >= 0 && total_solids <= 7){
            totalSolidsPayment = kgs_milk * -130;
        }
        else if (total_solids >= 8 && total_solids <= 18){
            totalSolidsPayment = kgs_milk * -90;
        }
        else if (total_solids >= 19 && total_solids <= 35) {
            totalSolidsPayment = kgs_milk * 95;
        }
        else if (total_solids >= 36) {
            totalSolidsPayment = kgs_milk * 150;
        }
        else {      // Error Case
            totalSolidsPayment = 0;
        }
        return totalSolidsPayment;
    }

    public float sumPayments(float categoryPayment, float fatPayment, float totalSolidsPayment) {
        float sumPayments = categoryPayment + fatPayment + totalSolidsPayment;

        return sumPayments;
    }


    public float shiftPayment(SummaryModel summary, float payment) {
        float shiftBonus = 1.0f;
        // Checking if there is more than one file uploaded
        int last_file = summary.getFileUploads().size() - 1;
        int penultimate_file = summary.getFileUploads().size() - 2;

        if (last_file == 0){
            if (summary.getFileUploads().get(last_file).getShift().equals("M")){
                shiftBonus = 0.12f;
            }
            else if(summary.getFileUploads().get(last_file).getShift().equals("T")){
                shiftBonus = 0.08f;
            }
            else {  // Error Case
                shiftBonus = 0.0f;
            }
        }
        else{
            // Checking if the last file is from the same day as the penultimate file
            if (summary.getFileUploads().get(last_file).getDate().equals(summary.getFileUploads().get(penultimate_file).getDate())){
                // Checking if the last file shift is different from the penultimate file shift
                if (!summary.getFileUploads().get(last_file).getShift().equals(summary.getFileUploads().get(penultimate_file).getShift())){
                    if (summary.getFileUploads().get(last_file).getShift().equals("M")
                    || summary.getFileUploads().get(penultimate_file).getShift().equals("M")
                    || summary.getFileUploads().get(last_file).getShift().equals("T")
                    || summary.getFileUploads().get(penultimate_file).getShift().equals("T")){
                        shiftBonus = 0.12f;
                    }
                    else {  // Error Case
                        shiftBonus = 0.0f;
                    }
                }
                else {  // Error Case
                    shiftBonus = 0.0f;
                }
            }
        }
        return payment * shiftBonus;
    }

    public float discountKgsPayment(SummaryModel summary, float payment) {
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

    public float discountRetention(float payment) {
        return payment * retentionTaxes;
    }

    public float totalPayment() {
        float totalPayment = 0;

        return totalPayment;
    }

}
