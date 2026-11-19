package mx.uabc.algoritmos;

import mx.uabc.algoritmos.datos.LectorCsv;
import mx.uabc.algoritmos.medicion.ResultadoMedicion;
import mx.uabc.algoritmos.medicion.ServicioBenchmark;
import mx.uabc.algoritmos.modelo.Cancion;
import mx.uabc.algoritmos.ordenamiento.ComparadorCancion;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoArrays;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoMergeSort;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoQuickSort;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoSeleccion;
import mx.uabc.algoritmos.ordenamiento.OrdenamientoShell;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Programa de consola usado para revisar por separado la lectura del CSV
 * y los algoritmos antes de trabajar con la ventana gráfica.
 */
public class Main {

    private static final String COLUMNA_DE_EJEMPLO = "energy";

    public static void main(String[] args) throws IOException {
        // La ruta funciona si se ejecuta el programa desde la carpeta del proyecto.
        Path rutaCsv = Path.of(
                "data",
                "billboard_hot_100_spotify_audio_features_enriched.csv"
        );

        // El lector convierte las filas del archivo en objetos Cancion.
        List<Cancion> canciones = new LectorCsv().leer(rutaCsv);
        System.out.println("Canciones cargadas: " + canciones.size());
        if (!canciones.isEmpty()) {
            Cancion primera = canciones.get(0);
            System.out.println("Primera canción:");
            System.out.println("  Título: " + primera.getSong());
            System.out.println("  Artista: " + primera.getPerformer());
            System.out.println("  Duración (ms): " + primera.getSpotifyTrackDurationMs());
            System.out.println("  Energía: " + primera.getEnergy());
        }

        // Estos ejemplos pequeños permiten observar cada algoritmo sin usar todavía el CSV completo.
        mostrarEjemploSeleccion();
        mostrarEjemploShell();
        mostrarEjemploQuickSort();
        mostrarEjemploMergeSort();
        mostrarEjemploArraysSort();
        mostrarEjemploArraysParallelSort();
        mostrarCancionesOrdenadasPorColumna(canciones, COLUMNA_DE_EJEMPLO);
        mostrarComparacionTiempos(canciones, COLUMNA_DE_EJEMPLO);
    }

    private static void mostrarEjemploSeleccion() {
        Integer[] numeros = {64, 25, 12, 22, 11};
        OrdenamientoSeleccion.ordenar(numeros, Comparator.naturalOrder());
        System.out.println("Ejemplo selección directa: " + Arrays.toString(numeros));
    }

    private static void mostrarEjemploShell() {
        Integer[] numeros = {64, 25, 12, 22, 11};
        OrdenamientoShell.ordenar(numeros, Comparator.naturalOrder());
        System.out.println("Ejemplo Shell sort: " + Arrays.toString(numeros));
    }

    private static void mostrarEjemploQuickSort() {
        Integer[] numeros = {64, 25, 12, 22, 11};
        OrdenamientoQuickSort.ordenar(numeros, Comparator.naturalOrder());
        System.out.println("Ejemplo quicksort: " + Arrays.toString(numeros));
    }

    private static void mostrarEjemploMergeSort() {
        Integer[] numeros = {64, 25, 12, 22, 11};
        OrdenamientoMergeSort.ordenar(numeros, Comparator.naturalOrder());
        System.out.println("Ejemplo mergesort: " + Arrays.toString(numeros));
    }

    private static void mostrarEjemploArraysSort() {
        Integer[] numeros = {64, 25, 12, 22, 11};
        OrdenamientoArrays.ordenar(numeros, Comparator.naturalOrder());
        System.out.println("Ejemplo Arrays.sort: " + Arrays.toString(numeros));
    }

    private static void mostrarEjemploArraysParallelSort() {
        Integer[] numeros = {64, 25, 12, 22, 11};
        OrdenamientoArrays.ordenarEnParalelo(numeros, Comparator.naturalOrder());
        System.out.println("Ejemplo Arrays.parallelSort: " + Arrays.toString(numeros));
    }

    private static void mostrarCancionesOrdenadasPorColumna(List<Cancion> canciones,
                                                             String columna) {
        if (canciones.isEmpty()) {
            return;
        }

        Comparator<Cancion> comparador = ComparadorCancion.paraColumna(columna);
        imprimirOrdenados("Selección directa", canciones, columna,
                muestra -> OrdenamientoSeleccion.ordenar(muestra, comparador));
        imprimirOrdenados("Shell sort", canciones, columna,
                muestra -> OrdenamientoShell.ordenar(muestra, comparador));
        imprimirOrdenados("Quicksort", canciones, columna,
                muestra -> OrdenamientoQuickSort.ordenar(muestra, comparador));
        imprimirOrdenados("Mergesort", canciones, columna,
                muestra -> OrdenamientoMergeSort.ordenar(muestra, comparador));
        imprimirOrdenados("Arrays.sort", canciones, columna,
                muestra -> OrdenamientoArrays.ordenar(muestra, comparador));
        imprimirOrdenados("Arrays.parallelSort", canciones, columna,
                muestra -> OrdenamientoArrays.ordenarEnParalelo(muestra, comparador));
    }

    private static void imprimirOrdenados(String nombre, List<Cancion> canciones,
                                          String columna, Consumer<Cancion[]> ordenar) {
        // Se imprime solo una muestra breve para no llenar la consola con miles de filas.
        Cancion[] muestra = crearMuestra(canciones, 5);
        ordenar.accept(muestra);
        System.out.println(nombre + " por " + columna + ":");
        for (Cancion cancion : muestra) {
            System.out.println("  " + cancion.getSong() + " — " + cancion.getPerformer());
        }
    }

    private static void mostrarComparacionTiempos(List<Cancion> canciones, String columna) {
        // La selección directa crece mucho en trabajo; se limita la muestra para esta demostración.
        Cancion[] datos = crearMuestra(canciones, 1000);
        List<ResultadoMedicion> resultados = new ServicioBenchmark().medirTodos(datos, columna);

        System.out.println("Tiempos para ordenar por " + columna + " ("
                + datos.length + " canciones):");
        for (ResultadoMedicion resultado : resultados) {
            System.out.println("  " + resultado.getAlgoritmo() + ": "
                    + resultado.getNanosegundos() + " ns ("
                    + resultado.getMilisegundos() + " ms)");
        }
    }

    private static Cancion[] crearMuestra(List<Cancion> canciones, int limite) {
        // Si el archivo tiene menos elementos que el límite, se usa solamente lo disponible.
        int cantidad = Math.min(limite, canciones.size());
        return canciones.subList(0, cantidad).toArray(new Cancion[0]);
    }
}
