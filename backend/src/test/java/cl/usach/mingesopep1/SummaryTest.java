package cl.usach.mingesopep1;

import org.checkerframework.checker.units.qual.s;
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
public class SummaryTest {
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
        fileUploadEntity2.setKgs_milk(35);
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
        fileUploadEntity2.setKgs_milk(35);
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
    public void testDiscountFatPayment1(){
        
    }
}
