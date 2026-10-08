package com.arquitecturajava.web1.selenium.helpers;

/**
 * Lo que el usuario ve en una fila del listado de cursos, tal cual aparece en
 * pantalla (los precios ya formateados, por ejemplo "121,00 €").
 */
public record FilaCurso(String titulo, String autor, String precio, String precioConIva) {
}
