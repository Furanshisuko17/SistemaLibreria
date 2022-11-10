
package com.utp.trabajo.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedor")
public class Proveedor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idProveedor;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(nullable = false)
	private String ruc;
	
	@Column(nullable = false)
	private String direccion;
	
	@Column(nullable = false)
	private String telefono;
    
    @Column(length = 30)
    private String tipoComercio; //Minorista o mayorista

}
