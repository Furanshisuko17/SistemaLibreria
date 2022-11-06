package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteDao extends JpaRepository<Cliente, Long> {

}
