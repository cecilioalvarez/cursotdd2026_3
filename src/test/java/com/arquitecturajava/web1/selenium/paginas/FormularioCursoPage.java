package com.arquitecturajava.web1.selenium.paginas;

import java.math.BigDecimal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.arquitecturajava.web1.selenium.helpers.DatosCurso;

/**
 * Page Object del formulario de curso, el mismo para alta y edición.
 */
public class FormularioCursoPage extends Pagina {

	public static final String TITULO_ALTA = "Nuevo curso";
	public static final String TITULO_EDICION = "Editar curso";

	@FindBy(id = "titulo")
	private WebElement campoTitulo;

	@FindBy(id = "autor")
	private WebElement campoAutor;

	@FindBy(id = "precio")
	private WebElement campoPrecio;

	@FindBy(css = "button[type=submit]")
	private WebElement botonGuardar;

	public FormularioCursoPage(WebDriver driver) {
		super(driver);
	}

	public DatosCurso datosMostrados() {
		return new DatosCurso(
				campoTitulo.getDomProperty("value"),
				campoAutor.getDomProperty("value"),
				Double.parseDouble(campoPrecio.getDomProperty("value")));
	}

	public FormularioCursoPage rellenar(DatosCurso datos) {
		escribir(campoTitulo, datos.titulo());
		escribir(campoAutor, datos.autor());
		// Sin ceros sobrantes: 200.0 se teclea como "200"
		escribir(campoPrecio, BigDecimal.valueOf(datos.precio()).stripTrailingZeros().toPlainString());
		return this;
	}

	public ListadoCursosPage guardar() {
		botonGuardar.click();
		pausa();
		return new ListadoCursosPage(driver);
	}
}
