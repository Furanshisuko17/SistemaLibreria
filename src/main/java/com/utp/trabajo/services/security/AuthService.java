package com.utp.trabajo.services.security;

import com.utp.trabajo.exception.auth.UsernameNotFoundException;
import com.utp.trabajo.exception.auth.WrongPasswordException;
import com.utp.trabajo.model.dao.EmpleadoDao;
import com.utp.trabajo.model.dao.RolAccesoDao;
import com.utp.trabajo.model.entities.Empleado;
import com.utp.trabajo.model.entities.Permiso;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmpleadoDao empleadoDao;
    
    @Autowired
    private RolAccesoDao rolAccesoDao;
    
    @Autowired
    private RestartEndpoint restartEndpoint;

	private Empleado loggedEmpleado;

	private List<String> permisos;
    
    public AuthService() {
        permisos = new ArrayList<String>();
    }

	@Transactional
	public boolean login(String username, char[] rawPassword)
		throws UsernameNotFoundException, WrongPasswordException {
		//
		if (empleadoDao.existsEmpleadoByUsername(username)) {
			System.out.println("Existe empleado!");
			
			Empleado empleado = empleadoDao.findByUsername(username);
			String password = empleado.getEncryptedPassword();
			boolean isPassword = passwordEncoder.matches(String.valueOf(rawPassword), password);
			if (isPassword) {
                loggedEmpleado = empleado;
				setPermisos(empleado);
				return true;
			} else {
				throw new WrongPasswordException("Contraseña incorrecta.");
			}
		} else {
			throw new UsernameNotFoundException("Usuario no encontrado.");
		}
	}
	
	@Transactional(readOnly = true)
    private void setPermisos(Empleado empleado) {
        List<Permiso> p = empleado.getRolAcceso().getPermisos();
        
		for (Permiso permiso : p) {
            System.out.println(permiso.getNombre());
			permisos.add(permiso.getNombre());
		}
	}

	public void logout() {
        clearSession();
        Object rest = restartEndpoint.restart(); //partially working 
        System.out.println(rest);
        //SistemaLibreriaApplication.restart(); // not working, no workarounds were found 09/11/2022, 4:23PM

	}
    
    private boolean clearSession() {
        setLoggedEmpleado(null);
        setPermisos(new ArrayList<String>());
        return true;
    }
    
    private void setLoggedEmpleado(Empleado loggedEmpleado) {
        this.loggedEmpleado = loggedEmpleado;
    }

    private void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }
    
	public List<String> getPermisos() {
		return permisos;
	}

    public Empleado getLoggedEmpleado() {
        return loggedEmpleado;
    }
    
}
