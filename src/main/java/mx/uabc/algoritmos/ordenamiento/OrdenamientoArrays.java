package mx.uabc.algoritmos.ordenamiento;

import java.util.Arrays;
import java.util.Comparator;

/** Reúne las dos alternativas de ordenamiento que ofrece java.util.Arrays. */
public class OrdenamientoArrays {

    /** Ordena el arreglo con la implementación estándar de Java. */
    public static <T> void ordenar(T[] elementos, Comparator<? super T> comparador) {
        Arrays.sort(elementos, comparador);
    }

    /**
     * Ordena usando trabajo paralelo cuando la implementación de Java lo considera
     * conveniente para el tamaño del arreglo.
     */
    public static <T> void ordenarEnParalelo(T[] elementos,
                                             Comparator<? super T> comparador) {
        Arrays.parallelSort(elementos, comparador);
    }
}
