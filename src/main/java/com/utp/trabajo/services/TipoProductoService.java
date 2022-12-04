/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.TipoProductoDao;
import com.utp.trabajo.model.entities.TipoProducto;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoProductoService {

    @Autowired
    private TipoProductoDao tipoproductoDao;
    
    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public List<TipoProducto> streamTipoProducto(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<TipoProducto> streamedTipoProducto = tipoproductoDao.findByIdTipoProductoGreaterThan(lastId)) {
            return streamedTipoProducto.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public TipoProducto encontrarTipoProductoPorId(Long idMarca) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return tipoproductoDao.findById(idMarca).orElseThrow();
    }

    @Transactional
    public void nuevoTipoProducto(TipoProducto tipoproducto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        tipoproductoDao.save(tipoproducto);

    }

    @Transactional
    public void actualizarTipoProducto(TipoProducto tipoproducto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
    }

    @Transactional
    public List<TipoProducto> eliminarTipoProducto(List<Long> idsTipoProducto) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return tipoproductoDao.removeAllByIdTipoProductoIn(idsTipoProducto);
    }
}
