package com.arquitecturajava.web1.selenium.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.arquitecturajava.web1.selenium.helpers.FilaCurso;

/**
 * Page Object de {@code /cursos}: la tabla con el listado de cursos.
 */
public class ListadoCursosPage extends Pagina {

	public static final String TITULO = "Listado de cursos";

	@FindBy(linkText = "Nuevo curso")
	private WebElement botonNuevoCurso;

	@FindBy(css = "table tbody tr")
	private List<WebElement> filas;

	public ListadoCursosPage(WebDriver driver) {
		super(driver);
	}

	public static ListadoCursosPage abrir(WebDriver driver, String urlBase) {
		driver.get(urlBase + "/cursos");
		pausa();
		return new ListadoCursosPage(driver);
	}

	public int numeroDeCursos() {
		return filas.size();
	}

	public List<FilaCurso> cursos() {
		return filas.stream().map(ListadoCursosPage::leerFila).toList();
	}

	// El id cambia en cada prueba, así que estos elementos no pueden ir en un @FindBy
	public FilaCurso cursoConId(Long id) {
		return leerFila(driver.findElement(By.xpath("//table/tbody/tr[td[1][normalize-space()='" + id + "']]")));
	}

	public FormularioCursoPage pulsarNuevoCurso() {
		botonNuevoCurso.click();
		pausa();
		return new FormularioCursoPage(driver);
	}

	public FormularioCursoPage pulsarEditar(Long id) {
		driver.findElement(By.cssSelector("a[href='/cursos/" + id + "/editar']")).click();
		pausa();
		return new FormularioCursoPage(driver);
	}

	// Por título, como lo buscaría el usuario: de un curso recién creado no conoce el id
	public ListadoImparticionesPage pulsarVerImparticiones(String tituloDelCurso) {
		driver.findElement(By.xpath("//table/tbody/tr[td[2][normalize-space()='" + tituloDelCurso + "']]"
				+ "//a[normalize-space()='Ver imparticiones']")).click();
		pausa();
		return new ListadoImparticionesPage(driver);
	}

	public ConfirmacionBorrado pulsarBorrar(Long id) {
		driver.findElement(By.cssSelector("form[action='/cursos/" + id + "/borrar'] button")).click();
		return new ConfirmacionBorrado(driver);
	}

	// Columnas: Id, Título, Autor, Precio, Precio con IVA, Acciones
	private static FilaCurso leerFila(WebElement fila) {
		List<WebElement> celdas = fila.findElements(By.tagName("td"));
		return new FilaCurso(
				celdas.get(1).getText(),
				celdas.get(2).getText(),
				celdas.get(3).getText(),
				celdas.get(4).getText());
	}
}
