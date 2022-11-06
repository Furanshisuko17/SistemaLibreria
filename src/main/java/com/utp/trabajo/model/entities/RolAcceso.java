package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "rol_acceso")
public class RolAcceso implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRol;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(nullable = false, length = 300) //consider when creating the interface for this
	private String descripcion;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "permisos_concedidos",
		joinColumns = @JoinColumn(name = "idRol"),
		inverseJoinColumns = @JoinColumn(name = "idPermiso"))
	private List<Permiso> permisos;

}
