package com.alura.literalura;

import com.alura.literalura.principal.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private final Menu menu;

	// Inyección por constructor, para que el IDE no marque "never assigned"
	public LiteraluraApplication(Menu menu) {
		this.menu = menu;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	/**
	 * Spring Boot llamará a este método una vez que la aplicación arranque
	 * y el contexto esté listo. Aquí se llama a menu.mostrarMenu().
	 */
	@Override
	public void run(String... args) {
		menu.mostrarMenu();
	}
}
