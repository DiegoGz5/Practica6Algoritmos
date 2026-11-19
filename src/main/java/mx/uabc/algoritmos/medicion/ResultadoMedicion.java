package mx.uabc.algoritmos.medicion;

/** Guarda los datos que la interfaz necesita para presentar una medición. */
public class ResultadoMedicion {

    // Se conserva el campo usado, el algoritmo, el tamaño de muestra y el tiempo obtenido.
    // El tiempo se guarda en nanosegundos y se convierte a milisegundos para mostrarlo.
    private final String columna;
    private final String algoritmo;
    private final int cantidadElementos;
    private final long nanosegundos;

    public ResultadoMedicion(String columna, String algoritmo,
                             int cantidadElementos, long nanosegundos) {
        this.columna = columna;
        this.algoritmo = algoritmo;
        this.cantidadElementos = cantidadElementos;
        this.nanosegundos = nanosegundos;
    }

    public String getColumna() {
        return columna;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public int getCantidadElementos() {
        return cantidadElementos;
    }

    public long getNanosegundos() {
        return nanosegundos;
    }

    public double getMilisegundos() {
        // Un milisegundo equivale a un millón de nanosegundos.
        return nanosegundos / 1_000_000.0;
    }
}
