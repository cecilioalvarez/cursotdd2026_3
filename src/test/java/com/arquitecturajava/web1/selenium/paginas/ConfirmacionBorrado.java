package com.arquitecturajava.web1.selenium.paginas;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * El cuadro {@code confirm()} del navegador que sale al pulsar "Borrar". No es
 * HTML, así que no tiene @FindBy: Selenium lo maneja con {@link Alert}.
 */
public class ConfirmacionBorrado {

	private final WebDriver driver;
	private final Alert alerta;

	ConfirmacionBorrado(WebDriver driver) {
		this.driver = driver;
		this.alerta = new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.alertIsPresent());
		Pagina.pausa();
	}

	public String mensaje() {
		return alerta.getText();
	}

	public ListadoCursosPage aceptar() {
		alerta.accept();
		Pagina.pausa();
		return new ListadoCursosPage(driver);
	}
}
