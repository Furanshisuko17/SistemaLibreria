
package com.utp.trabajo.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_distribucion")
public class TipoDistribucion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long idTipoDistribucion;
	
	@OneToOne
	@JoinColumn(nullable = false, name = "idTipoProducto", referencedColumnName = "idTipoProducto")
	public TipoProducto tipoProducto;
	
	@Column(nullable = false)
	public String nombreEmpaquetado;
	
	@Column(nullable = false)
	public int cantidad;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "idMarca", referencedColumnName = "idMarca")
	private Marca marca;
	
}
