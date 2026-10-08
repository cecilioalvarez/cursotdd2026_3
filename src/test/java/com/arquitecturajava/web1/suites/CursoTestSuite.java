package com.arquitecturajava.web1.suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Agrupa las pruebas de cada capa del concepto curso: negocio, repositorio,
 * servicio y controlador.
 *
 * Las clases van por nombre porque son package-private en otros paquetes; si
 * se renombra o mueve alguna, hay que actualizarla aquí.
 *
 * El nombre termina en "TestSuite" a propósito: Maven Surefire no la ejecuta
 * en un {@code ./mvnw test} normal (esas clases ya se ejecutan por su cuenta y
 * correrían dos veces). Para lanzarla sola:
 * {@code ./mvnw test -Dtest=CursoTestSuite}, o desde el IDE.
 */
@Suite
@SuiteDisplayName("Curso: negocio, repositorio, servicio y controlador")
@SelectClasses(names = {
		"com.arquitecturajava.web1.negocio.CursoTest",
		"com.arquitecturajava.web1.repositorios.CursoRepositoryTest",
		"com.arquitecturajava.web1.servicios.CursoServiceTest",
		"com.arquitecturajava.web1.controladores.CursoControllerTest"
})
class CursoTestSuite {
}
