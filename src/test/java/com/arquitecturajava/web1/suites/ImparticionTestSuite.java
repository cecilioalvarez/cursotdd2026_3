package com.arquitecturajava.web1.suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Agrupa las pruebas de cada capa del concepto impartición: negocio,
 * repositorio, servicio y controlador.
 *
 * Igual que {@link CursoTestSuite}: las clases van por nombre porque son
 * package-private en otros paquetes, y el nombre termina en "TestSuite" para
 * que un {@code ./mvnw test} normal no ejecute sus pruebas dos veces. Para
 * lanzarla sola: {@code ./mvnw test -Dtest=ImparticionTestSuite}, o desde el IDE.
 */
@Suite
@SuiteDisplayName("Impartición: negocio, repositorio, servicio y controlador")
@SelectClasses(names = {
		"com.arquitecturajava.web1.negocio.ImparticionTest",
		"com.arquitecturajava.web1.repositorios.ImparticionRepositoryTest",
		"com.arquitecturajava.web1.servicios.ImparticionServiceTest",
		"com.arquitecturajava.web1.controladores.ImparticionControllerTest"
})
class ImparticionTestSuite {
}
