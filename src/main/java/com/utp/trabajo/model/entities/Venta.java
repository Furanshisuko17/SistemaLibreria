package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "venta")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    private Timestamp fechaEmision;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<DetallesVenta> detallesVenta;

    @OneToOne
    @JoinColumn(nullable = false, name = "idComprobante", referencedColumnName = "idComprobante")
    private Comprobante comprobante;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigInteger igv;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioTotal;

    @ManyToOne
    @JoinColumn(nullable = false, name = "idEmpleado", referencedColumnName = "idEmpleado")
    private Empleado empleado; //references empleado//

    @ManyToOne
    @JoinColumn(nullable = false, name = "idCliente", referencedColumnName = "idCliente")
    private Cliente cliente;	//references cliente
    
    @ManyToOne
    @JoinColumn(nullable = false, name = "idMetodoPagoVenta", referencedColumnName = "idMetodoPago")
    private MetodoPago metodoPago;

}
