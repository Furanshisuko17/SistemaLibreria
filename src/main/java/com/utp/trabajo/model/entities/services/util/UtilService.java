package com.utp.trabajo.model.entities.services.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.springframework.stereotype.Service;

@Service
public class UtilService {
	
	public FlatSVGIcon getIcon(String path) {
		FlatSVGIcon icon = new FlatSVGIcon(getClass().getResource(path));
		return icon;
	}
	
	public FlatSVGIcon getIcon(String path, int size) {
		FlatSVGIcon icon = new FlatSVGIcon(getClass().getResource(path));
		return icon.derive(size, size);
	}
	
}
