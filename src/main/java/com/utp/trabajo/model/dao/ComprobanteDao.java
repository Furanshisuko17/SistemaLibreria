package com.utp.trabajo.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.trabajo.model.entities.Comprobante;

public interface ComprobanteDao extends JpaRepository<Comprobante, Long> {

}
