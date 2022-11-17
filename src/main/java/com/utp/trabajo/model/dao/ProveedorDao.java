
package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Proveedor;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProveedorDao extends JpaRepository<Proveedor, Long>{
    Stream<Proveedor> findByIdProveedorGreaterThan(Long lastId);

}
