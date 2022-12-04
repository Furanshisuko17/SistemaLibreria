
package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.almacen.AlmacenTab;
import com.utp.trabajo.gui.view.almacen.MarcaTab;
import com.utp.trabajo.gui.view.almacen.TipoTab;
import com.utp.trabajo.gui.view.productos.ProductoTab;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AlmacenBeans {
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public MarcaTab MarcaTabPrototype() {
        return new MarcaTab();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public TipoTab TipoTabPrototype() {
        return new TipoTab();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public AlmacenTab AlmacenTabPrototype() {
        return new AlmacenTab();
    }

}
