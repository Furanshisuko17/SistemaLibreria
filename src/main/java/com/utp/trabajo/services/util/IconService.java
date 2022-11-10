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
    
	//public FlatSVGIcon icono;
	
	/**
	 * Inicializa todos los iconos
	 */
	@PostConstruct
	public void init() {
		iconoPrincipal = utilService.getIcon("/icons/iconoPrincipal.svg",  64);
		iconoVentas = utilService.getIcon("/icons/ventasIcono.svg", 16);
		iconoCompras = utilService.getIcon("/icons/comprasIcono.svg", 16);
		iconoAlmacen = utilService.getIcon("/icons/almacenIcono.svg", 16);
		iconoEstadisticas = utilService.getIcon("/icons/estadisticasIcono.svg", 16);
		iconoAdministracion = utilService.getIcon("/icons/administracionIcono.svg", 16);
		iconoExit = utilService.getIcon("/icons/exit.svg", 16);
		iconoLightMode = utilService.getIcon("/icons/lightMode.svg", 16);
		iconoDarkMode = utilService.getIcon("/icons/darkMode.svg", 16);
        iconoCuenta = utilService.getIcon("/icons/cuentaIcono.svg", 16);
        iconoLogout = utilService.getIcon("/icons/logoutIcon.svg", 16);
	}
	
	
	
}
