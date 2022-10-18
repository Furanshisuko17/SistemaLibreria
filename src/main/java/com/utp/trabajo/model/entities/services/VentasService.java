package com.utp.trabajo.model.entities.services;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.model.dao.VentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentasService {
	
    @Autowired
    private VentaDao ventaDao;

}
