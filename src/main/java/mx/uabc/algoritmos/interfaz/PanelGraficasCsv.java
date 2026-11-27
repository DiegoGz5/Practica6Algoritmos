package mx.uabc.algoritmos.interfaz;

import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import mx.uabc.algoritmos.modelo.Cancion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Crea una gráfica de distribución para cada columna del conjunto de canciones. */
public class PanelGraficasCsv extends TilePane {

    private static final int INTERVALOS_NUMERICOS = 12;
    private static final int CATEGORIAS_VISIBLES = 1;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM");

    private final List<DefinicionGrafica> definiciones = List.of(
            categorica("ID de canción", Cancion::getSongId),
            categorica("Artista", Cancion::getPerformer),
            categorica("Canción", Cancion::getSong),
            categorica("Género de Spotify", Cancion::getSpotifyGenre),
            categorica("ID de pista", Cancion::getSpotifyTrackId),
            categorica("URL de vista previa", Cancion::getSpotifyTrackPreviewUrl),
            numerica("Duración de pista (minutos)", Cancion::getSpotifyTrackDurationMs,
                    valor -> valor.doubleValue() / 60_000.0, "minutos"),
            booleana("Contenido explícito", Cancion::getSpotifyTrackExplicit),
            categorica("Álbum", Cancion::getSpotifyTrackAlbum),
            numerica("Bailabilidad", Cancion::getDanceability, Number::doubleValue, "puntuación"),
            numerica("Energía", Cancion::getEnergy, Number::doubleValue, "puntuación"),
            numerica("Tonalidad", Cancion::getKey, Number::doubleValue, "valor"),
            numerica("Volumen", Cancion::getLoudness, Number::doubleValue, "dB"),
            numerica("Modo", Cancion::getMode, Number::doubleValue, "valor"),
            numerica("Habla", Cancion::getSpeechiness, Number::doubleValue, "puntuación"),
            numerica("Acústica", Cancion::getAcousticness, Number::doubleValue, "puntuación"),
            numerica("Instrumentalidad", Cancion::getInstrumentalness, Number::doubleValue, "puntuación"),
            numerica("Sonido en vivo", Cancion::getLiveness, Number::doubleValue, "puntuación"),
            numerica("Valencia", Cancion::getValence, Number::doubleValue, "puntuación"),
            numerica("Tempo", Cancion::getTempo, Number::doubleValue, "BPM"),
            numerica("Compás", Cancion::getTimeSignature, Number::doubleValue, "valor"),
            numerica("Popularidad de Spotify", Cancion::getSpotifyTrackPopularity,
                    Number::doubleValue, "puntos"),
            numerica("Mejor posición en Billboard", Cancion::getPeakPosition,
                    Number::doubleValue, "posición"),
            numerica("Semanas en Billboard", Cancion::getWeeksOnChart,
                    Number::doubleValue, "semanas"),
            fecha("Primera semana", Cancion::getFirstWeek),
            fecha("Última semana", Cancion::getLastWeek),
            categorica("Nivel de éxito", Cancion::getChartSuccessTier),
            categorica("Perfil de duración del éxito", Cancion::getHitLongevityProfile),
            categorica("Grupo de estado acústico", Cancion::getAcousticMoodCluster),
            categorica("Época musical", Cancion::getMusicalEra),
            categorica("Fragmento de evidencia", Cancion::getEvidenceSpan),
            categorica("Justificación", Cancion::getCotRationale),
            numerica("Puntaje de confianza", Cancion::getConfidenceScore,
                    Number::doubleValue, "puntuación")
    );

    public PanelGraficasCsv() {
        setOrientation(Orientation.HORIZONTAL);
        setHgap(14);
        setVgap(14);
        setPrefColumns(2);
        setPrefTileWidth(500);
        setPrefTileHeight(300);
        setMinWidth(480);
        setPrefWidth(1020);
    }

    /** Recalcula las distribuciones de todas las columnas a partir de las canciones cargadas. */
    public void mostrarDatos(List<Cancion> canciones) {
        getChildren().clear();
        for (DefinicionGrafica definicion : definiciones) {
            getChildren().add(crearTarjeta(definicion, canciones));
        }
    }

    private VBox crearTarjeta(DefinicionGrafica definicion, List<Cancion> canciones) {
        List<Object> valores = new ArrayList<>();
        int nulos = 0;
        for (Cancion cancion : canciones) {
            Object valor = definicion.obtenerValor().apply(cancion);
            if (valor == null) {
                nulos++;
            } else {
                valores.add(valor);
            }
        }

        Node grafica = switch (definicion.tipo()) {
            case NUMERICA -> crearHistogramaNumerico(definicion, valores);
            case FECHA -> crearHistogramaFecha(definicion, valores);
            case BOOLEANO -> crearGraficaBooleana(valores, nulos);
            case CATEGORICA -> crearGraficaCategorica(valores, nulos);
        };

        Label resumen = new Label("Con dato: " + valores.size() + "    Sin dato: " + nulos);
        resumen.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        VBox tarjeta = new VBox(5, new Label(definicion.titulo()), grafica, resumen);
        tarjeta.setStyle("-fx-padding: 10; -fx-background-color: white; "
                + "-fx-border-color: #d7dce2; -fx-border-radius: 4; -fx-background-radius: 4;");
        tarjeta.setMinWidth(480);
        tarjeta.setPrefWidth(500);
        tarjeta.setPrefHeight(285);
        VBox.setVgrow(grafica, Priority.ALWAYS);
        return tarjeta;
    }

