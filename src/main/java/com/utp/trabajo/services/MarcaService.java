package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Marca;
import com.utp.trabajo.model.dao.MarcaDao;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarcaService {

    @Autowired
    private MarcaDao marcaDao;
    
    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public List<Marca> streamMarca(Long lastId, Long limit) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Marca> streamedMarca = marcaDao.findByIdMarcaGreaterThan(lastId)) {
            return streamedMarca.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Marca encontrarMarcaPorId(Long idMarca) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return marcaDao.findById(idMarca).orElseThrow();
    }

    @Transactional
    public Marca nuevaMarca(Marca marca) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        return marcaDao.save(marca);
    }

    @Transactional
    public Marca actualizarMarca(Marca marca) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
        return marcaDao.save(marca);
    }

    @Transactional
    public List<Marca> eliminarMarca(List<Long> idsMarca) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return marcaDao.removeAllByIdMarcaIn(idsMarca);
    }
}
