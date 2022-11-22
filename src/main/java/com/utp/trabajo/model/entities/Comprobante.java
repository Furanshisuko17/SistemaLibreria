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
@Table(name = "comprobante")
public class Comprobante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idComprobante;

    @Column(nullable = false)
    public String nombreComprobante;

    @Column(nullable = false)
    public String tipoComprobante;

    //TODO: Implementar un sistema de generacion de comprobantes con PDF's
}
