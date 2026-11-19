package mx.uabc.algoritmos.ordenamiento;

import java.util.Comparator;

/**
 * Ordenamiento Shell. Primero organiza elementos alejados entre sí y reduce
 * gradualmente esa distancia hasta terminar con una pasada de inserción normal.
 * El comparador define qué significa que un elemento vaya antes que otro.
 */
public class OrdenamientoShell {

    /** Ordena el arreglo de menor a mayor según el criterio del comparador. */
    public static <T> void ordenar(T[] elementos, Comparator<? super T> comparador) {
        // El intervalo determina qué tan lejos puede moverse un elemento en cada pasada.
        for (int intervalo = elementos.length / 2; intervalo > 0; intervalo /= 2) {
            for (int actual = intervalo; actual < elementos.length; actual++) {
                // Se guarda el elemento porque las posiciones anteriores pueden desplazarse.
                T elemento = elementos[actual];
                int posicion = actual;

                // Desplaza valores mayores hasta encontrar el lugar correcto para elemento.
                while (posicion >= intervalo
                        && comparador.compare(elementos[posicion - intervalo], elemento) > 0) {
                    elementos[posicion] = elementos[posicion - intervalo];
                    posicion -= intervalo;
                }

                elementos[posicion] = elemento;
            }
        }
    }
}
