package com.utp.trabajo.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.trabajo.model.entities.EstadoCompra;
import java.util.List;
import java.util.stream.Stream;

public interface EstadoCompraDao extends JpaRepository<EstadoCompra, Long> {
    Stream<EstadoCompra> findByIdEstadoCompraGreaterThan(Long lastId);

    List<EstadoCompra> removeAllByIdEstadoCompraIn(Iterable<? extends Long> ids);

}
