package cl.usach.mingesopep1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.usach.mingesopep1.repositories.FileUploadRepository;

@Service
public class FileUploadService {
    @Autowired
    private FileUploadRepository fileUploadRepository;

    
}
