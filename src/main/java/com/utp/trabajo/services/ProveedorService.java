package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.ProveedorDao;
import com.utp.trabajo.model.entities.Proveedor;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorDao proveedorDao;
    
    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public List<Proveedor> streamProveedores(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Proveedor> streamedProveedores = proveedorDao.findByIdProveedorGreaterThan(lastId)) {
            return streamedProveedores.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Proveedor encontrarProveedorPorId(Long idProveedor) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        return proveedorDao.findById(idProveedor).orElseThrow();
    }

    @Transactional
    public Proveedor nuevoProveedor(Proveedor proveedor) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        return proveedorDao.save(proveedor);

    }

    @Transactional
    public  Proveedor actualizarProveedor(Proveedor proveedor) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
        return proveedorDao.save(proveedor);
        
    }

    @Transactional
    public List<Proveedor> eliminarProveedor(List<Long> idsProveedor) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return proveedorDao.removeAllByIdProveedorIn(idsProveedor);
    }
        @Transactional(readOnly = true)
    public long contarProveedores() throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return proveedorDao.count();
    }
}
