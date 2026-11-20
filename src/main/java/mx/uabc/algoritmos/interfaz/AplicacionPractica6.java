package mx.uabc.algoritmos.interfaz;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uabc.algoritmos.medicion.ResultadoMedicion;
import mx.uabc.algoritmos.medicion.ServicioBenchmark;
import mx.uabc.algoritmos.datos.LectorCsv;
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

/** Ventana principal para consultar las canciones y comparar sus ordenamientos. */
public class AplicacionPractica6 extends Application {

    // La ruta es relativa a la carpeta del proyecto para no depender de una ruta personal.
    private static final Path RUTA_CSV = Path.of(
            "data",
            "billboard_hot_100_spotify_audio_features_enriched.csv"
    );
    // Cada definición conecta el nombre visible con el encabezado CSV y el getter de Cancion.
    private static final List<DefinicionColumna> COLUMNAS = List.of(
            new DefinicionColumna("ID de canción", "song_id", "songId", 150),
            new DefinicionColumna("Artista", "performer", "performer", 180),
            new DefinicionColumna("Canción", "song", "song", 220),
            new DefinicionColumna("Género de Spotify", "spotify_genre", "spotifyGenre", 160),
            new DefinicionColumna("ID de pista", "spotify_track_id", "spotifyTrackId", 180),
            new DefinicionColumna("URL de vista previa", "spotify_track_preview_url", "spotifyTrackPreviewUrl", 240),
            new DefinicionColumna("Duración (ms)", "spotify_track_duration_ms", "spotifyTrackDurationMs", 130),
            new DefinicionColumna("Explícita", "spotify_track_explicit", "spotifyTrackExplicit", 100),
            new DefinicionColumna("Álbum", "spotify_track_album", "spotifyTrackAlbum", 200),
            new DefinicionColumna("Bailabilidad", "danceability", "danceability", 120),
            new DefinicionColumna("Energía", "energy", "energy", 100),
            new DefinicionColumna("Tonalidad", "key", "key", 100),
            new DefinicionColumna("Volumen (dB)", "loudness", "loudness", 120),
            new DefinicionColumna("Modo", "mode", "mode", 90),
            new DefinicionColumna("Habla", "speechiness", "speechiness", 110),
            new DefinicionColumna("Acústica", "acousticness", "acousticness", 110),
            new DefinicionColumna("Instrumentalidad", "instrumentalness", "instrumentalness", 140),
            new DefinicionColumna("Sonido en vivo", "liveness", "liveness", 130),
            new DefinicionColumna("Valencia", "valence", "valence", 100),
            new DefinicionColumna("Tempo", "tempo", "tempo", 90),
            new DefinicionColumna("Compás", "time_signature", "timeSignature", 100),
            new DefinicionColumna("Popularidad", "spotify_track_popularity", "spotifyTrackPopularity", 120),
            new DefinicionColumna("Mejor posición", "peak_position", "peakPosition", 130),
            new DefinicionColumna("Semanas en lista", "weeks_on_chart", "weeksOnChart", 140),
            new DefinicionColumna("Primera semana", "first_week", "firstWeek", 140),
            new DefinicionColumna("Última semana", "last_week", "lastWeek", 140),
            new DefinicionColumna("Nivel de éxito", "chart_success_tier", "chartSuccessTier", 150),
            new DefinicionColumna("Perfil de duración", "hit_longevity_profile", "hitLongevityProfile", 180),
            new DefinicionColumna("Grupo de estado acústico", "acoustic_mood_cluster", "acousticMoodCluster", 200),
            new DefinicionColumna("Época musical", "musical_era", "musicalEra", 140),
            new DefinicionColumna("Fragmento de evidencia", "evidence_span", "evidenceSpan", 220),
            new DefinicionColumna("Justificación", "cot_rationale", "cotRationale", 280),
            new DefinicionColumna("Puntaje de confianza", "confidence_score", "confidenceScore", 160)
    );

