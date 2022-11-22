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
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "registro_almacen")
public class RegistroAlmacen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistroAlmacen;

    @ManyToOne
    @JoinColumn(nullable = false, name = "idProducto", referencedColumnName = "idProducto")
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private Timestamp fechaHora;

    @Column(nullable = false)
    private boolean tipoMovimiento; //0: salida, 1: entrada

    @ManyToOne
    @JoinColumn(nullable = false, name = "idArea", referencedColumnName = "idArea")
    private Area area;

}
