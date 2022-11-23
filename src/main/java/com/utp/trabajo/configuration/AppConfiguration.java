package com.utp.trabajo.configuration;

import com.utp.trabajo.gui.view.ventas.ClientesTab;
import com.utp.trabajo.gui.view.compras.ListaComprasTab;
import com.utp.trabajo.gui.view.compras.MateriaPrimaTab;
import com.utp.trabajo.gui.view.compras.ProovedoresTab;
import com.utp.trabajo.gui.view.ventas.NuevaVentaTab;
import com.utp.trabajo.gui.view.ventas.VentasTab;
import com.utp.trabajo.gui.view.almacen.MarcaTab;
import com.utp.trabajo.gui.view.almacen.TipoTab;
import com.utp.trabajo.gui.view.almacen.ProductoTab;
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

    //ALMACEN
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
    public ProductoTab ProductoTabPrototype() {
        return new ProductoTab();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public AlmacenTab AlmacenTabPrototype() {
        return new AlmacenTab();
    }
    //ALMACEN 

    //VENTAS
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
    //VENTAS

    //COMPRAS
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
    //COMPRAS

}
