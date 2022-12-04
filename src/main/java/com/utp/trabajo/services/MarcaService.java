package com.utp.trabajo.services;

import com.utp.trabajo.model.entities.Marca;
import com.utp.trabajo.model.dao.MarcaDao;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarcaService {

    @Autowired
    private MarcaDao marcaDao;

    @Transactional(readOnly = true)
    public List<Marca> streamMarca(Long lastId, Long limit) {
        try ( Stream<Marca> streamedMarca = marcaDao.findByIdMarcaGreaterThan(lastId)) {
            return streamedMarca.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Marca encontrarMarcaPorId(Long idMarca) {
        return marcaDao.findById(idMarca).orElseThrow();
    }

    @Transactional
    public void nuevaMarca(Marca marca) {
        marcaDao.save(marca);

    }

    @Transactional
    public void actualizarMarca(Marca marca) {

    }

    @Transactional
    public List<Marca> eliminarMarca(List<Long> idsMarca) {
        return marcaDao.removeAllByIdMarcaIn(idsMarca);
    }
}
