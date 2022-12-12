package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.CompraDao;
import com.utp.trabajo.model.dao.EstadoCompraDao;
import com.utp.trabajo.model.entities.Compra;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprasService {

    @Autowired
    private CompraDao compraDao;
    @Autowired
    private EstadoCompraDao estadocompraDao;
    
    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public List<Compra> streamCompras(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Compra> streamedCompras = compraDao.findByIdCompraGreaterThan(lastId)) {
            return streamedCompras.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Compra encontrarCompraPorId(Long idCompra) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        return compraDao.findById(idCompra).orElseThrow();
    }

    @Transactional
    public void nuevaCompra(Compra compra) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        compraDao.save(compra);

    }

    @Transactional
    public void actualizarCompra(Compra compra) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
    }

    @Transactional
    public List<Compra> eliminarCompra(List<Long> idsCompra) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        
        return compraDao.removeAllByIdCompraIn(idsCompra);
    }
    @Transactional(readOnly = true)
    public long contarCompras() throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return compraDao.count();
    }
    
    

}
