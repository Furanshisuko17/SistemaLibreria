package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Producto;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoDao extends JpaRepository<Producto, Long> {
    
    Stream<Producto> findByIdProductoGreaterThan(Long lastId);

    List<Producto> removeAllByIdProductoIn(Iterable<? extends Long> ids);

}
