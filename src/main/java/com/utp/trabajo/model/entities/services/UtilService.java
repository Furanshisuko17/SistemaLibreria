package com.utp.trabajo.model.entities.services;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.springframework.stereotype.Service;

@Service
public class UtilService {
	
    public FlatSVGIcon get16x16Icon(String path) {
            FlatSVGIcon icon = new FlatSVGIcon(getClass().getResource(path));
            return icon.derive(16, 16);
    }
	
}