    private Node crearHistogramaNumerico(DefinicionGrafica definicion, List<Object> valores) {
        List<Double> numeros = new ArrayList<>();
        for (Object valor : valores) {
            numeros.add(definicion.convertirNumero().apply((Number) valor));
        }
        return crearHistograma(definicion.unidad(), numeros,
                INTERVALOS_NUMERICOS, numero -> String.format("%.2f", numero));
    }

    private Node crearHistogramaFecha(DefinicionGrafica definicion, List<Object> valores) {
        List<Double> dias = new ArrayList<>();
        for (Object valor : valores) {
            dias.add((double) ((LocalDate) valor).toEpochDay());
        }
        return crearHistograma("Canciones", dias,
                INTERVALOS_NUMERICOS,
                numero -> LocalDate.ofEpochDay(Math.round(numero)).format(FORMATO_FECHA));
    }

    private Node crearHistograma(String unidad, List<Double> valores,
                                 int cantidadIntervalos,
                                 Function<Double, String> formatoExtremo) {
        CategoryAxis ejeX = new CategoryAxis();
        ejeX.setLabel(unidad);
        ejeX.setTickLabelRotation(25);
        NumberAxis ejeY = new NumberAxis();
        ejeY.setLabel("Canciones");
        BarChart<String, Number> grafica = new BarChart<>(ejeX, ejeY);
        grafica.setLegendVisible(false);
        grafica.setAnimated(false);
        grafica.setBarGap(1);
        grafica.setCategoryGap(2);

        if (valores.isEmpty()) {
            return grafica;
        }

        double minimo = valores.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
        double maximo = valores.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        if (minimo == maximo) {
            maximo = minimo + 1;
        }
        double ancho = (maximo - minimo) / cantidadIntervalos;
        int[] conteos = new int[cantidadIntervalos];
        for (double valor : valores) {
            int indice = (int) ((valor - minimo) / ancho);
            conteos[Math.max(0, Math.min(indice, cantidadIntervalos - 1))]++;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < cantidadIntervalos; i++) {
            double inicio = minimo + i * ancho;
            double fin = i == cantidadIntervalos - 1 ? maximo : inicio + ancho;
            String etiqueta = formatoExtremo.apply(inicio) + "–" + formatoExtremo.apply(fin);
            serie.getData().add(new XYChart.Data<>(etiqueta, conteos[i]));
        }
        grafica.getData().add(serie);
        return grafica;
    }

    private Node crearGraficaBooleana(List<Object> valores, int nulos) {
        int verdaderos = 0;
        int falsos = 0;
        for (Object valor : valores) {
            if (Boolean.TRUE.equals(valor)) {
                verdaderos++;
            } else {
                falsos++;
            }
        }

        PieChart grafica = new PieChart();
        grafica.setLabelsVisible(false);
        agregarPorcion(grafica, "true", verdaderos);
        agregarPorcion(grafica, "false", falsos);
        agregarPorcion(grafica, "[null]", nulos);
        return grafica;
    }

    private Node crearGraficaCategorica(List<Object> valores, int nulos) {
        Map<String, Integer> frecuencias = new HashMap<>();
        for (Object valor : valores) {
            frecuencias.merge(valor.toString(), 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> categoriasFrecuentes = frecuencias.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(CATEGORIAS_VISIBLES)
                .toList();
        int otros = valores.size();

        PieChart grafica = new PieChart();
        grafica.setLabelsVisible(false);
        if (nulos > 0) {
            agregarPorcion(grafica, "[null]", nulos);
        }
        for (Map.Entry<String, Integer> categoria : categoriasFrecuentes) {
            String etiqueta = acortar(categoria.getKey(), 24);
            agregarPorcion(grafica, etiqueta, categoria.getValue());
            otros -= categoria.getValue();
        }
        if (otros > 0) {
            agregarPorcion(grafica, "Other", otros);
        }
        return grafica;
    }

    private String acortar(String texto, int maximo) {
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo - 1) + "…";
    }

    private void agregarPorcion(PieChart grafica, String etiqueta, int cantidad) {
        if (cantidad > 0) {
            grafica.getData().add(new PieChart.Data(etiqueta, cantidad));
        }
    }

    private static DefinicionGrafica categorica(
            String titulo, Function<Cancion, ?> obtenerValor) {
        return new DefinicionGrafica(titulo, TipoDato.CATEGORICA,
                cancion -> obtenerValor.apply(cancion), null, "categorías");
    }

    private static DefinicionGrafica booleana(
            String titulo, Function<Cancion, Boolean> obtenerValor) {
        return new DefinicionGrafica(titulo, TipoDato.BOOLEANO,
                cancion -> obtenerValor.apply(cancion), null, "valores");
    }

    private static DefinicionGrafica numerica(
            String titulo, Function<Cancion, ? extends Number> obtenerValor,
            Function<Number, Double> convertir, String unidad) {
        return new DefinicionGrafica(titulo, TipoDato.NUMERICA,
                cancion -> obtenerValor.apply(cancion), convertir, unidad);
    }

    private static DefinicionGrafica fecha(
            String titulo, Function<Cancion, LocalDate> obtenerValor) {
        return new DefinicionGrafica(titulo, TipoDato.FECHA,
                cancion -> obtenerValor.apply(cancion), null, "fechas");
    }

    private enum TipoDato {
        NUMERICA, FECHA, BOOLEANO, CATEGORICA
    }

    private record DefinicionGrafica(String titulo, TipoDato tipo,
                                     Function<Cancion, Object> obtenerValor,
                                     Function<Number, Double> convertirNumero,
                                     String unidad) {
    }
}
