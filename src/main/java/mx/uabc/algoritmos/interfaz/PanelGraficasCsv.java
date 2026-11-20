package mx.uabc.algoritmos.interfaz;

import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import mx.uabc.algoritmos.modelo.Cancion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Muestra las distribuciones de cuatro columnas visibles en el detalle del CSV. */
public class PanelGraficasCsv extends GridPane {

    private static final int INTERVALOS_DURACION = 8;
    private static final int INTERVALOS_BAILABILIDAD = 10;

    private final BarChart<String, Number> graficaDuracion = crearGraficaBarras(
            "Duración de las pistas", "Duración (minutos)", "Canciones"
    );
    private final PieChart graficaExplicita = new PieChart();
    private final PieChart graficaAlbumes = new PieChart();
    private final Label resumenExplicita = new Label();
    private final Label resumenAlbumes = new Label();
    private final BarChart<String, Number> graficaBailabilidad = crearGraficaBarras(
            "Bailabilidad de las pistas", "Puntuación de bailabilidad", "Canciones"
    );

    public PanelGraficasCsv() {
        setHgap(12);
        setVgap(12);
        setMinHeight(500);
        setPrefHeight(540);

        ColumnConstraints columnaIzquierda = new ColumnConstraints();
        columnaIzquierda.setPercentWidth(50);
        columnaIzquierda.setHgrow(Priority.ALWAYS);
        ColumnConstraints columnaDerecha = new ColumnConstraints();
        columnaDerecha.setPercentWidth(50);
        columnaDerecha.setHgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(columnaIzquierda, columnaDerecha);

        RowConstraints filaSuperior = new RowConstraints();
        filaSuperior.setPercentHeight(50);
        filaSuperior.setVgrow(Priority.ALWAYS);
        RowConstraints filaInferior = new RowConstraints();
        filaInferior.setPercentHeight(50);
        filaInferior.setVgrow(Priority.ALWAYS);
        getRowConstraints().addAll(filaSuperior, filaInferior);

        graficaExplicita.setTitle("Contenido explícito");
        graficaExplicita.setLabelsVisible(false);
        graficaAlbumes.setTitle("Álbumes de las pistas");
        graficaAlbumes.setLabelsVisible(false);
        graficaDuracion.setPrefHeight(230);
        graficaBailabilidad.setPrefHeight(230);

        add(crearPanel(graficaDuracion), 0, 0);
        add(crearPanelCircular(graficaExplicita, resumenExplicita), 1, 0);
        add(crearPanelCircular(graficaAlbumes, resumenAlbumes), 0, 1);
        add(crearPanel(graficaBailabilidad), 1, 1);
    }

    /** Actualiza las cuatro gráficas después de cargar el CSV. */
    public void mostrarDatos(List<Cancion> canciones) {
        mostrarDistribucionDuracion(canciones);
        mostrarDistribucionExplicita(canciones);
        mostrarDistribucionAlbumes(canciones);
        mostrarDistribucionBailabilidad(canciones);
    }

    private VBox crearPanel(javafx.scene.Node grafica) {
        VBox panel = new VBox(grafica);
        GridPane.setHgrow(panel, Priority.ALWAYS);
        GridPane.setVgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(grafica, Priority.ALWAYS);
        return panel;
    }

    private VBox crearPanelCircular(PieChart grafica, Label resumen) {
        resumen.setWrapText(true);
        resumen.setMaxWidth(190);
        resumen.setStyle("-fx-font-size: 12px;");
        grafica.setMinSize(190, 180);
        grafica.setPrefHeight(230);

        HBox contenido = new HBox(8, grafica, resumen);
        HBox.setHgrow(grafica, Priority.ALWAYS);
        return crearPanel(contenido);
    }

    private BarChart<String, Number> crearGraficaBarras(
            String titulo, String etiquetaHorizontal, String etiquetaVertical) {
        CategoryAxis ejeHorizontal = new CategoryAxis();
        ejeHorizontal.setLabel(etiquetaHorizontal);
        ejeHorizontal.setTickLabelRotation(20);
        NumberAxis ejeVertical = new NumberAxis();
        ejeVertical.setLabel(etiquetaVertical);

        BarChart<String, Number> grafica = new BarChart<>(ejeHorizontal, ejeVertical);
        grafica.setTitle(titulo);
        grafica.setLegendVisible(false);
        grafica.setAnimated(false);
        grafica.setMinHeight(220);
        grafica.setPrefHeight(245);
        return grafica;
    }

    private void mostrarDistribucionDuracion(List<Cancion> canciones) {
        List<Double> valores = new ArrayList<>();
        for (Cancion cancion : canciones) {
            Long valor = cancion.getSpotifyTrackDurationMs();
            if (valor != null) {
                valores.add(valor / 60_000.0);
            }
        }
        // Las canciones de más de ocho minutos se agrupan para que un valor extremo
        // no comprima la distribución de las duraciones habituales.
        mostrarHistograma(graficaDuracion, valores, 0, 8,
                INTERVALOS_DURACION, true, " min");
    }

