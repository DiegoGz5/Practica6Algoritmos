package mx.uabc.algoritmos.medicion;

import java.util.Arrays;
import java.util.function.Consumer;

/** Mide el tiempo que tarda una operación de ordenamiento sobre una copia. */
public class MedidorTiempo {

    /**
     * Copia los datos antes de iniciar el cronómetro y mide solo el ordenamiento.
     *
     * @param datosOriginales arreglo que se conservará sin cambios
     * @param ordenamiento operación que ordena el arreglo recibido
     * @return duración del ordenamiento en nanosegundos
     */
    public static <T> long medir(T[] datosOriginales, Consumer<T[]> ordenamiento) {
        // La copia se hace antes de iniciar el cronómetro para medir solo el algoritmo.
        T[] copia = Arrays.copyOf(datosOriginales, datosOriginales.length);

        // nanoTime sirve para medir una duración corta; se resta el inicio al tiempo final.
        long inicio = System.nanoTime();
        ordenamiento.accept(copia);
        return System.nanoTime() - inicio;
    }
}
