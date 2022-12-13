package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.ProductoDao;
import com.utp.trabajo.model.entities.Producto;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    @Autowired
    private ProductoDao productoDao;
    
    @Autowired
    private SecurityService securityService;
    
    @Transactional(readOnly = true)
    public List<Producto> streamProductos(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Producto> streamedProductos = productoDao.findByIdProductoGreaterThan(lastId)) {
            return streamedProductos
                .limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Producto encontrarProductoPorId(Long idProducto) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return productoDao.findById(idProducto).orElseThrow();
    }
    
    @Transactional(readOnly = true)
    public List<Producto> encontrarProductosPorNombre(String nombre) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return productoDao.findByNombreIgnoreCaseContaining(nombre);
    }

    @Transactional
    public Producto nuevoProducto(Producto producto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        
        return productoDao.save(producto);
    }
    
    @Transactional
    public List<Producto> actualizarProductos(List<Producto> productos) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de edicion.");
        }
        
        return productoDao.saveAll(productos);
    }

    @Transactional
    public Producto actualizarProducto(Producto producto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
        
        return productoDao.save(producto);
    }

    @Transactional
    public List<Producto> eliminarClientes(List<Long> idsProducto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return productoDao.removeAllByIdProductoIn(idsProducto);
    }

    @Transactional(readOnly = true)
    public long contarProductos() throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        return productoDao.count();
    }
    
}
