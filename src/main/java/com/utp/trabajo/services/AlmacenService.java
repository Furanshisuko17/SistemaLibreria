
package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.AlmacenDao;
import com.utp.trabajo.model.entities.Almacen;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlmacenService {
    @Autowired
    private AlmacenDao almacenDao;
    
    @Autowired
    private SecurityService securityService;
    
    @Transactional(readOnly = true)
    public List<Almacen> streamAlmacen(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Almacen> streamedAlmacen = almacenDao.findByIdProductoGreaterThan(lastId)) {
            return streamedAlmacen.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Almacen encontrarAlmacenPorId(Long idProducto) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return almacenDao.findById(idProducto).orElseThrow();
    }

    @Transactional
    public Almacen nuevoAlmacen(Almacen almacen) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        
        return almacenDao.save(almacen);
    }

    @Transactional
    public Almacen actualizarAlmacen(Almacen almacen) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
        
        return almacenDao.save(almacen);
    }

    @Transactional
    public List<Almacen> eliminarAlmacen(List<Long> idsProducto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return almacenDao.removeAllByIdProductoIn(idsProducto);
    }

    @Transactional(readOnly = true)
    public long contarAlmacen() throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return almacenDao.count();
    }
    
}
