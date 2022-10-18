package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_compra")
public class DetalleCompra implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalleCompra;
	
	private Timestamp fechaSalida;
	
	private Timestamp fechaLlegada;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idCompra", referencedColumnName = "idCompra")
	private Compra compra;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idTipoDistribucion", referencedColumnName = "idTipoDistribucion")
	private TipoDistribucion tipoDistribucion;
	
	@Column(nullable = false)
	private int cantidad;
	
	@Column(nullable = false, precision = 12, scale=2)
	private BigInteger precio;
		
	@ManyToOne
	@JoinColumn(nullable = false, name = "idEstadoCompra", referencedColumnName = "idEstadoCompra")
	private EstadoCompra estadoCompra;
	
	@ManyToOne
	@JoinColumn(name = "idCalidadProducto", referencedColumnName = "idCalidadProducto")
	private CalidadProducto calidadProducto;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idProveedor", referencedColumnName = "idProveedor")
	private Proveedor proveedor;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idProducto", referencedColumnName = "idProducto")
	private Producto producto;
	
	@ManyToOne
	@JoinColumn(name = "idComprobante", referencedColumnName = "idComprobante")
	private Comprobante comprobante;
	
}
