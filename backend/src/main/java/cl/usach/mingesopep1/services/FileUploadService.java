package cl.usach.mingesopep1.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.usach.mingesopep1.entities.FileUploadEntity;
import cl.usach.mingesopep1.repositories.FileUploadRepository;

@Service
public class FileUploadService {
    @Autowired
    private FileUploadRepository fileUploadRepository;

    private final Path root = Paths.get("uploads");

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta uploads.");
        }
    }


    public void saveFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if(fileName != null){
            if(!file.isEmpty()){
                try {
                    init();
                    Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new RuntimeException("No se pudo guardar el archivo. Error: " + e.getMessage());
                }
            }
        }
    }

    public void readCsv(String fileName) {
        String line = "";
        String csvSplit = ";";
        String root = "uploads/";
        try {
            BufferedReader br = new BufferedReader(new FileReader(root + fileName));
            int firtLine = 1;
            while((line = br.readLine()) != null) {
                if(firtLine == 1) {
                    firtLine++;
                }
                else{
                    String[] data = line.split(csvSplit);
                    FileUploadEntity fileUploadEntity = new FileUploadEntity();
                    fileUploadEntity.setDate(data[0]);
                    fileUploadEntity.setShift(data[1]);
                    fileUploadEntity.setSupplier(data[2]);
                    fileUploadEntity.setKgs_milk(data[3]);
                    fileUploadRepository.save(fileUploadEntity);
                }
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException("No se encontró el archivo. Error: " + e.getMessage());
        } finally {
            System.out.println("Lectura de archivo finalizada.");
        }

    }



    
}
