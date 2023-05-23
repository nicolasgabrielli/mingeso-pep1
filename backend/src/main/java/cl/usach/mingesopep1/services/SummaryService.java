package cl.usach.mingesopep1.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.usach.mingesopep1.entities.FileUploadEntity;
import cl.usach.mingesopep1.entities.FileUploadEntityType2;
import cl.usach.mingesopep1.entities.SummaryEntity;
import cl.usach.mingesopep1.entities.SupplierEntity;
import cl.usach.mingesopep1.models.SummaryModel;
import cl.usach.mingesopep1.repositories.SummaryRepository;


@Service
public class SummaryService {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SummaryRepository summaryRepository;

    // Taxes for payments > 950.000
    private float retentionTaxes = (float) 0.13; // 13%

    public ArrayList<SummaryModel> createSummaryModels() {
        List<SupplierEntity> suppliersData;
        List<FileUploadEntity> fileUploadsData;
        List<FileUploadEntityType2> fileUploadsType2Data;
        // Getting data from database
        suppliersData = supplierService.getAllSuppliers();
        fileUploadsData = fileUploadService.getAllFiles();
        fileUploadsType2Data = fileUploadService.getAllFilesType2();

        // Transforming the data into arraylist
        ArrayList<SupplierEntity> suppliers = new ArrayList<SupplierEntity>(suppliersData);
        ArrayList<FileUploadEntity> fileUploads = new ArrayList<FileUploadEntity>(fileUploadsData);
        ArrayList<FileUploadEntityType2> fileUploadsType2 = new ArrayList<FileUploadEntityType2>(fileUploadsType2Data);

        ArrayList<SummaryModel> summaries = new ArrayList<SummaryModel>();
        SummaryModel summary;
        // Making a summary for each supplier
        for (int i = 0; i < suppliers.size(); i++){
            summary = new SummaryModel();
            summary.setSupplierCode(suppliers.get(i).getCode());
            summary.setSupplierCategory(suppliers.get(i).getCategory());
            summary.setSupplierName(suppliers.get(i).getName());
            if(suppliers.get(i).getRetention().equals("Sí")){
                summary.setSupplierRetention(true);
            }
            else{
                summary.setSupplierRetention(false);
            }
            summary.setFileUploads(new ArrayList<FileUploadEntity>());
            summary.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());
            for (int j = 0; j < fileUploads.size(); j++){
                if(fileUploads.get(j).getSupplier() == suppliers.get(i).getCode()){
                    summary.getFileUploads().add(fileUploads.get(j));
                }
            }

            for (int j = 0; j < fileUploadsType2.size(); j++){
                if(fileUploadsType2.get(j).getSupplier() == suppliers.get(i).getCode()){
                    summary.getFileUploadsType2().add(fileUploadsType2.get(j));
                }
            }
            summaries.add(summary);
        }

