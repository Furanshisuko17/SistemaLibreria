
package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.ventas.NuevaVentaWindow;
import com.utp.trabajo.gui.view.ventas.VentasTab;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class VentasBeans {
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public NuevaVentaWindow nuevaVentaTabPrototype() {
        return new NuevaVentaWindow();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public VentasTab ventasTabPrototype() {
        return new VentasTab();
    }

}
