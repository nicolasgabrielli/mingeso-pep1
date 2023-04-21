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

    public void createSupplier(String name, String code, String category, String retention) {
        SupplierEntity supplier = new SupplierEntity();
        supplier.setName(name);
        supplier.setCode(code);
        supplier.setCategory(category);
        supplier.setRetention(retention);
        supplierRepository.save(supplier);
    }

}
