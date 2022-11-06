package com.utp.trabajo.services.security;

import com.utp.trabajo.exception.UsernameNotFoundException;
import com.utp.trabajo.exception.WrongPasswordException;
import com.utp.trabajo.model.dao.EmpleadoDao;
import com.utp.trabajo.model.entities.Empleado;
import com.utp.trabajo.model.entities.Permiso;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmpleadoDao empleadoDao;

	private User loggedUser;

	private List<String> permisos;

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
				setPermisos(empleado);
				return true;
			} else {
				throw new WrongPasswordException("Contraseña incorrecta.");
			}
		} else {
			throw new UsernameNotFoundException("Usuario no encontrado.");
		}
	}
	
	@Transactional() //FIX THIS!
	private void setPermisos(Empleado empleado) {
		List<Permiso> p = empleado.getRolAcceso().getPermisos();
		for (Permiso permiso : p) {
			permisos.add(permiso.getNombre());
		}
		
	}

	public void logout() {
		//TODO: logout
	}
	
	public List<String> getPermisos() {
		return permisos;
	}

}
