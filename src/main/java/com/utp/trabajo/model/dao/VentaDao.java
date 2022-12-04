package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Venta;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaDao extends JpaRepository<Venta, Long> {
    
    Stream<Venta> findByIdVentaGreaterThan(Long lastId);

    List<Venta> removeAllByIdVentaIn(Iterable<? extends Long> ids);

}
