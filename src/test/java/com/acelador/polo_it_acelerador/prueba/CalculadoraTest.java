package com.acelador.polo_it_acelerador.prueba;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import prueba.Calculadora;

public class CalculadoraTest {

     @Test
    void testSuma() {
        Calculadora calc = new Calculadora();
        assertEquals(5, calc.sumar(2, 3));
    }

    @Test
    void testResta() {
        Calculadora calc = new Calculadora();
        assertEquals(1, calc.restar(3, 2));
    }

}