package com.utp.trabajo.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.trabajo.model.entities.EstadoCompra;

public interface EstadoCompraDao extends JpaRepository<EstadoCompra, Long> {

}
