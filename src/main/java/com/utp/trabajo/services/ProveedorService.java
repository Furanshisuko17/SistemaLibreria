package com.utp.trabajo.services;

import com.utp.trabajo.model.dao.ProveedorDao;
import com.utp.trabajo.model.entities.Proveedor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorDao proveedorDao;

    @Transactional(readOnly = true)
    public List<Proveedor> streamProveedores(Long lastId, Long limit) {
        try ( Stream<Proveedor> streamedProveedores = proveedorDao.findByIdProveedorGreaterThan(lastId)) {
            return streamedProveedores.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Proveedor encontrarProveedorPorId(Long idProveedor) {
        return proveedorDao.findById(idProveedor).orElseThrow();
    }

    @Transactional
    public void nuevoProveedor(Proveedor proveedor) {
        proveedorDao.save(proveedor);

    }

    @Transactional
    public void actualizarProveedor(Proveedor proveedor) {

    }

    @Transactional
    public List<Proveedor> eliminarProveedor(List<Long> idsProveedor) {
        return proveedorDao.removeAllByIdProveedorIn(idsProveedor);
    }
}
