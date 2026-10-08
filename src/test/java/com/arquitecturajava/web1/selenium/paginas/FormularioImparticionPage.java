package com.arquitecturajava.web1.selenium.paginas;

import java.time.LocalDate;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.arquitecturajava.web1.selenium.helpers.DatosImparticion;

/**
 * Page Object del formulario de alta de una impartición.
 */
public class FormularioImparticionPage extends Pagina {

	public static final String TITULO = "Nueva impartición";

	@FindBy(css = "h1 + p")
	private WebElement subtituloConElCurso;

	@FindBy(id = "nombre")
	private WebElement campoNombre;

	@FindBy(id = "fechaInicio")
	private WebElement campoFechaInicio;

	@FindBy(id = "fechaFin")
	private WebElement campoFechaFin;

	@FindBy(css = "button[type=submit]")
	private WebElement botonGuardar;

	public FormularioImparticionPage(WebDriver driver) {
		super(driver);
	}

	public String tituloDelCurso() {
		return subtituloConElCurso.getText();
	}

	public FormularioImparticionPage rellenar(DatosImparticion datos) {
		escribir(campoNombre, datos.nombre());
		elegirFecha(campoFechaInicio, datos.fechaInicio());
		elegirFecha(campoFechaFin, datos.fechaFin());
		return this;
	}

	public ListadoImparticionesPage guardar() {
		botonGuardar.click();
		pausa();
		return new ListadoImparticionesPage(driver);
	}

	// Lo que hay que teclear en un <input type="date"> depende del idioma del
	// navegador (dd/mm/aaaa, mm/dd/aaaa...). Se le da el valor ISO directamente,
	// que es lo que el campo guarda y envía sea cual sea el idioma.
	private void elegirFecha(WebElement campo, LocalDate fecha) {
		((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", campo, fecha.toString());
		pausa();
	}
}
