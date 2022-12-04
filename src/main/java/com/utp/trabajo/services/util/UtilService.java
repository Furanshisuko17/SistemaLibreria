package com.utp.trabajo.services.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.springframework.stereotype.Service;

/**
 * Carga todos los iconos .svg de la carpeta /icons en la memoria.
 * @author Fran
 */
@Service
public class UtilService {
    
    
    
    public FlatSVGIcon getIcon(String path) {
        FlatSVGIcon icon = new FlatSVGIcon(getClass().getResource("/icons/" + path + ".svg"));
        return icon;
    }

    public FlatSVGIcon getIcon(String path, int size) {
        FlatSVGIcon icon = new FlatSVGIcon(getClass().getResource("/icons/" + path + ".svg"));
        return icon.derive(size, size);
    }

}
