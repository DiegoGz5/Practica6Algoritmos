package mx.uabc.algoritmos.ordenamiento;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Mergesort: divide el arreglo en segmentos más pequeños, los ordena y
 * combina sus elementos en orden ascendente. El comparador establece el orden
 * que se debe respetar durante cada combinación.
 */
public class OrdenamientoMergeSort {

    /** Prepara el arreglo auxiliar y comienza el proceso recursivo. */
    public static <T> void ordenar(T[] elementos, Comparator<? super T> comparador) {
        // Se reutiliza este arreglo temporal durante las combinaciones de los segmentos.
        T[] auxiliar = Arrays.copyOf(elementos, elementos.length);
        ordenar(elementos, auxiliar, 0, elementos.length - 1, comparador);
    }

    private static <T> void ordenar(T[] elementos, T[] auxiliar, int inicio, int fin,
                                    Comparator<? super T> comparador) {
        // Un segmento con un solo elemento ya está ordenado.
        if (inicio >= fin) {
            return;
        }

        // Ordena cada mitad antes de mezclarlas.
        int medio = inicio + (fin - inicio) / 2;
        ordenar(elementos, auxiliar, inicio, medio, comparador);
        ordenar(elementos, auxiliar, medio + 1, fin, comparador);
        combinar(elementos, auxiliar, inicio, medio, fin, comparador);
    }

    private static <T> void combinar(T[] elementos, T[] auxiliar, int inicio, int medio,
                                     int fin, Comparator<? super T> comparador) {
        // Se conserva una copia del segmento para poder leer de ambas mitades al escribir.
        for (int indice = inicio; indice <= fin; indice++) {
            auxiliar[indice] = elementos[indice];
        }

        // izquierda y derecha señalan el siguiente valor disponible en cada mitad.
        int izquierda = inicio;
        int derecha = medio + 1;

        // En cada paso se coloca en destino el menor de los dos valores disponibles.
        for (int destino = inicio; destino <= fin; destino++) {
            if (izquierda > medio) {
                elementos[destino] = auxiliar[derecha++];
            } else if (derecha > fin) {
                elementos[destino] = auxiliar[izquierda++];
            } else if (comparador.compare(auxiliar[izquierda], auxiliar[derecha]) <= 0) {
                // En igualdad se toma primero el elemento izquierdo; así se conserva el orden.
                elementos[destino] = auxiliar[izquierda++];
            } else {
                elementos[destino] = auxiliar[derecha++];
            }
        }
    }
}
