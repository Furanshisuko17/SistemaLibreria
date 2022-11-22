package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Marca;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarcaDao extends JpaRepository<Marca, Long> {

    Stream<Marca> findByIdMarcaGreaterThan(Long lastId);

    List<Marca> removeAllByIdMarcaIn(Iterable<? extends Long> ids);
}
