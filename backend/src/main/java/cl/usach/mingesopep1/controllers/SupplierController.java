package cl.usach.mingesopep1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import cl.usach.mingesopep1.services.SupplierService;
import cl.usach.mingesopep1.entities.SupplierEntity;

import java.util.List;


@Controller
@RequestMapping
public class SupplierController {

    @Autowired
    private SupplierService supplierService;


    @GetMapping("/suppliers")
    public String getAllSuppliers(Model model) {
        List<SupplierEntity> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        return "supplier-list";
    }

    @GetMapping("/suppliers/create")
    public String createSupplierForm() {
        return "supplier-create";
    }

    @PostMapping("/suppliers/create")
    public String createSupplier(@RequestParam("name") String name, 
            @RequestParam("code") String code, 
            @RequestParam("category") String category, 
            @RequestParam("retention") String retention) {

        supplierService.createSupplier(name, code, category, retention);
        return "redirect:/suppliers/create";
    }
    
}
