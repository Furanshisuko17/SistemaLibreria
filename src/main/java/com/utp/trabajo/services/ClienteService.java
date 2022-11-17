package com.utp.trabajo.services;

import com.utp.trabajo.model.dao.ClienteDao;
import com.utp.trabajo.model.entities.Cliente;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteDao clienteDao;
	   
    @Transactional(readOnly = true)
    public List<Cliente> streamClientes(Long lastId, Long limit) {
        try(Stream<Cliente> streamedClientes = clienteDao.findByIdClienteGreaterThan(lastId)) {
            return streamedClientes.limit(limit)
                    .collect(Collectors.toList());
        }   
    } 
    
    @Transactional(readOnly = true)
    public Cliente encontrarClientePorId(Long idCliente) {
        return clienteDao.findById(idCliente).orElseThrow();
    }
    
    @Transactional
    public void nuevoCliente(Cliente cliente) {
        clienteDao.save(cliente);
        
    }
    
    @Transactional
    public void actualizarCliente(Cliente cliente) {
        
    }
    
    @Transactional
    public List<Cliente> eliminarCliente(List<Long> idsCliente) {
        return clienteDao.removeAllByIdClienteIn(idsCliente);
    }
    
	
}
