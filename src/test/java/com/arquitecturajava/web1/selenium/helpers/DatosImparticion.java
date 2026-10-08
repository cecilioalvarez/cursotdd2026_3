package com.arquitecturajava.web1.selenium.helpers;

import java.time.LocalDate;

/**
 * Lo que el usuario rellena en el formulario de impartición.
 */
public record DatosImparticion(String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
}
