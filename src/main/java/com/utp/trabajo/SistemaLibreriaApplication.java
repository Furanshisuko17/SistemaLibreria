package com.utp.trabajo;

//import org.springframework.boot.SpringApplication;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.gui.view.MainView;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:config/mysql/db.properties")
//@EnableAsync
public class SistemaLibreriaApplication {
    
    private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		//SpringApplication.run(SistemaLibreriaApplication.class, args);
		FlatDarkLaf.setup();
		FlatSVGIcon.ColorFilter.getInstance()
				.add(Color.black, new Color(90, 90, 90), new Color(175, 177, 179));
		FlatInspector.install( "ctrl shift alt X" ); // To inspect the UI - dev only       
		context = getBuiltAndConfiguredApp().run(args);
        createInterface(context);
	}
    
    public static SpringApplicationBuilder getBuiltAndConfiguredApp() {
        return new SpringApplicationBuilder(SistemaLibreriaApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE);
    }
    
    public static void createInterface(ConfigurableApplicationContext context) {
        EventQueue.invokeLater(() -> {
			MainView mainView = context.getBean(MainView.class);
			//mainView.setVisible(false); 
            for (Window window : mainView.getOwnedWindows()) {
                System.out.println(window.getName());
                if (window.getName().equalsIgnoreCase("logindialog")) {
                    window.setVisible(true); //TODO: needs another workaround - medium priority
                }
            };
		});
    }
    
    
    
    //NO FUNCIONA, no está en uso
    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        for(String arg : args.getSourceArgs()) {
            System.out.println("Args: " + arg);
            
        }
        Thread thread = new Thread(() -> {
            context.close();
            context = getBuiltAndConfiguredApp().run(args.getSourceArgs());
            
        });
       thread.setDaemon(false);
       thread.start();
        
    }
}