    private void mostrarDistribucionBailabilidad(List<Cancion> canciones) {
        List<Double> valores = new ArrayList<>();
        for (Cancion cancion : canciones) {
            Double valor = cancion.getDanceability();
            if (valor != null) {
                valores.add(valor);
            }
        }
        mostrarHistograma(graficaBailabilidad, valores, 0, 1,
                INTERVALOS_BAILABILIDAD, false, "");
    }

    private void mostrarHistograma(BarChart<String, Number> grafica,
                                   List<Double> valores,
                                   double minimo, double maximo,
                                   int cantidadIntervalos,
                                   boolean agregarIntervaloSuperior,
                                   String unidad) {
        if (valores.isEmpty()) {
            grafica.getData().clear();
            return;
        }

        double anchoIntervalo = (maximo - minimo) / cantidadIntervalos;
        int cantidadBarras = cantidadIntervalos + (agregarIntervaloSuperior ? 1 : 0);
        int[] cantidades = new int[cantidadBarras];

        // Cada barra cuenta cuántas canciones tienen un valor dentro de su intervalo.
        for (double valor : valores) {
            int indice;
            if (agregarIntervaloSuperior && valor > maximo) {
                indice = cantidadIntervalos;
            } else {
                indice = (int) ((valor - minimo) / anchoIntervalo);
                indice = Math.max(0, Math.min(indice, cantidadIntervalos - 1));
            }
            cantidades[indice]++;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < cantidadIntervalos; i++) {
            double inicio = minimo + i * anchoIntervalo;
            double fin = inicio + anchoIntervalo;
            String intervalo = String.format("%.1f–%.1f%s", inicio, fin, unidad);
            serie.getData().add(new XYChart.Data<>(intervalo, cantidades[i]));
        }
        if (agregarIntervaloSuperior) {
            serie.getData().add(new XYChart.Data<>("> " + maximo + unidad,
                    cantidades[cantidadIntervalos]));
        }
        grafica.getData().setAll(FXCollections.observableArrayList(serie));
    }

    private void mostrarDistribucionExplicita(List<Cancion> canciones) {
        int explicitas = 0;
        int noExplicitas = 0;
        int sinDato = 0;

        for (Cancion cancion : canciones) {
            Boolean esExplicita = cancion.getSpotifyTrackExplicit();
            if (esExplicita == null) {
                sinDato++;
            } else if (esExplicita) {
                explicitas++;
            } else {
                noExplicitas++;
            }
        }

        List<PieChart.Data> porciones = new ArrayList<>();
        agregarPorcion(porciones, "true", explicitas, canciones.size());
        agregarPorcion(porciones, "false", noExplicitas, canciones.size());
        agregarPorcion(porciones, "null", sinDato, canciones.size());
        graficaExplicita.setData(FXCollections.observableArrayList(porciones));
        resumenExplicita.setText("true  " + formatoConteo(explicitas, canciones.size())
                + "\nfalse " + formatoConteo(noExplicitas, canciones.size())
                + "\nnull  " + formatoConteo(sinDato, canciones.size()));
    }

    private void mostrarDistribucionAlbumes(List<Cancion> canciones) {
        Map<String, Integer> cantidadesPorAlbum = new HashMap<>();
        int sinDato = 0;

        for (Cancion cancion : canciones) {
            String album = cancion.getSpotifyTrackAlbum();
            if (album == null || album.isBlank()) {
                sinDato++;
            } else {
                cantidadesPorAlbum.merge(album, 1, Integer::sum);
            }
        }

        // Se conserva el álbum más frecuente y se agrupan los demás como en la vista resumida.
        Map.Entry<String, Integer> albumMasFrecuente = cantidadesPorAlbum.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        int otros = cantidadesPorAlbum.values().stream().mapToInt(Integer::intValue).sum();
        List<PieChart.Data> porciones = new ArrayList<>();
        agregarPorcion(porciones, "[null]", sinDato, canciones.size());
        String albumPrincipal = "Sin álbum disponible";
        int cantidadAlbumPrincipal = 0;

        if (albumMasFrecuente != null) {
            agregarPorcion(porciones, albumMasFrecuente.getKey(),
                    albumMasFrecuente.getValue(), canciones.size());
            albumPrincipal = albumMasFrecuente.getKey();
            cantidadAlbumPrincipal = albumMasFrecuente.getValue();
            otros -= albumMasFrecuente.getValue();
        }
        agregarPorcion(porciones, "Other", otros, canciones.size());
        graficaAlbumes.setData(FXCollections.observableArrayList(porciones));
        resumenAlbumes.setText("[null]  " + formatoConteo(sinDato, canciones.size())
                + "\n" + albumPrincipal + "  "
                + formatoConteo(cantidadAlbumPrincipal, canciones.size())
                + "\nOther  " + formatoConteo(otros, canciones.size()));
    }

    private void agregarPorcion(List<PieChart.Data> porciones,
                                String nombre, int cantidad, int total) {
        if (cantidad == 0 || total == 0) {
            return;
        }
        porciones.add(new PieChart.Data(nombre, cantidad));
    }

    private String formatoConteo(int cantidad, int total) {
        if (total == 0) {
            return "0 (0.0%)";
        }
        double porcentaje = 100.0 * cantidad / total;
        return String.format("%d (%.1f%%)", cantidad, porcentaje);
    }
}
