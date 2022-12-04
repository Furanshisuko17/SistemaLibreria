
package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.compras.ListaComprasTab;
import com.utp.trabajo.gui.view.compras.ProveedoresTab;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ComprasBeans {
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ListaComprasTab listaComprasTabPrototype() {
        return new ListaComprasTab();
    }


    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProveedoresTab proveedoressTabPrototype() {
        return new ProveedoresTab();
    }

}
