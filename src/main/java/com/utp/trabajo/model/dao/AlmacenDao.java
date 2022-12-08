package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Query;

public interface AlmacenDao extends JpaRepository<Almacen, Long> {

        Stream<Almacen> findByIdProductoGreaterThan(Long lastId);

    List<Almacen> removeAllByIdProductoIn(Iterable<? extends Long> ids);
}
