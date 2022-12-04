package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.productos.ProductoTab;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ProductosBeans {
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProductoTab ProductoTabPrototype() {
        return new ProductoTab();
    }

}
