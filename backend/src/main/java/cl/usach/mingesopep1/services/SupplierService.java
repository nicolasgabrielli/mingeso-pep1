package cl.usach.mingesopep1.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import cl.usach.mingesopep1.repositories.SupplierRepository;
import cl.usach.mingesopep1.entities.SupplierEntity;
import java.util.List;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    public List<SupplierEntity> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public SupplierEntity getSupplierById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    public SupplierEntity createSupplier(SupplierEntity supplier) {
        return supplierRepository.save(new SupplierEntity(supplier.getId(), supplier.getName(), supplier.getCode(), supplier.getCategory(), supplier.getRetention()));
    }

}
