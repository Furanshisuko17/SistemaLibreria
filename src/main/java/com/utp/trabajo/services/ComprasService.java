package com.utp.trabajo.services;

import com.utp.trabajo.model.dao.CompraDao;
import com.utp.trabajo.model.entities.Compra;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprasService {

    @Autowired
    private CompraDao compraDao;

    @Transactional(readOnly = true)
    public List<Compra> streamCompras(Long lastId, Long limit) {
        try ( Stream<Compra> streamedCompras = compraDao.findByIdCompraGreaterThan(lastId)) {
            return streamedCompras.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Compra encontrarCompraPorId(Long idCompra) {
        return compraDao.findById(idCompra).orElseThrow();
    }

    @Transactional
    public void nuevaCompra(Compra compra) {
        compraDao.save(compra);

    }

    @Transactional
    public void actualizarCompra(Compra compra) {

    }

    @Transactional
    public List<Compra> eliminarCompra(List<Long> idsCompra) {
        return compraDao.removeAllByIdCompraIn(idsCompra);
    }

}
