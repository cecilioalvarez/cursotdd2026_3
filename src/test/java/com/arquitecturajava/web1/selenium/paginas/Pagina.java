package com.arquitecturajava.web1.selenium.paginas;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Base de los Page Objects: PageFactory rellena los campos anotados con
 * {@code @FindBy}. Cada campo se busca en el navegador en el momento de usarlo,
 * no al crear la página.
 *
 * Con el navegador visible, las acciones van despacio para poder seguirlas.
 * Con {@code -Dselenium.headless=true} no hay pausas.
 */
public abstract class Pagina {

	public static final boolean HEADLESS = Boolean.getBoolean("selenium.headless");
	private static final Duration PAUSA_ENTRE_PASOS = HEADLESS ? Duration.ZERO : Duration.ofMillis(1500);
	private static final Duration PAUSA_ENTRE_TECLAS = HEADLESS ? Duration.ZERO : Duration.ofMillis(80);

	protected final WebDriver driver;

	protected Pagina(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public String titulo() {
		return driver.getTitle();
	}

	// Teclea carácter a carácter para que se vea cómo se rellena el campo
	protected void escribir(WebElement campo, String texto) {
		campo.clear();
		for (char letra : texto.toCharArray()) {
			campo.sendKeys(String.valueOf(letra));
			esperar(PAUSA_ENTRE_TECLAS);
		}
		pausa();
	}

	public static void pausa() {
		esperar(PAUSA_ENTRE_PASOS);
	}

	private static void esperar(Duration duracion) {
		if (duracion.isZero()) {
			return;
		}
		try {
			Thread.sleep(duracion);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
