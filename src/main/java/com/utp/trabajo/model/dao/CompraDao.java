package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Compra;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraDao extends JpaRepository<Compra, Long> {

    Stream<Compra> findByIdCompraGreaterThan(Long lastId);

    List<Compra> removeAllByIdCompraIn(Iterable<? extends Long> ids);

}
