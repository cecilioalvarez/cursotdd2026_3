package com.arquitecturajava.web1.selenium;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.arquitecturajava.web1.selenium.paginas.Pagina;

/**
 * Clase padre de las pruebas de extremo a extremo: arranca la aplicación en un
 * puerto aleatorio y abre un navegador real (Edge) antes de cada test, y lo
 * cierra al terminar. Selenium Manager descarga el driver la primera vez.
 *
 * Las pruebas heredan de ella y trabajan con los Page Objects del paquete
 * {@code paginas} a partir de {@link #driver} y {@link #urlBase()}.
 *
 * Por defecto el navegador se ve y cada paso va despacio para poder seguirlo.
 * Para ejecutarlo sin ventana y a velocidad normal (por ejemplo en CI):
 * {@code ./mvnw test -Dselenium.headless=true}
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class SeleniumTestBase {

	@LocalServerPort
	private int puerto;

	protected WebDriver driver;

	@BeforeEach
	void abrirNavegador() {
		EdgeOptions opciones = new EdgeOptions();
		opciones.addArguments("--window-size=1280,800");
		if (Pagina.HEADLESS) {
			opciones.addArguments("--headless=new");
		}
		driver = new EdgeDriver(opciones);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	}

	@AfterEach
	void cerrarNavegador() {
		if (driver != null) {
			Pagina.pausa();
			driver.quit();
		}
	}

	protected String urlBase() {
		return "http://localhost:" + puerto;
	}
}
