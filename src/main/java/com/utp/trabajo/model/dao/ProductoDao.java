package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoDao extends JpaRepository<Producto, Long> {

}