    private final TableView<Cancion> tablaCanciones = new TableView<>();
    private final TableView<ResultadoMedicion> tablaResultados = new TableView<>();
    private final CategoryAxis ejeAlgoritmos = new CategoryAxis();
    private final NumberAxis ejeTiempo = new NumberAxis();
    private final BarChart<String, Number> graficaTiempos =
            new BarChart<>(ejeAlgoritmos, ejeTiempo);
    private final PanelGraficasCsv panelGraficasCsv = new PanelGraficasCsv();
    private final Label estado = new Label("Carga las canciones para comenzar.");
    private final ComboBox<String> selectorColumna = new ComboBox<>();
    private final ComboBox<String> selectorAlgoritmo = new ComboBox<>();
    private final Button botonOrdenar = new Button("Ordenar");
    private final Button botonComparar = new Button("Comparar los seis");
    private final TextField campoCantidad = new TextField("1000");
    private List<Cancion> cancionesOriginales = List.of();

    // También se guarda el ancho inicial para que los campos largos tengan más espacio.
    private record DefinicionColumna(String etiqueta, String encabezadoCsv,
                                     String propiedadJava, double ancho) {
    }

    @Override
    public void start(Stage ventana) {
        // Primero se preparan las tablas y controles; después se colocan dentro de la ventana.
        configurarColumnas();
        configurarControles();
        configurarGrafica();

        Button botonCargar = new Button("Cargar canciones");
        botonCargar.setOnAction(evento -> cargarCanciones());
        botonOrdenar.setOnAction(evento -> ordenarCanciones());
        botonComparar.setOnAction(evento -> compararTiempos());

        HBox barraControles = new HBox(10,
                botonCargar,
                new Label("Ordenar por:"), selectorColumna,
                new Label("Algoritmo:"), selectorAlgoritmo,
                new Label("Canciones para comparar:"), campoCantidad,
                botonOrdenar,
                botonComparar
        );
        campoCantidad.setPrefColumnCount(6);
        // La tabla y la gráfica muestran dos representaciones de las mismas mediciones.
        HBox panelResultados = new HBox(12, tablaResultados, graficaTiempos);
        HBox.setHgrow(tablaResultados, Priority.ALWAYS);
        HBox.setHgrow(graficaTiempos, Priority.ALWAYS);
        Tab pestanaRendimiento = new Tab("Rendimiento", panelResultados);
        ScrollPane desplazamientoGraficas = new ScrollPane(panelGraficasCsv);
        desplazamientoGraficas.setFitToWidth(true);
        desplazamientoGraficas.setFitToHeight(false);
        desplazamientoGraficas.setPannable(true);
        desplazamientoGraficas.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        desplazamientoGraficas.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        Tab pestanaDatos = new Tab("Gráficas del CSV", desplazamientoGraficas);
        pestanaRendimiento.setClosable(false);
        pestanaDatos.setClosable(false);
        TabPane pestanasGraficas = new TabPane(pestanaRendimiento, pestanaDatos);
        VBox contenido = new VBox(12,
                new Label("Billboard Hot 100 y Spotify"),
                barraControles,
                estado,
                tablaCanciones,
                pestanasGraficas
        );
        contenido.setStyle("-fx-padding: 16;");
        VBox.setVgrow(tablaCanciones, Priority.ALWAYS);
        tablaResultados.setPrefHeight(250);
        graficaTiempos.setPrefHeight(250);
        pestanasGraficas.setPrefHeight(540);

        Scene escena = new Scene(contenido, 1200, 950);
        ventana.setTitle("Práctica 6 - Algoritmos de ordenamiento");
        ventana.setMinWidth(1100);
        ventana.setMinHeight(760);
        ventana.setScene(escena);
        ventana.show();
    }

    private void configurarControles() {
        // El usuario elige el campo y el algoritmo que se aplicará a la tabla principal.
        selectorColumna.getItems().addAll(
                COLUMNAS.stream().map(DefinicionColumna::etiqueta).toList()
        );
        selectorColumna.setValue("Energía");
        selectorAlgoritmo.getItems().addAll(
                "Selección directa", "Shell sort", "Quicksort",
                "Mergesort", "Arrays.sort", "Arrays.parallelSort"
        );
        selectorAlgoritmo.setValue("Quicksort");
    }

