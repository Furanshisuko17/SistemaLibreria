package com.utp.trabajo.model.dao;

import com.utp.trabajo.model.entities.Cliente;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClienteDao extends JpaRepository<Cliente, Long> {
    
    //@Query()
    Stream<Cliente> findByIdClienteGreaterThan(Long lastId);
    
    List<Cliente> removeAllByIdClienteIn(Iterable<? extends Long> ids);
    
}
