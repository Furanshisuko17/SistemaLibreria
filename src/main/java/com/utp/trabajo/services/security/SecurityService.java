package com.utp.trabajo.services.security;

import com.utp.trabajo.model.entities.Empleado;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthService authService;
	
	public List<String> getPermissions() {
		return authService.getPermisos();
	}
    
    public Empleado getLoggedEmpleado() {
        return authService.getLoggedEmpleado();
    }
    
}
