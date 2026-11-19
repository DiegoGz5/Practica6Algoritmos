package mx.uabc.algoritmos.ordenamiento;

import java.util.Comparator;

/**
 * Quicksort que divide cada segmento en elementos menores, iguales y mayores
 * que un pivote, según el comparador recibido. La llamada recursiva repite
 * el proceso en los grupos que aún tienen más de un elemento.
 */
public class OrdenamientoQuickSort {

    /** Inicia la ordenación usando todo el arreglo como primer segmento. */
    public static <T> void ordenar(T[] elementos, Comparator<? super T> comparador) {
        ordenar(elementos, 0, elementos.length - 1, comparador);
    }

    private static <T> void ordenar(T[] elementos, int inicio, int fin,
                                    Comparator<? super T> comparador) {
        // Un segmento de cero o un elemento ya está ordenado.
        if (inicio >= fin) {
            return;
        }

        // Se elige un elemento del centro para dividir el segmento actual.
        T pivote = elementos[inicio + (fin - inicio) / 2];
        // Estos índices marcan los límites de los grupos que ya se clasificaron.
        int finMenores = inicio;
        int actual = inicio;
        int inicioMayores = fin;

        // Al terminar el ciclo: menores | iguales | mayores que el pivote.
        while (actual <= inicioMayores) {
            int comparacion = comparador.compare(elementos[actual], pivote);

            if (comparacion < 0) {
                intercambiar(elementos, finMenores, actual);
                finMenores++;
                actual++;
            } else if (comparacion > 0) {
                // El valor que llega desde el final aún no se ha comparado; por eso actual no avanza.
                intercambiar(elementos, actual, inicioMayores);
                inicioMayores--;
            } else {
                actual++;
            }
        }

        // El grupo igual ya está en su lugar; solo hace falta ordenar los extremos.
        ordenar(elementos, inicio, finMenores - 1, comparador);
        ordenar(elementos, inicioMayores + 1, fin, comparador);
    }

    private static <T> void intercambiar(T[] elementos, int primero, int segundo) {
        // La variable temporal evita perder el valor de una de las dos posiciones.
        T temporal = elementos[primero];
        elementos[primero] = elementos[segundo];
        elementos[segundo] = temporal;
    }
}
