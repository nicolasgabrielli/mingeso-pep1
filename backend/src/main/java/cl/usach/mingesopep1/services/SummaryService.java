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
        summary.setDiscountFatPayment(discountFatPayment(summary, payment));
        summary.setDiscountTotalSolidsPayment(discountTotalSolidsPayment(summary, payment));
        payment = sumVariations(summary.getShiftPayment(), summary.getDiscountKgsPayment(), 
            summary.getDiscountFatPayment(), summary.getDiscountTotalSolidsPayment());
        summary.setDiscountRetention(discountRetention(payment));
        summary.setTotalPayment(payment + summary.getDiscountRetention());
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

    public float sumVariations(float shiftPayment, float discountKgsPayment, float discountFatPayment, float discountTotalSolidsPayment) {
        float sumVariations = shiftPayment + discountKgsPayment + discountFatPayment + discountTotalSolidsPayment;

        return sumVariations;
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
        // Checking if there is more than one file uploaded
        int last_file = summary.getFileUploads().size() - 1;
        int penultimate_file = summary.getFileUploads().size() - 2;

        if (last_file < 1){
            return variationKgsPayment;
        }
        else{
            if (!summary.getFileUploads().get(last_file).getDate().equals(summary.getFileUploads().get(penultimate_file).getDate())){
                float last_kgs_milk = summary.getFileUploads().get(last_file).getKgs_milk();
                float penultimate_kgs_milk = summary.getFileUploads().get(penultimate_file).getKgs_milk();
                if (last_kgs_milk > penultimate_kgs_milk){
                    if (last_kgs_milk/penultimate_kgs_milk <= 1.08){
                        variationKgsPayment = 0.0f;
                    }
                    else if (last_kgs_milk/penultimate_kgs_milk >= 1.09 && last_kgs_milk/penultimate_kgs_milk <= 1.25){
                        variationKgsPayment = 0.07f;
                    }
                    else if (last_kgs_milk/penultimate_kgs_milk >= 1.26 && last_kgs_milk/penultimate_kgs_milk <= 1.45){
                        variationKgsPayment = 0.15f;
                    }
                    else if (last_kgs_milk/penultimate_kgs_milk >= 1.46){
                        variationKgsPayment = 0.3f;
                    }
                }
            }
        }
        return payment * variationKgsPayment;
    }

    public float discountFatPayment(SummaryModel summary, float payment) {
        float variationFatPayment = 0;
        // Checking if there is more than one file uploaded
        int last_file = summary.getFileUploadsType2().size() - 1;
        int penultimate_file = summary.getFileUploadsType2().size() - 2;

        if (last_file < 1){
            return variationFatPayment;
        }
        else{
            float last_fat_data = summary.getFileUploadsType2().get(last_file).getFat();
            float penultimate_fat_data = summary.getFileUploadsType2().get(penultimate_file).getFat();
            if (last_fat_data > penultimate_fat_data){
                if (last_fat_data/penultimate_fat_data <= 1.15){
                    variationFatPayment = 0.0f;
                }
                else if (last_fat_data/penultimate_fat_data >= 1.16 && last_fat_data/penultimate_fat_data <= 1.25){
                    variationFatPayment = 0.12f;
                }
                else if (last_fat_data/penultimate_fat_data >= 1.26 && last_fat_data/penultimate_fat_data <= 1.40){
                    variationFatPayment = 0.2f;
                }
                else if (last_fat_data/penultimate_fat_data >= 1.41){
                    variationFatPayment = 0.3f;
                }
            }
        }
        return variationFatPayment;
    }

    public float discountTotalSolidsPayment(SummaryModel summary, float payment) {
        float variationTotalSolidsPayment = 0;
        // Checking if there is more than one file uploaded
        int last_file = summary.getFileUploadsType2().size() - 1;
        int penultimate_file = summary.getFileUploadsType2().size() - 2;

        if (last_file < 1){
            return variationTotalSolidsPayment;
        }
        else{
            float last_total_solids_data = summary.getFileUploadsType2().get(last_file).getTotal_solids();
            float penultimate_total_solids_data = summary.getFileUploadsType2().get(penultimate_file).getTotal_solids();
            if (last_total_solids_data > penultimate_total_solids_data){
                if (last_total_solids_data/penultimate_total_solids_data <= 1.06){
                    variationTotalSolidsPayment = 0.0f;
                }
                else if (last_total_solids_data/penultimate_total_solids_data >= 1.07 && last_total_solids_data/penultimate_total_solids_data <= 1.12){
                    variationTotalSolidsPayment = 0.18f;
                }
                else if (last_total_solids_data/penultimate_total_solids_data >= 1.13 && last_total_solids_data/penultimate_total_solids_data <= 1.35){
                    variationTotalSolidsPayment = 0.27f;
                }
                else if (last_total_solids_data/penultimate_total_solids_data >= 1.36){
                    variationTotalSolidsPayment = 0.45f;
                }
            }
        }
        return variationTotalSolidsPayment;
    }

    public float discountRetention(float payment) {
        if (payment >= 950000){
            return payment * retentionTaxes;    
        }
        return 0.0f;
    }

    public float totalPayment(SummaryModel summary) {
        float totalPayment = 0;

        return totalPayment;
    }

}
