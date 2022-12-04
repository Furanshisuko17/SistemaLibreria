
package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.clientes.ClientesTab;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ClientesBeans {
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ClientesTab clientesTabPrototype() {
        return new ClientesTab();
    }
    
}
