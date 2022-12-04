/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utp.trabajo.services;

import com.utp.trabajo.model.dao.TipoProductoDao;
import com.utp.trabajo.model.entities.TipoProducto;
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

    @Transactional(readOnly = true)
    public List<TipoProducto> streamTipoProducto(Long lastId, Long limit) {
        try ( Stream<TipoProducto> streamedTipoProducto = tipoproductoDao.findByIdTipoProductoGreaterThan(lastId)) {
            return streamedTipoProducto.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public TipoProducto encontrarTipoProductoPorId(Long idMarca) {
        return tipoproductoDao.findById(idMarca).orElseThrow();
    }

    @Transactional
    public void nuevoTipoProducto(TipoProducto tipoproducto) {
        tipoproductoDao.save(tipoproducto);

    }

    @Transactional
    public void actualizarTipoProducto(TipoProducto tipoproducto) {

    }

    @Transactional
    public List<TipoProducto> eliminarTipoProducto(List<Long> idsTipoProducto) {
        return tipoproductoDao.removeAllByIdTipoProductoIn(idsTipoProducto);
    }
}
