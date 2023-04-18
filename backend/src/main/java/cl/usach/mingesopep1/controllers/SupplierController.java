package cl.usach.mingesopep1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.usach.mingesopep1.services.SupplierService;
import cl.usach.mingesopep1.entities.SupplierEntity;

import java.util.List;


@RestController
@RequestMapping("/api")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/suppliers")
    public ResponseEntity<List<SupplierEntity>> getAllSuppliers() {
        List<SupplierEntity> suppliers = supplierService.getAllSuppliers();
        if (suppliers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<SupplierEntity> getSupplierById(@PathVariable Long id) {
        SupplierEntity supplier = supplierService.getSupplierById(id);
        if (supplier == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @PostMapping("/suppliers/create")
    public ResponseEntity<SupplierEntity> createSupplier(@RequestBody SupplierEntity supplier) {
        SupplierEntity newSupplier = supplierService.createSupplier(supplier);
        return new ResponseEntity<>(newSupplier, HttpStatus.CREATED);
    }
    
}