    private void configurarColumnas() {
        // Se generan las 33 columnas a partir de una sola lista de definiciones.
        for (DefinicionColumna definicion : COLUMNAS) {
            tablaCanciones.getColumns().add(columna(
                    definicion.etiqueta(), definicion.propiedadJava(), definicion.ancho()
            ));
        }

        tablaResultados.getColumns().add(columnaResultado("Algoritmo", "algoritmo", 240));
        tablaResultados.getColumns().add(columnaResultado("Canciones", "cantidadElementos", 120));
        tablaResultados.getColumns().add(columnaResultado("Tiempo (ms)", "milisegundos", 180));
    }

    private void configurarGrafica() {
        graficaTiempos.setTitle("Tiempo de ejecución por algoritmo");
        graficaTiempos.setLegendVisible(false);
        graficaTiempos.setAnimated(false);
        ejeAlgoritmos.setLabel("Algoritmo");
        ejeTiempo.setLabel("Tiempo (ms)");

    }

    private TableColumn<Cancion, Object> columna(String titulo, String propiedad, double ancho) {
        TableColumn<Cancion, Object> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        columna.setPrefWidth(ancho);
        columna.setSortable(false);
        return columna;
    }

    private TableColumn<ResultadoMedicion, Object> columnaResultado(
            String titulo, String propiedad, double ancho) {
        TableColumn<ResultadoMedicion, Object> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        columna.setPrefWidth(ancho);
        columna.setSortable(false);
        return columna;
    }

    private void cargarCanciones() {
        try {
            List<Cancion> canciones = new LectorCsv().leer(RUTA_CSV);
            // Se guarda el orden original para que las comparaciones siempre partan de la misma entrada.
            cancionesOriginales = List.copyOf(canciones);
            tablaCanciones.setItems(FXCollections.observableArrayList(canciones));
            panelGraficasCsv.mostrarDatos(canciones);
            estado.setText("Canciones cargadas: " + canciones.size());
            botonOrdenar.setDisable(false);
            botonComparar.setDisable(false);
        } catch (IOException error) {
            estado.setText("No se pudo cargar el CSV.");
            mostrarError("No fue posible cargar las canciones.", error.getMessage());
        }
    }

