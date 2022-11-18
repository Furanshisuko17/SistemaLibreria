package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.ventas.ClientesTab;
import com.utp.trabajo.gui.view.compras.ListaComprasTab;
import com.utp.trabajo.gui.view.compras.MateriaPrimaTab;
import com.utp.trabajo.gui.view.compras.ProovedoresTab;
import com.utp.trabajo.gui.view.ventas.NuevaVentaTab;
import com.utp.trabajo.gui.view.ventas.VentasTab;



import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfiguration {
	
	@Bean
	public BCryptPasswordEncoder BCryptpasswordEncoder() {
		return new BCryptPasswordEncoder();
        
	}
    
    @Bean
    public RestartEndpoint restartEndPoint() {
        return new RestartEndpoint();
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ClientesTab clientesTabPrototype() {
        return new ClientesTab();
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public NuevaVentaTab nuevaVentaTabPrototype() {
        return new NuevaVentaTab();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public VentasTab ventasTabPrototype() {
        return new VentasTab();
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ListaComprasTab listaComprasTabPrototype() {
        return new ListaComprasTab();
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public MateriaPrimaTab materiaPrimaTabPrototype() {
        return new MateriaPrimaTab();
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProovedoresTab proveedoressTabPrototype() {
        return new ProovedoresTab();
    }
}
