package cl.usach.mingesopep1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/file-upload")
    public String fileUploadPost(
        @RequestParam("file") MultipartFile file, 
        RedirectAttributes redirectAttributes) {
            fileUploadService.saveFile(file);
            redirectAttributes.addFlashAttribute("message", "Archivo Subido con éxito!" + file.getOriginalFilename() + "!");
            fileUploadService.readCsv(file.getOriginalFilename());
            return "redirect:/file-upload";
    }
}
