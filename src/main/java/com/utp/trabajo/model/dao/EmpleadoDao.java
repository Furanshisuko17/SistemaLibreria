
package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmpleadoDao extends JpaRepository<Empleado, Long>{

}