        return summaries;
    }

    public SummaryModel calculateSummaryModel(SummaryModel summary) {

        int last_file = summary.getFileUploads().size() - 1;
        int last_file_type2 = summary.getFileUploadsType2().size() - 1;
        
        if(summary.getFileUploads().size() == 0 || summary.getFileUploadsType2().size() == 0){
            return null;
        }
        
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
        payment = payment - sumVariations(summary.getShiftPayment(), summary.getDiscountKgsPayment(), 
            summary.getDiscountFatPayment(), summary.getDiscountTotalSolidsPayment());
        summary.setTotalPayment(payment);
        summary.setTaxRetention(payment + taxRetention(summary, payment));
        summary.setFinalPayment(payment + summary.getTaxRetention());

        return summary;
    }

    public ArrayList<SummaryModel> calculateSummaries(ArrayList<SummaryModel> summaries) {
        for (int i = 0; i < summaries.size(); i++){
            summaries.set(i, calculateSummaryModel(summaries.get(i)));
        }
        return summaries;
    }

    public void makeSummary(){
        ArrayList<SummaryModel> summaries = createSummaryModels();
        summaries = calculateSummaries(summaries);
        SummaryEntity summaryEntity;
        int last_file, last_file_type2;
        if(summaries.size() > 0){
            summaryRepository.deleteAll();
            for (int i = 0; i < summaries.size(); i++){
                summaryEntity = new SummaryEntity();
                last_file = summaries.get(i).getFileUploads().size() - 1;
                last_file_type2 = summaries.get(i).getFileUploadsType2().size() - 1;
                if(summaries.get(i) != null){
                    summaryEntity.setDate(summaries.get(i).getFileUploads().get(last_file).getDate());
                    summaryEntity.setCode(summaries.get(i).getSupplierCode());
                    summaryEntity.setName(summaries.get(i).getSupplierName());
                    summaryEntity.setKgsMilk(summaries.get(i).getFileUploads().get(last_file).getKgs_milk());
                    summaryEntity.setDays(calculateDays(summaries.get(i)));
                    summaryEntity.setAvgDailyMilk(calculateAvgDailyMilk(summaries.get(i)));
                    summaryEntity.setMilkVariation(summaries.get(i).getMilkVariation());
                    summaryEntity.setFat(summaries.get(i).getFileUploadsType2().get(last_file_type2).getFat());
                    summaryEntity.setFatVariation(summaries.get(i).getFatVariation());
                    summaryEntity.setTotalSolids(summaries.get(i).getFileUploadsType2().get(last_file_type2).getTotal_solids());
                    summaryEntity.setTotalSolidsVariation(summaries.get(i).getTotalSolidsVariation());
                    summaryEntity.setMilkPayment(summaries.get(i).getCategoryPayment());
                    summaryEntity.setFatPayment(summaries.get(i).getFatPayment());
                    summaryEntity.setTotalSolidsPayment(summaries.get(i).getTotalSolidsPayment());
                    summaryEntity.setFrenquencyBonus(summaries.get(i).getShiftPayment());
                    summaryEntity.setMilkVarDiscount(summaries.get(i).getDiscountKgsPayment());
                    summaryEntity.setFatVarDiscount(summaries.get(i).getDiscountFatPayment());
                    summaryEntity.setStVarDiscount(summaries.get(i).getDiscountTotalSolidsPayment());
                    summaryEntity.setTotalPayment(summaries.get(i).getTotalPayment());
                    summaryEntity.setRetentionAmmount(summaries.get(i).getTaxRetention());
                    summaryEntity.setFinalPayment(summaries.get(i).getFinalPayment());
                    summaryRepository.save(summaryEntity);
                }
            }
        }
    }

    public ArrayList<SummaryEntity> getSummaries(){
        return (ArrayList<SummaryEntity>) summaryRepository.findAll();
    }

    // Payment Methods

    public float categoryPayment(int kgs_milk, String category) {
        float categoryPayment;
        if (category.equals("A")){
            categoryPayment = (float) kgs_milk * 700;
        }
        else if (category.equals("B")){
            categoryPayment = (float) kgs_milk * 550;
        }
        else if (category.equals("C")){
            categoryPayment = (float) kgs_milk * 400;
        }
        else if (category.equals("D")){
            categoryPayment = (float) kgs_milk * 250;
        }        
        else {      // Error Case
            categoryPayment = 0;
        }
        return categoryPayment;
    }

    public float fatPayment(int kgs_milk, float fat) {
        float fatPayment;
        if (fat >= 0 && fat <= 20){
            fatPayment = (float) kgs_milk * 30;
        }
        else if (fat >= 21 && fat <= 45){
            fatPayment = (float) kgs_milk * 80;
        }
        else if (fat >= 46) {
            fatPayment = (float) kgs_milk * 120;
        }
        else {      // Error Case
            fatPayment = 0;
        }
        return fatPayment;
    }

    public float totalSolidsPayment(int kgs_milk, float total_solids) {
        float totalSolidsPayment;
        if (total_solids >= 0 && total_solids <= 7){
            totalSolidsPayment = (float) kgs_milk * -130;
        }
        else if (total_solids >= 8 && total_solids <= 18){
            totalSolidsPayment = (float) kgs_milk * -90;
        }
        else if (total_solids >= 19 && total_solids <= 35) {
            totalSolidsPayment = (float) kgs_milk * 95;
        }
        else if (total_solids >= 36) {
            totalSolidsPayment = (float) kgs_milk * 150;
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
                    if (summary.getFileUploads().get(last_file).getShift().equals("T") && summary.getFileUploads().get(penultimate_file).getShift().equals("M")){
                        shiftBonus = 0.2f;
                    }
                    else if (summary.getFileUploads().get(last_file).getShift().equals("M") && summary.getFileUploads().get(penultimate_file).getShift().equals("T")){
                        shiftBonus = 0.2f;
                    }
                }
            }
            else{
                if (summary.getFileUploads().get(last_file).getShift().equals("M")){
                    shiftBonus = 0.12f;
                }
                else if (summary.getFileUploads().get(last_file).getShift().equals("T")){
                    shiftBonus = 0.08f;
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

        if (summary.getFileUploads().size() <= 1){
            return variationKgsPayment;
        }
        else {
            if (summary.getFileUploads().get(last_file).getDate().equals(summary.getFileUploads().get(penultimate_file).getDate()) && summary.getFileUploads().size() > 2){
                penultimate_file--;
            }
            float last_kgs_milk = summary.getFileUploads().get(last_file).getKgs_milk();
            float penultimate_kgs_milk = summary.getFileUploads().get(penultimate_file).getKgs_milk();
            summary.setMilkVariation((last_kgs_milk/penultimate_kgs_milk - 1) * 100);
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
        return variationKgsPayment * payment;
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
            summary.setFatVariation((penultimate_fat_data/last_fat_data - 1) * 100);
            
            if (penultimate_fat_data/last_fat_data <= 1.15){
                variationFatPayment = 0.0f;
            }
            else if (penultimate_fat_data/last_fat_data >= 1.16 && penultimate_fat_data/last_fat_data <= 1.25){
                variationFatPayment = 0.12f;
            }
            else if (penultimate_fat_data/last_fat_data >= 1.26 && penultimate_fat_data/last_fat_data <= 1.40){
                variationFatPayment = 0.2f;
            }
            else if (penultimate_fat_data/last_fat_data >= 1.41){
                variationFatPayment = 0.3f;
            }
        }
        return variationFatPayment * payment;
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
            summary.setTotalSolidsVariation((last_total_solids_data/penultimate_total_solids_data - 1) * 100);
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
        return variationTotalSolidsPayment * payment;
    }

    public int calculateDays(SummaryModel summary){
        int days = 0;
        ArrayList<String> dates = new ArrayList<String>();
        
        if (summary.getFileUploads().size() == 0 || summary.getFileUploads() == null){
            return days;
        }
        else{
            dates.add(summary.getFileUploads().get(0).getDate());
            days++;
            for(int i = 1; i < summary.getFileUploads().size(); i++){
                for(int j = 0; j < dates.size(); j++){
                    if (summary.getFileUploads().get(i).getDate().equals(dates.get(j))){
                        break;
                    }
                    else if (j == dates.size() - 1){
                        dates.add(summary.getFileUploads().get(i).getDate());
                        days++;
                    }
                }
            }
        }
        return days;
    }

    public float calculateAvgDailyMilk(SummaryModel summary){
        float avgDailyMilk = 0;
        if (summary.getFileUploads().size() == 0){
            return avgDailyMilk;
        }
        else{
            for (int i = 0; i < summary.getFileUploads().size(); i++){
                avgDailyMilk += summary.getFileUploads().get(i).getKgs_milk();        
            }
            avgDailyMilk = avgDailyMilk/summary.getFileUploads().size();
        }
        return avgDailyMilk;
    }

    public float sumVariations(float shiftPayment, float discountKgsPayment, float discountFatPayment, float discountTotalSolidsPayment) {
        float sumVariations = shiftPayment + discountKgsPayment + discountFatPayment + discountTotalSolidsPayment;

        return sumVariations;
    }

    public float taxRetention(SummaryModel summary, float payment) {
        if (payment >= 950000 && summary.getSupplierRetention()){
            return payment * retentionTaxes;    
        }
        return 0.0f;
    }

}
