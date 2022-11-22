package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.stream.Stream;

public interface TipoProductoDao extends JpaRepository<TipoProducto, Long> {

    Stream<TipoProducto> findByIdTipoProductoGreaterThan(Long lastId);

    List<TipoProducto> removeAllByIdTipoProductoIn(Iterable<? extends Long> ids);
}
