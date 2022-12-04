package com.utp.trabajo.services;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.dao.ClienteDao;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.services.security.SecurityService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteDao clienteDao;
    
    @Autowired
    private SecurityService securityService;
    
    @Transactional(readOnly = true)
    public List<Cliente> streamClientes(Long lastId, Long limit) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        try ( Stream<Cliente> streamedClientes = clienteDao.findByIdClienteGreaterThan(lastId)) {
            return streamedClientes.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Cliente encontrarClientePorId(Long idCliente) throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return clienteDao.findById(idCliente).orElseThrow();
    }

    @Transactional
    public Cliente nuevoCliente(Cliente cliente) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("create")) {
            throw new NotEnoughPermissionsException("Sin permisos de creación.");
        }
        
        return clienteDao.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(Cliente cliente) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("edit")) {
            throw new NotEnoughPermissionsException("Sin permisos de edición.");
        }
        
        return clienteDao.save(cliente);
    }

    @Transactional
    public List<Cliente> eliminarClientes(List<Long> idsCliente) throws NotEnoughPermissionsException {
        if (!securityService.getPermissions().contains("delete")) {
            throw new NotEnoughPermissionsException("Sin permisos de eliminación.");
        }
        return clienteDao.removeAllByIdClienteIn(idsCliente);
    }

    @Transactional(readOnly = true)
    public long contarClientes() throws NotEnoughPermissionsException {
        
        if (!securityService.getPermissions().contains("read")) {
            throw new NotEnoughPermissionsException("Sin permisos de lectura.");
        }
        
        return clienteDao.count();
    }

}
