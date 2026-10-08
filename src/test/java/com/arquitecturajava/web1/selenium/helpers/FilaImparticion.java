package com.arquitecturajava.web1.selenium.helpers;

/**
 * Lo que el usuario ve en una fila del listado de imparticiones, tal cual
 * aparece en pantalla (las fechas como "02/11/2026").
 */
public record FilaImparticion(String nombre, String fechaInicio, String fechaFin) {
}
