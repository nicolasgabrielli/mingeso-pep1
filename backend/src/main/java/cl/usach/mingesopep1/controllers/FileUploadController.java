package cl.usach.mingesopep1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import cl.usach.mingesopep1.services.FileUploadService;

@Controller
@RequestMapping
public class FileUploadController {
    
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/file-upload")
    public String fileUpload() {
        return "file-upload";
    }
}
