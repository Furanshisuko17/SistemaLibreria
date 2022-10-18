package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //TODO: remove this when launched to production
	private Long idProducto;
	
	private String nombre;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idMarca", referencedColumnName = "idMarca")
	private Marca marca;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idTipoProducto", referencedColumnName = "idTipoProducto")
	private TipoProducto tipoProducto;
	
	@OneToOne(mappedBy = "producto")
	private Almacen almacen;
	
	@Column(length = 400)
	private String descripcion;
	
	private Timestamp fechaUltimaVenta;
	
	//TODO: more columns
}
