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
@Table(name = "cliente")
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCliente;
	
	@Column(length = 15) // have to create a unique key ,unique = true
	private String identificacion;
	
	private String nombre;
	
	private String direccion;
	
	@Column(length = 20)
	private String telefono;
	
	@Column(nullable = false)
	private String razonSocial;
    
    @Column(nullable = true)
    private Long numeroCompras; //cantidad de compras que ha hecho el cliente
	
	
	//private String 
	
	
}
