package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.clientes.ClientesTab;
import com.utp.trabajo.gui.view.compras.ListaComprasTab;
import com.utp.trabajo.gui.view.compras.ProovedoresTab;
import com.utp.trabajo.gui.view.ventas.NuevaVentaTab;
import com.utp.trabajo.gui.view.ventas.VentasTab;
import com.utp.trabajo.gui.view.almacen.MarcaTab;
import com.utp.trabajo.gui.view.almacen.TipoTab;
import com.utp.trabajo.gui.view.productos.ProductoTab;
import com.utp.trabajo.gui.view.almacen.AlmacenTab;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptpasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestartEndpoint restartEndPoint() {
        return new RestartEndpoint();
    }

    
}
