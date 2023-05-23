package cl.usach.mingesopep1.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.usach.mingesopep1.entities.FileUploadEntity;
import cl.usach.mingesopep1.entities.FileUploadEntityType2;
import cl.usach.mingesopep1.repositories.FileUploadRepository;
import cl.usach.mingesopep1.repositories.FileUploadRepositoryType2;

@Service
public class FileUploadService {
    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private FileUploadRepositoryType2 fileUploadRepositoryType2;

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
            int firstLine = 1;
            int format = 0;
            while((line = br.readLine()) != null) {
                if(firstLine == 1) {
                    String[] data = line.split(csvSplit);
                    if(data[0].equals("Proveedor") || data[1].equals("% Grasa") || data[2].equals("% Sólido Total")){
                        format = 2;
                    }
                    else if(data[0].equals("Fecha")|| data[1].equals("Turno") || data[2].equals("Proveedor") || data[3].equals("KLS Leche")){
                        format = 1;
                    }
                    else{
                        br.close();
                        throw new RuntimeException("El archivo no tiene el formato correcto.");
                    }
                    firstLine++;
                }
                else if(firstLine != 1 && format == 1){
                    String[] data = line.split(csvSplit);
                    FileUploadEntity fileUploadEntity = new FileUploadEntity();
                    fileUploadEntity.setDate(data[0]);
                    fileUploadEntity.setShift(data[1]);
                    fileUploadEntity.setSupplier(Integer.parseInt(data[2]));
                    fileUploadEntity.setKgs_milk(Integer.parseInt(data[3]));
                    fileUploadRepository.save(fileUploadEntity);
                }
                else if(firstLine != 1 && format == 2){
                    String[] data = line.split(csvSplit);
                    FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
                    fileUploadEntityType2.setSupplier(Integer.parseInt(data[0]));
                    fileUploadEntityType2.setFat(Float.parseFloat(data[1]));
                    fileUploadEntityType2.setTotal_solids(Float.parseFloat(data[2]));
                    fileUploadRepositoryType2.save(fileUploadEntityType2);
                }
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException("No se encontró el archivo. Error: " + e.getMessage());
        } finally {
            System.out.println("Lectura de archivo finalizada.");
        }
    }

    public List<FileUploadEntity> getAllFiles() {
        return fileUploadRepository.findAll();
    }

    public List<FileUploadEntityType2> getAllFilesType2() {
        return fileUploadRepositoryType2.findAll();
    }

    public void createFileUpload(String date, String shift, int supplier, int kgs_milk) {
        FileUploadEntity fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.setDate(date);
        fileUploadEntity.setShift(shift);
        fileUploadEntity.setSupplier(supplier);
        fileUploadEntity.setKgs_milk(kgs_milk);
        fileUploadRepository.save(fileUploadEntity);
    }

    public void createFileUploadType2(int supplier, float fat, float total_solids) {
        FileUploadEntityType2 fileUploadEntityType2 = new FileUploadEntityType2();
        fileUploadEntityType2.setSupplier(supplier);
        fileUploadEntityType2.setFat(fat);
        fileUploadEntityType2.setTotal_solids(total_solids);
        fileUploadRepositoryType2.save(fileUploadEntityType2);
    }
    
}
