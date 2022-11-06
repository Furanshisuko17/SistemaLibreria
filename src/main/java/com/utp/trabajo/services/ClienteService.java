package com.utp.trabajo.services;

import com.utp.trabajo.model.dao.ClienteDao;
import com.utp.trabajo.model.entities.Cliente;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteDao clienteDao;
	
	@Transactional(readOnly = true)
	public DefaultTableModel listarClientes() {
		var clientes = clienteDao.findAll();
		
		DefaultTableModel tablaClientes = new DefaultTableModel();
		for(Cliente cliente : clientes) { //TODO: find better way to retrieve all data
			Object[] values = new Object[9];
			values[0] = cliente.getIdCliente();
			values[1] = cliente.getNombre();
			values[2] = cliente.getDireccion();
			values[3] = cliente.getIdentificacion();
			values[4] = cliente.getTelefono();
			values[5] = cliente.getRazonSocial();
			tablaClientes.addRow(values);
		}
		return tablaClientes;
	}
	
}