    private void ordenarCanciones() {
        if (tablaCanciones.getItems().isEmpty()) {
            estado.setText("Primero carga las canciones.");
            return;
        }

        // El encabezado del CSV identifica el comparador que corresponde a la selección del usuario.
        String nombreColumnaElegida = selectorColumna.getValue();
        String columnaElegida = buscarColumna(nombreColumnaElegida).encabezadoCsv();
        String algoritmoElegido = selectorAlgoritmo.getValue();
        Comparator<Cancion> comparador = ComparadorCancion.paraColumna(columnaElegida);
        Cancion[] datos = tablaCanciones.getItems().toArray(new Cancion[0]);

        estado.setText("Ordenando " + datos.length + " canciones...");
        botonOrdenar.setDisable(true);

        // El ordenamiento puede tardar; ejecutarlo en segundo plano mantiene activa la ventana.
        Task<OrdenamientoTerminado> tarea = new Task<>() {
            @Override
            protected OrdenamientoTerminado call() {
                long inicio = System.nanoTime();
                aplicarOrdenamiento(datos, algoritmoElegido, comparador);
                long nanosegundos = System.nanoTime() - inicio;
                return new OrdenamientoTerminado(datos, nanosegundos);
            }
        };

        tarea.setOnSucceeded(evento -> {
            // Las actualizaciones de la interfaz se hacen al terminar la tarea.
            OrdenamientoTerminado resultado = tarea.getValue();
            tablaCanciones.setItems(FXCollections.observableArrayList(
                    Arrays.asList(resultado.canciones())
            ));
            estado.setText(algoritmoElegido + " ordenó " + datos.length
                    + " canciones por " + nombreColumnaElegida + " en "
                    + String.format("%.3f ms", resultado.nanosegundos() / 1_000_000.0));
            botonOrdenar.setDisable(false);
        });

        tarea.setOnFailed(evento -> {
            botonOrdenar.setDisable(false);
            mostrarError("No se pudo ordenar la tabla.", tarea.getException().getMessage());
        });

        Thread hilo = new Thread(tarea, "ordenamiento-canciones");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void compararTiempos() {
        if (cancionesOriginales.isEmpty()) {
            estado.setText("Primero carga las canciones.");
            return;
        }

        // Se valida el tamaño antes de tomar la muestra para evitar valores fuera del conjunto.
        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
        } catch (NumberFormatException error) {
            mostrarError("Cantidad inválida.", "Escribe un número entero de canciones.");
            return;
        }

        if (cantidad < 1 || cantidad > cancionesOriginales.size()) {
            mostrarError("Cantidad fuera de rango.",
                    "Escribe un número entre 1 y " + cancionesOriginales.size() + ".");
            return;
        }

        String nombreColumnaElegida = selectorColumna.getValue();
        String columnaElegida = buscarColumna(nombreColumnaElegida).encabezadoCsv();
        // Todos los algoritmos medirán estas mismas canciones en el orden original.
        Cancion[] muestra = cancionesOriginales.subList(0, cantidad).toArray(new Cancion[0]);

        estado.setText("Comparando los seis algoritmos con " + cantidad + " canciones...");
        botonComparar.setDisable(true);

        Task<List<ResultadoMedicion>> tarea = new Task<>() {
            @Override
            protected List<ResultadoMedicion> call() {
                return new ServicioBenchmark().medirTodos(muestra, columnaElegida);
            }
        };

        tarea.setOnSucceeded(evento -> {
            List<ResultadoMedicion> resultados = tarea.getValue();
            tablaResultados.setItems(FXCollections.observableArrayList(resultados));
            actualizarGrafica(resultados);
            estado.setText("Comparación por " + nombreColumnaElegida + " completada; "
                    + "cada algoritmo recibió la misma muestra de " + cantidad + " canciones.");
            botonComparar.setDisable(false);
        });

        tarea.setOnFailed(evento -> {
            botonComparar.setDisable(false);
            mostrarError("No se pudieron comparar los algoritmos.",
                    tarea.getException().getMessage());
        });

        Thread hilo = new Thread(tarea, "comparacion-algoritmos");
        hilo.setDaemon(true);
        hilo.start();
    }

    private DefinicionColumna buscarColumna(String etiqueta) {
        // La etiqueta está en español; para ordenar necesitamos el encabezado exacto del CSV.
        return COLUMNAS.stream()
                .filter(columna -> columna.etiqueta().equals(etiqueta))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Columna desconocida: " + etiqueta));
    }

    private void actualizarGrafica(List<ResultadoMedicion> resultados) {
        // Cada resultado aporta una barra: algoritmo en el eje horizontal y milisegundos en el vertical.
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (ResultadoMedicion resultado : resultados) {
            serie.getData().add(new XYChart.Data<>(
                    resultado.getAlgoritmo(), resultado.getMilisegundos()
            ));
        }
        graficaTiempos.getData().setAll(serie);
    }


    private void aplicarOrdenamiento(Cancion[] datos, String algoritmo,
                                    Comparator<Cancion> comparador) {
        switch (algoritmo) {
            case "Selección directa" -> OrdenamientoSeleccion.ordenar(datos, comparador);
            case "Shell sort" -> OrdenamientoShell.ordenar(datos, comparador);
            case "Quicksort" -> OrdenamientoQuickSort.ordenar(datos, comparador);
            case "Mergesort" -> OrdenamientoMergeSort.ordenar(datos, comparador);
            case "Arrays.sort" -> OrdenamientoArrays.ordenar(datos, comparador);
            case "Arrays.parallelSort" -> OrdenamientoArrays.ordenarEnParalelo(datos, comparador);
            default -> throw new IllegalArgumentException("Algoritmo desconocido: " + algoritmo);
        }
    }

    private void mostrarError(String encabezado, String detalle) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(encabezado);
        alerta.setContentText(detalle);
        alerta.showAndWait();
    }

    private record OrdenamientoTerminado(Cancion[] canciones, long nanosegundos) {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
