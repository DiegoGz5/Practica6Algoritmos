package mx.uabc.algoritmos.ordenamiento;

import java.util.Comparator;

/**
 * Ordenamiento por selección directa: en cada posición coloca el menor
 * elemento que todavía pertenece a la parte no ordenada. El comparador permite
 * aplicar el mismo algoritmo a números, canciones u otros tipos de objetos.
 */
public class OrdenamientoSeleccion {

    /**
     * Ordena el arreglo en forma ascendente según el comparador recibido.
     */
    public static <T> void ordenar(T[] elementos, Comparator<? super T> comparador) {
        for (int i = 0; i < elementos.length - 1; i++) {
            // menor conserva el valor encontrado y k su posición, como en el algoritmo de clase.
            T menor = elementos[i];
            int k = i;

            // Recorre el resto y recuerda el valor menor y la posición donde está.
            for (int j = i + 1; j < elementos.length; j++) {
                // Un resultado negativo indica que elementos[j] debe ir antes que menor.
                if (comparador.compare(elementos[j], menor) < 0) {
                    menor = elementos[j];
                    k = j;
                }
            }

            // Mueve el elemento inicial a la posición anterior del menor encontrado.
            elementos[k] = elementos[i];
            elementos[i] = menor;
        }
    }
}
