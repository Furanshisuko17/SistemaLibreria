
package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
@Table(name = "detalles_venta")
public class DetallesVenta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetallesVenta;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idVenta", referencedColumnName = "idVenta")
	private Venta venta;
	
	private int cantidad;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idProducto", referencedColumnName = "idProducto")
	private Producto producto;
	
	@Column(precision = 12, scale=2)
	private BigInteger precioUnidad;
	
	@Column(precision = 12, scale=2)
	private BigInteger total;
	//Total se calcula en el programa
}
