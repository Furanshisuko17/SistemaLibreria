package com.utp.trabajo.services.transaction;

import com.utp.trabajo.model.dao.VentaDao;
import com.utp.trabajo.model.entities.Venta;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentasService {

    @Autowired
    private VentaDao ventaDao;

    @Transactional(readOnly = true)
    public DefaultTableModel listarVentas() {
        List<Venta> ventas = ventaDao.findAll();

        DefaultTableModel ventasReturned = new DefaultTableModel();
        for (Venta venta : ventas) { //TODO: find better way to retrieve all data
            Object[] values = new Object[9];
            values[0] = venta.getIdVenta();
            values[1] = venta.getFechaEmision();
            values[2] = venta.getComprobante();
            values[3] = venta.getIgv();
            values[4] = venta.getMetodoPago();
            values[5] = venta.getPrecioTotal();
            values[6] = venta.getEmpleado();
            values[7] = venta.getCliente();
            ventasReturned.addRow(values);
        }
        return ventasReturned;

    }
}
