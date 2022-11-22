package com.utp.trabajo.model.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "compra")
public class Compra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompra;

    @Column(nullable = false)
    private Timestamp fechaCompra;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigInteger precioTotal;

    @ManyToOne
    @JoinColumn(nullable = false, name = "idMetodoPagoCompra", referencedColumnName = "idMetodoPago")
    private MetodoPago metodoPago;

    private String transporte;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigInteger descuento;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.MERGE)
    private List<DetalleCompra> detallesCompra;

}
