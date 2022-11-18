
package com.utp.trabajo.services;

import com.utp.trabajo.model.dao.CompraDao;
import com.utp.trabajo.model.entities.Compra;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprasService {
    @Autowired
    private CompraDao compraDao;
	
	@Transactional(readOnly = true)
	public DefaultTableModel listarCompras() {
		List<Compra> compras = compraDao.findAll();
		
		DefaultTableModel comprasReturned = new DefaultTableModel();
		for(Compra compra : compras) { //TODO: find better way to retrieve all data
			Object[] values = new Object[7];
			values[0] = compra.getIdCompra();
			values[1] = compra.getFechaCompra();
			values[2] = compra.getTransporte();
			values[3] = compra.getDescuento();
			values[4] = compra.getMetodoPago();
			values[5] = compra.getPrecioTotal();
			comprasReturned.addRow(values);
		}
		return comprasReturned;
		
	} 
}
