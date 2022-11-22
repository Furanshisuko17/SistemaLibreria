package com.utp.trabajo.services.transaction;

import com.utp.trabajo.model.dao.ClienteDao;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.services.security.SecurityService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase de transaccion para la entidad de Clientes Esta clase se comunica
 * directamente con la base de datos No está pensada para ser usada directamente
 * por las interfaces de usuario.
 *
 * @author Fran
 */
@Service
public class ClienteTransaction {

    @Autowired
    private ClienteDao clienteDao;

    @Transactional(readOnly = true)
    public List<Cliente> streamClientes(Long lastId, Long limit) {
        try ( Stream<Cliente> streamedClientes = clienteDao.findByIdClienteGreaterThan(lastId)) {
            return streamedClientes.limit(limit)
                .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Cliente encontrarClientePorId(Long idCliente) {
        return clienteDao.findById(idCliente).orElseThrow();
    }

    @Transactional
    public Cliente nuevoCliente(Cliente cliente) {
        return clienteDao.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(Cliente cliente) {
        return clienteDao.save(cliente);
    }

    @Transactional
    public List<Cliente> eliminarClientes(List<Long> idsCliente) {
        return clienteDao.removeAllByIdClienteIn(idsCliente);
    }

    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteDao.count();
    }

}
