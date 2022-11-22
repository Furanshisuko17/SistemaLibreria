package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "empleado")
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmpleado;

    @ManyToOne
    @JoinColumn(name = "idRol", referencedColumnName = "idRol", nullable = true)
    private RolAcceso rolAcceso;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, length = 8, unique = true)
    private String dni;

    private String direccion;

    private Timestamp fechaNacimiento;

    @Column(nullable = false)
    private Timestamp fechaContratacion;

    private Timestamp fechaCese;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne
    @JoinColumn(nullable = false, name = "idArea", referencedColumnName = "idArea")
    private Area area;

    @Column(nullable = false)
    private String tipoContratacion;

    @Column(unique = true)
    private String username;

    @Column(length = 70)
    private String encryptedPassword;

    @Column(length = 15)
    private String telefono;
}
