package com.arquitecturajava.web1.selenium.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.arquitecturajava.web1.selenium.helpers.FilaImparticion;

/**
 * Page Object de {@code /cursos/{id}/imparticiones}: las imparticiones de un curso.
 */
public class ListadoImparticionesPage extends Pagina {

	public static final String TITULO = "Imparticiones";

	@FindBy(css = "h1 + p")
	private WebElement subtituloConElCurso;

	@FindBy(linkText = "Nueva impartición")
	private WebElement botonNuevaImparticion;

	@FindBy(linkText = "Volver a cursos")
	private WebElement botonVolverACursos;

	@FindBy(css = ".alert-info")
	private List<WebElement> avisosSinImparticiones;

	@FindBy(css = "table tbody tr")
	private List<WebElement> filas;

	public ListadoImparticionesPage(WebDriver driver) {
		super(driver);
	}

	public String tituloDelCurso() {
		return subtituloConElCurso.getText();
	}

	public boolean muestraAvisoSinImparticiones() {
		return !avisosSinImparticiones.isEmpty();
	}

	public List<FilaImparticion> imparticiones() {
		return filas.stream().map(ListadoImparticionesPage::leerFila).toList();
	}

	public FormularioImparticionPage pulsarNuevaImparticion() {
		botonNuevaImparticion.click();
		pausa();
		return new FormularioImparticionPage(driver);
	}

	public ListadoCursosPage volverACursos() {
		botonVolverACursos.click();
		pausa();
		return new ListadoCursosPage(driver);
	}

	// Columnas: Id, Nombre, Fecha de inicio, Fecha de fin
	private static FilaImparticion leerFila(WebElement fila) {
		List<WebElement> celdas = fila.findElements(By.tagName("td"));
		return new FilaImparticion(
				celdas.get(1).getText(),
				celdas.get(2).getText(),
				celdas.get(3).getText());
	}
}
