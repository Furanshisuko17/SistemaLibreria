package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaDao extends JpaRepository<Venta, Long> {

}
