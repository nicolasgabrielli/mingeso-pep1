package cl.usach.mingesopep1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cl.usach.mingesopep1.models.SummaryModel;
import cl.usach.mingesopep1.services.SummaryService;
import cl.usach.mingesopep1.entities.FileUploadEntity;
import cl.usach.mingesopep1.entities.FileUploadEntityType2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

@SpringBootTest
class SummaryTest {
    @Autowired
    private SummaryService summaryService;

    // No files case.
    @Test
    public void testCalculateSummaryModel1() {
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1003);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        summaryModel = summaryService.calculateSummaryModel(summaryModel);

        assertNull(summaryModel);
    }

    // One file case. No Type 2.
    @Test
    public void testCalculateSummaryModel2() {
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1003);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("M");
        fileUploadEntity.setSupplier(1003);
        fileUploadEntity.setKgs_milk(100);
        summaryModel.getFileUploads().add(fileUploadEntity);

        summaryModel = summaryService.calculateSummaryModel(summaryModel);

        assertNull(summaryModel);
    }

    // One file case. One Type 2.
    @Test
    public void testCalculateSummaryModel3() {
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("M");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        summaryModel = summaryService.calculateSummaryModel(summaryModel);

        System.out.println(summaryModel.getFinalPayment());
        assertEquals(57288.0f, summaryModel.getFinalPayment());
    }

    @Test
    public void testCalculateSummaries1() {
        ArrayList<SummaryModel> summaries = new ArrayList<SummaryModel>();
        summaries = summaryService.calculateSummaries(summaries);
        assertEquals(0, summaries.size());
    }

    @Test
    public void testCalculateSummaries2() {
        ArrayList<SummaryModel> summaries = new ArrayList<SummaryModel>();
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("M");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        summaries.add(summaryModel);

        summaries = summaryService.calculateSummaries(summaries);

        System.out.println(summaries.get(0).getFinalPayment());
        assertEquals(57288.0f, summaries.get(0).getFinalPayment());
    }

    @Test
    public void testCategoryPayment1(){
        int kgs_milk = 100;
        String category = "D";

        float categoryPayment = summaryService.categoryPayment(kgs_milk, category);

        assertEquals(25000.0f, categoryPayment);
    }

    @Test
    public void testCategoryPayment2(){
        int kgs_milk = 100;
        String category = "E";

        float categoryPayment = summaryService.categoryPayment(kgs_milk, category);

        assertEquals(0.0f, categoryPayment);
    }

    @Test
    public void testCategoryPayment3(){
        int kgs_milk = 100;
        String category = "A";

        float categoryPayment = summaryService.categoryPayment(kgs_milk, category);

        assertEquals(70000.0f, categoryPayment);
    }

    @Test
    public void testCategoryPayment4(){
        int kgs_milk = 100;
        String category = "B";

        float categoryPayment = summaryService.categoryPayment(kgs_milk, category);

        assertEquals(55000.0f, categoryPayment);
    }

    @Test
    public void testCategoryPayment5(){
        int kgs_milk = 100;
        String category = "C";

        float categoryPayment = summaryService.categoryPayment(kgs_milk, category);

        assertEquals(40000.0f, categoryPayment);
    }

    @Test
    public void testFatPayment1(){
        int kgs_milk = 100;
        float fat = 22.0f;

        float fatPayment = summaryService.fatPayment(kgs_milk, fat);

        assertEquals(8000.0f, fatPayment);
    }

    @Test
    public void testFatPayment2(){
        int kgs_milk = 100;
        float fat = -1.0f;

        float fatPayment = summaryService.fatPayment(kgs_milk, fat);

        assertEquals(0.0f, fatPayment);
    }

    @Test
    public void testFatPayment3(){
        int kgs_milk = 100;
        float fat = 17.0f;

        float fatPayment = summaryService.fatPayment(kgs_milk, fat);

        assertEquals(3000.0f, fatPayment);
    }

    @Test
    public void testFatPayment4(){
        int kgs_milk = 100;
        float fat = 50.0f;

        float fatPayment = summaryService.fatPayment(kgs_milk, fat);

        assertEquals(12000.0f, fatPayment);
    }

    @Test
    public void testTotalSolidsPayment1(){
        int kgs_milk = 100;
        float total_solids = 36.0f;

        float totalSolidsPayment = summaryService.totalSolidsPayment(kgs_milk, total_solids);

        assertEquals(15000.0f, totalSolidsPayment);
    }

    @Test
    public void testTotalSolidsPayment2(){
        int kgs_milk = 100;
        float total_solids = -1.0f;

        float totalSolidsPayment = summaryService.totalSolidsPayment(kgs_milk, total_solids);

        assertEquals(0.0f, totalSolidsPayment);
    }

    @Test
    public void testTotalSolidsPayment3(){
        int kgs_milk = 100;
        float total_solids = 0.0f;

        float totalSolidsPayment = summaryService.totalSolidsPayment(kgs_milk, total_solids);

        assertEquals(-13000.0f, totalSolidsPayment);
    }

    @Test
    public void testTotalSolidsPayment4(){
        int kgs_milk = 100;
        float total_solids = 10.0f;

        float totalSolidsPayment = summaryService.totalSolidsPayment(kgs_milk, total_solids);

        assertEquals(-9000.0f, totalSolidsPayment);
    }

    @Test
    public void testTotalSolidsPayment5(){
        int kgs_milk = 100;
        float total_solids = 20.0f;

        float totalSolidsPayment = summaryService.totalSolidsPayment(kgs_milk, total_solids);

        assertEquals(9500.0f, totalSolidsPayment);
    }

    @Test
    public void testShiftPayment1(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("M");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float shiftPayment = summaryService.shiftPayment(summaryModel, payment);

        assertEquals(12.0f, shiftPayment);
    }

    @Test
    public void testShiftPayment2(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float shiftPayment = summaryService.shiftPayment(summaryModel, payment);

        assertEquals(8.0f, shiftPayment);
    }

    @Test
    public void testShiftPayment3(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity1 = new FileUploadEntity();
        fileUploadEntity1.setDate("18-03-2023");
        fileUploadEntity1.setShift("M");
        fileUploadEntity1.setSupplier(1025);
        fileUploadEntity1.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity1);

        FileUploadEntity fileUploadEntity2 = new FileUploadEntity();
        fileUploadEntity2.setDate("18-03-2023");
        fileUploadEntity2.setShift("T");
        fileUploadEntity2.setSupplier(1025);
        fileUploadEntity2.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity2);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float shiftPayment = summaryService.shiftPayment(summaryModel, payment);

        assertEquals(20.0f, shiftPayment);
    }

    @Test
    public void testShiftPayment4(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity1 = new FileUploadEntity();
        fileUploadEntity1.setDate("18-03-2023");
        fileUploadEntity1.setShift("T");
        fileUploadEntity1.setSupplier(1025);
        fileUploadEntity1.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity1);

        FileUploadEntity fileUploadEntity2 = new FileUploadEntity();
        fileUploadEntity2.setDate("19-03-2023");
        fileUploadEntity2.setShift("M");
        fileUploadEntity2.setSupplier(1025);
        fileUploadEntity2.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity2);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float shiftPayment = summaryService.shiftPayment(summaryModel, payment);

        assertEquals(12.0f, shiftPayment);
    }
    
    @Test
    public void testDiscountKgsPayment1(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity1 = new FileUploadEntity();
        fileUploadEntity1.setDate("18-03-2023");
        fileUploadEntity1.setShift("M");
        fileUploadEntity1.setSupplier(1025);
        fileUploadEntity1.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity1);

        FileUploadEntity fileUploadEntity2 = new FileUploadEntity();
        fileUploadEntity2.setDate("18-03-2023");
        fileUploadEntity2.setShift("T");
        fileUploadEntity2.setSupplier(1025);
        fileUploadEntity2.setKgs_milk(36);
        summaryModel.getFileUploads().add(fileUploadEntity2);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float discountKgsPayment = summaryService.discountKgsPayment(summaryModel, payment);

        assertEquals(0.0f, (float)((int)(discountKgsPayment *1000f))/1000f);

    }

    @Test
    public void testDiscountKgsPayment2(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity1 = new FileUploadEntity();
        fileUploadEntity1.setDate("18-03-2023");
        fileUploadEntity1.setShift("M");
        fileUploadEntity1.setSupplier(1025);
        fileUploadEntity1.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity1);

        FileUploadEntity fileUploadEntity2 = new FileUploadEntity();
        fileUploadEntity2.setDate("19-03-2023");
        fileUploadEntity2.setShift("T");
        fileUploadEntity2.setSupplier(1025);
        fileUploadEntity2.setKgs_milk(39);
        summaryModel.getFileUploads().add(fileUploadEntity2);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float discountKgsPayment = summaryService.discountKgsPayment(summaryModel, payment);

        assertEquals(7.0f, (float)((int)(discountKgsPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountKgsPayment3(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity1 = new FileUploadEntity();
        fileUploadEntity1.setDate("18-03-2023");
        fileUploadEntity1.setShift("M");
        fileUploadEntity1.setSupplier(1025);
        fileUploadEntity1.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity1);

        FileUploadEntity fileUploadEntity2 = new FileUploadEntity();
        fileUploadEntity2.setDate("19-03-2023");
        fileUploadEntity2.setShift("T");
        fileUploadEntity2.setSupplier(1025);
        fileUploadEntity2.setKgs_milk(45);
        summaryModel.getFileUploads().add(fileUploadEntity2);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float discountKgsPayment = summaryService.discountKgsPayment(summaryModel, payment);
        assertEquals(15.0f, (float)((int)(discountKgsPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountKgsPayment4(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity1 = new FileUploadEntity();
        fileUploadEntity1.setDate("18-03-2023");
        fileUploadEntity1.setShift("M");
        fileUploadEntity1.setSupplier(1025);
        fileUploadEntity1.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity1);

        FileUploadEntity fileUploadEntity2 = new FileUploadEntity();
        fileUploadEntity2.setDate("19-03-2023");
        fileUploadEntity2.setShift("M");
        fileUploadEntity2.setSupplier(1025);
        fileUploadEntity2.setKgs_milk(45);
        summaryModel.getFileUploads().add(fileUploadEntity2);

        FileUploadEntity fileUploadEntity3 = new FileUploadEntity();
        fileUploadEntity3.setDate("19-03-2023");
        fileUploadEntity3.setShift("T");
        fileUploadEntity3.setSupplier(1025);
        fileUploadEntity3.setKgs_milk(100);
        summaryModel.getFileUploads().add(fileUploadEntity3);

        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(1025);
        fileUploadEntityType2.setFat(30.0f);
        fileUploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(fileUploadEntityType2);

        float discountKgsPayment = summaryService.discountKgsPayment(summaryModel, payment);
        assertEquals(30.0f, (float)((int)(discountKgsPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountFatPayment1(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        float discountFatPayment = summaryService.discountFatPayment(summaryModel, payment);

        assertEquals(0.0f, (float)((int)(discountFatPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountFatPayment2(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        FileUploadEntityType2 file2UploadEntityType2 = new FileUploadEntityType2();
        file2UploadEntityType2.setSupplier(1025);
        file2UploadEntityType2.setFat(30.0f);
        file2UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file2UploadEntityType2);

        float discountFatPayment = summaryService.discountFatPayment(summaryModel, payment);

        assertEquals(0.0f, (float)((int)(discountFatPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountFatPayment3(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(40.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        FileUploadEntityType2 file2UploadEntityType2 = new FileUploadEntityType2();
        file2UploadEntityType2.setSupplier(1025);
        file2UploadEntityType2.setFat(30.0f);
        file2UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file2UploadEntityType2);

        float discountFatPayment = summaryService.discountFatPayment(summaryModel, payment);

        assertEquals(20.0f, (float)((int)(discountFatPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountFatPayment4(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(40.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        FileUploadEntityType2 file2UploadEntityType2 = new FileUploadEntityType2();
        file2UploadEntityType2.setSupplier(1025);
        file2UploadEntityType2.setFat(10.0f);
        file2UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file2UploadEntityType2);

        float discountFatPayment = summaryService.discountFatPayment(summaryModel, payment);

        assertEquals(30.0f, (float)((int)(discountFatPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountTotalSolidsPayment1(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        float discountTotalSolidsPayment = summaryService.discountTotalSolidsPayment(summaryModel, payment);

        assertEquals(0.0f, (float)((int)(discountTotalSolidsPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountTotalSolidsPayment2(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        FileUploadEntityType2 file2UploadEntityType2 = new FileUploadEntityType2();
        file2UploadEntityType2.setSupplier(1025);
        file2UploadEntityType2.setFat(30.0f);
        file2UploadEntityType2.setTotal_solids(51.0f);
        summaryModel.getFileUploadsType2().add(file2UploadEntityType2);

        float discountTotalSolidsPayment = summaryService.discountTotalSolidsPayment(summaryModel, payment);

        assertEquals(0.0f, (float)((int)(discountTotalSolidsPayment *1000f))/1000f);
    }

    @Test
    public void testDiscountTotalSolidsPayment3(){
        float payment = 100.0f;

        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(40.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);
        
        FileUploadEntityType2 file2UploadEntityType2 = new FileUploadEntityType2();
        file2UploadEntityType2.setSupplier(1025);
        file2UploadEntityType2.setFat(30.0f);
        file2UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file2UploadEntityType2);

        float discountTotalSolidsPayment = summaryService.discountTotalSolidsPayment(summaryModel, payment);

        assertEquals(27.0f, (float)((int)(discountTotalSolidsPayment *1000f))/1000f);
    }

    @Test
    public void testCalculateDays1(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        int days = summaryService.calculateDays(summaryModel);

        assertEquals(1, days);
    }

    @Test
    public void testCalculateDays2(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity file1UploadEntity = new FileUploadEntity();
        file1UploadEntity.setDate("18-03-2023");
        file1UploadEntity.setShift("T");
        file1UploadEntity.setSupplier(1025);
        file1UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file1UploadEntity);

        FileUploadEntity file2UploadEntity = new FileUploadEntity();
        file2UploadEntity.setDate("19-03-2023");
        file2UploadEntity.setShift("T");
        file2UploadEntity.setSupplier(1025);
        file2UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file2UploadEntity);

        FileUploadEntity file3UploadEntity = new FileUploadEntity();
        file3UploadEntity.setDate("20-03-2023");
        file3UploadEntity.setShift("T");
        file3UploadEntity.setSupplier(1025);
        file3UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file3UploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        int days = summaryService.calculateDays(summaryModel);

        assertEquals(3, days);
    }

    @Test
    public void testCalculateDays3(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        int days = summaryService.calculateDays(summaryModel);

        assertEquals(0, days);
    }

    @Test
    public void testCalculateDays4(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity file1UploadEntity = new FileUploadEntity();
        file1UploadEntity.setDate("19-03-2023");
        file1UploadEntity.setShift("M");
        file1UploadEntity.setSupplier(1025);
        file1UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file1UploadEntity);

        FileUploadEntity file2UploadEntity = new FileUploadEntity();
        file2UploadEntity.setDate("19-03-2023");
        file2UploadEntity.setShift("T");
        file2UploadEntity.setSupplier(1025);
        file2UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file2UploadEntity);

        FileUploadEntity file3UploadEntity = new FileUploadEntity();
        file3UploadEntity.setDate("20-03-2023");
        file3UploadEntity.setShift("M");
        file3UploadEntity.setSupplier(1025);
        file3UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file3UploadEntity);

        FileUploadEntity file4UploadEntity = new FileUploadEntity();
        file4UploadEntity.setDate("20-03-2023");
        file4UploadEntity.setShift("T");
        file4UploadEntity.setSupplier(1025);
        file4UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file4UploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        int days = summaryService.calculateDays(summaryModel);

        assertEquals(2, days);
    }

    @Test
    public void testCalculateAvgDailyMilk1(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        float avgDailyMilk = summaryService.calculateAvgDailyMilk(summaryModel);

        assertEquals(35.0f, avgDailyMilk);
    }

    @Test
    public void testCalculateAvgDailyMilk2(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        float avgDailyMilk = summaryService.calculateAvgDailyMilk(summaryModel);

        assertEquals(0.0f, avgDailyMilk);
    }
    
    @Test
    public void testCalculateAvgDailyMilk3(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity file1UploadEntity = new FileUploadEntity();
        file1UploadEntity.setDate("18-03-2023");
        file1UploadEntity.setShift("M");
        file1UploadEntity.setSupplier(1025);
        file1UploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(file1UploadEntity);

        FileUploadEntity file2UploadEntity = new FileUploadEntity();
        file2UploadEntity.setDate("18-03-2023");
        file2UploadEntity.setShift("T");
        file2UploadEntity.setSupplier(1025);
        file2UploadEntity.setKgs_milk(70);
        summaryModel.getFileUploads().add(file2UploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        float avgDailyMilk = summaryService.calculateAvgDailyMilk(summaryModel);

        assertEquals(52.5f, avgDailyMilk);
    }

    @Test
    public void testCalculateAvgDailyMilk4(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity file1UploadEntity = new FileUploadEntity();
        file1UploadEntity.setDate("18-03-2023");
        file1UploadEntity.setShift("M");
        file1UploadEntity.setSupplier(1025);
        file1UploadEntity.setKgs_milk(10);
        summaryModel.getFileUploads().add(file1UploadEntity);

        FileUploadEntity file2UploadEntity = new FileUploadEntity();
        file2UploadEntity.setDate("18-03-2023");
        file2UploadEntity.setShift("T");
        file2UploadEntity.setSupplier(1025);
        file2UploadEntity.setKgs_milk(20);
        summaryModel.getFileUploads().add(file2UploadEntity);

        FileUploadEntity file3UploadEntity = new FileUploadEntity();
        file3UploadEntity.setDate("19-03-2023");
        file3UploadEntity.setShift("M");
        file3UploadEntity.setSupplier(1025);
        file3UploadEntity.setKgs_milk(30);
        summaryModel.getFileUploads().add(file3UploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        float avgDailyMilk = summaryService.calculateAvgDailyMilk(summaryModel);

        assertEquals(20.0f, avgDailyMilk);
    }

    @Test
    public void testSumVariations(){
        float shiftPayment = 1.0f;
        float discountKgsPayment = 2.0f;
        float discountFatPayment = 3.0f;
        float discountTotalSolidsPayment = 4.0f;

        float sumVariations = summaryService.sumVariations(shiftPayment, discountKgsPayment, discountFatPayment, discountTotalSolidsPayment);

        assertEquals(10.0f, sumVariations);
    }

    @Test
    public void testTaxRetention1(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(false);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        float payment = 1000000.0f;
        
        float taxRetention = summaryService.taxRetention(summaryModel, payment);

        assertEquals(0.0f, taxRetention);
    }

    @Test
    public void testTaxRetention2(){
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setSupplierCode(1025);
        summaryModel.setSupplierCategory("A");
        summaryModel.setSupplierName("Nicolás Gabrielli");
        summaryModel.setSupplierRetention(true);
        summaryModel.setFileUploads(new ArrayList<FileUploadEntity>());
        summaryModel.setFileUploadsType2(new ArrayList<FileUploadEntityType2>());

        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate("18-03-2023");
        fileUploadEntity.setShift("T");
        fileUploadEntity.setSupplier(1025);
        fileUploadEntity.setKgs_milk(35);
        summaryModel.getFileUploads().add(fileUploadEntity);

        FileUploadEntityType2 file1UploadEntityType2 = new FileUploadEntityType2();
        file1UploadEntityType2.setSupplier(1025);
        file1UploadEntityType2.setFat(30.0f);
        file1UploadEntityType2.setTotal_solids(50.0f);
        summaryModel.getFileUploadsType2().add(file1UploadEntityType2);

        float payment = 1000000.0f;
        
        float taxRetention = summaryService.taxRetention(summaryModel, payment);

        assertEquals(payment*0.13f, taxRetention);
    }

    @Test
    public void testMakeSummary(){
        makeSummary();
    }

    @Test
    public void testCreateSummaryModels(){
        ArrayList<SummaryModel> summaryModels = summaryService.createSummaryModels();
    }
}
