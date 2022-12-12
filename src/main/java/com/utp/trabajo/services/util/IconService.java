package com.utp.trabajo.services.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IconService {

    @Autowired
    private UtilService utilService;

    public FlatSVGIcon iconoPrincipal;
    public FlatSVGIcon iconoVentas;
    public FlatSVGIcon iconoCompras;
    public FlatSVGIcon iconoAlmacen;
    public FlatSVGIcon iconoEstadisticas;
    public FlatSVGIcon iconoAdministracion;
    public FlatSVGIcon iconoExit;
    public FlatSVGIcon iconoLightMode;
    public FlatSVGIcon iconoDarkMode;
    public FlatSVGIcon iconoCuenta;
    public FlatSVGIcon iconoLogout;
    public FlatSVGIcon iconoProducto;
    public FlatSVGIcon iconoCliente;
    public FlatSVGIcon iconoNuevaVenta;
    public FlatSVGIcon iconoEsperandoBusqueda;
    public FlatSVGIcon iconoBusquedaEncontrada;
    public FlatSVGIcon iconoBusquedaFallida;
    public FlatSVGIcon iconoBuscando;
    
    //public FlatSVGIcon icono;
    /**
     * Inicializa todos los iconos
     */
    @PostConstruct
    public void init() {
        iconoPrincipal = utilService.getIcon("iconoPrincipal", 64);
        iconoVentas = utilService.getIcon("ventasIcono", 16);
        iconoCompras = utilService.getIcon("comprasIcono", 16);
        iconoAlmacen = utilService.getIcon("almacenIcono", 16);
        iconoEstadisticas = utilService.getIcon("estadisticasIcono", 16);
        iconoAdministracion = utilService.getIcon("administracionIcono", 16);
        iconoExit = utilService.getIcon("exit", 16);
        iconoLightMode = utilService.getIcon("lightMode", 16);
        iconoDarkMode = utilService.getIcon("darkMode", 16);
        iconoCuenta = utilService.getIcon("cuentaIcono", 16);
        iconoLogout = utilService.getIcon("logoutIcon", 16);
        iconoProducto = utilService.getIcon("productosIcono", 16);
        iconoCliente = utilService.getIcon("clienteIcono", 16);
        iconoNuevaVenta = utilService.getIcon("nuevaVentaIcono", 16);
        iconoEsperandoBusqueda = utilService.getIcon("busquedaIcono", 16);
        iconoBusquedaEncontrada = utilService.getIcon("busquedaEncontradaIcono", 16);
        iconoBusquedaFallida = utilService.getIcon("busquedaFallidaIcono", 16);
        iconoBuscando = utilService.getIcon("buscandoIcono", 16);
    }

}
