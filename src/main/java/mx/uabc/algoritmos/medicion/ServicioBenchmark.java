package mx.uabc.algoritmos.medicion;

import mx.uabc.algoritmos.modelo.Cancion;
import mx.uabc.algoritmos.ordenamiento.ComparadorCancion;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoArrays;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoMergeSort;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoQuickSort;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoSeleccion;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoShell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/** Ejecuta y mide los seis métodos de la práctica usando una misma columna. */
public class ServicioBenchmark {

    public List<ResultadoMedicion> medirTodos(Cancion[] datos, String columna) {
        // Todos los algoritmos reciben el mismo criterio para decidir el orden de las canciones.
        Comparator<Cancion> comparador = ComparadorCancion.paraColumna(columna);
        List<ResultadoMedicion> resultados = new ArrayList<>(6);

        // Cada medición se realiza sobre una copia para que un algoritmo no altere la entrada del siguiente.
        agregarMedicion(resultados, datos, columna, "Selección directa",
                copia -> OrdenamientoSeleccion.ordenar(copia, comparador));
        agregarMedicion(resultados, datos, columna, "Shell sort",
                copia -> OrdenamientoShell.ordenar(copia, comparador));
        agregarMedicion(resultados, datos, columna, "Quicksort",
                copia -> OrdenamientoQuickSort.ordenar(copia, comparador));
        agregarMedicion(resultados, datos, columna, "Mergesort",
                copia -> OrdenamientoMergeSort.ordenar(copia, comparador));
        agregarMedicion(resultados, datos, columna, "Arrays.sort",
                copia -> OrdenamientoArrays.ordenar(copia, comparador));
        agregarMedicion(resultados, datos, columna, "Arrays.parallelSort",
                copia -> OrdenamientoArrays.ordenarEnParalelo(copia, comparador));

        return resultados;
    }

    private void agregarMedicion(List<ResultadoMedicion> resultados, Cancion[] datos,
                                 String columna, String algoritmo,
                                 Consumer<Cancion[]> ordenamiento) {
        long nanosegundos = MedidorTiempo.medir(datos, ordenamiento);
        resultados.add(new ResultadoMedicion(
                columna, algoritmo, datos.length, nanosegundos
        ));
    }
}
