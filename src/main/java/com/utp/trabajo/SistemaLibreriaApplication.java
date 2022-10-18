package com.utp.trabajo;

//import org.springframework.boot.SpringApplication;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.utp.trabajo.gui.view.MainView;
import java.awt.EventQueue;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:config/mysql/db.properties")
public class SistemaLibreriaApplication {

	public static void main(String[] args) {
		//SpringApplication.run(SistemaLibreriaApplication.class, args);
		FlatLightLaf.setup();
		ConfigurableApplicationContext context = new SpringApplicationBuilder(SistemaLibreriaApplication.class)
				.headless(false)
				.web(WebApplicationType.NONE)
				.run(args);
		
		EventQueue.invokeLater(() -> {
			MainView mainView = context.getBean(MainView.class);
				mainView.setVisible(true);
		});
		
	}
}
