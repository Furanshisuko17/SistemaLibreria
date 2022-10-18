
package com.utp.trabajo.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "almacen")
public class Almacen implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long idProducto;
	
	@Column(nullable = false)
	private Long stock;
	
	@Column(nullable = false)
	private int stockInicial;
	
	@Column(nullable = false)
	private int stockMinimo;	
	
	@OneToOne
	@JoinColumn(name = "idProducto", referencedColumnName = "idProducto")
	@MapsId
	private Producto producto;
	
	@Column(nullable = false, length = 5)
	private String estanteria;
	
	@Column(nullable = false)
	private int columna;
	
	@Column(nullable = false)
	private int fila;
}
