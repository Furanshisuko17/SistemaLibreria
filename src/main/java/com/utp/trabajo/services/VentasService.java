package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.VentaDao;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Venta;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentasService {

    @Autowired
    private VentaDao ventaDao;
   
    @Autowired
    private SecurityService securityService;
    
    @Transactional(readOnly = true)
    public List<Venta> streamVentas(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Venta> streamedVentas = ventaDao.findByIdVentaGreaterThan(lastId)) {
            return streamedVentas.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Venta encontrarVentaPorId(Long idVenta) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return ventaDao.findById(idVenta).orElseThrow();
    }

    @Transactional
    public Venta nuevaVenta(Venta venta) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        
        return ventaDao.save(venta);
    }

    @Transactional
    public Venta actualizarVenta(Venta venta) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
        
        return ventaDao.save(venta);
    }

    @Transactional
    public List<Venta> eliminarVenta(List<Long> idsVenta) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return ventaDao.removeAllByIdVentaIn(idsVenta);
    }

    @Transactional(readOnly = true)
    public long contarVentas() throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return ventaDao.count();
    }
    
}
