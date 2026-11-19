package mx.uabc.algoritmos.datos;

import mx.uabc.algoritmos.modelo.Cancion;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee el archivo CSV y convierte cada registro en un objeto Cancion.
 * Así, el resto del programa trabaja con objetos Java en lugar de cadenas del archivo.
 */
public class LectorCsv {

    private static final CSVFormat FORMATO_CSV = CSVFormat.RFC4180.builder()
            // Los nombres del encabezado permiten buscar los datos por columna.
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();

    /**
     * Lee todas las canciones del archivo indicado.
     *
     * @param ruta ubicación del archivo CSV
     * @return canciones encontradas en el archivo
     * @throws IOException si no se puede leer el archivo o convertir una fila
     */
    public List<Cancion> leer(Path ruta) throws IOException {
        List<Cancion> canciones = new ArrayList<>();

        try (CSVParser parser = CSVParser.parse(ruta, StandardCharsets.UTF_8, FORMATO_CSV)) {
            for (CSVRecord registro : parser) {
                // Una fila incompleta o con columnas extra no se puede convertir con seguridad.
                if (!registro.isConsistent()) {
                    throw new IOException("La fila " + registro.getRecordNumber()
                            + " no tiene la misma cantidad de columnas que el encabezado.");
                }

                try {
                    canciones.add(convertir(registro));
                } catch (RuntimeException error) {
                    throw new IOException("No se pudo convertir la fila "
                            + registro.getRecordNumber() + ".", error);
                }
            }
        }

        return canciones;
    }

    /** Convierte una fila del CSV a un objeto Cancion. */
    private Cancion convertir(CSVRecord registro) {
        // El constructor recibe los tres datos básicos; los demás campos se asignan después.
        Cancion cancion = new Cancion(
                texto(registro, "song_id"),
                texto(registro, "performer"),
                texto(registro, "song")
        );

        cancion.setSpotifyGenre(texto(registro, "spotify_genre"));
        cancion.setSpotifyTrackId(texto(registro, "spotify_track_id"));
        cancion.setSpotifyTrackPreviewUrl(texto(registro, "spotify_track_preview_url"));
        cancion.setSpotifyTrackDurationMs(enteroLargo(registro, "spotify_track_duration_ms"));
        cancion.setSpotifyTrackExplicit(booleano(registro, "spotify_track_explicit"));
        cancion.setSpotifyTrackAlbum(texto(registro, "spotify_track_album"));

        cancion.setDanceability(decimal(registro, "danceability"));
        cancion.setEnergy(decimal(registro, "energy"));
        cancion.setKey(entero(registro, "key"));
        cancion.setLoudness(decimal(registro, "loudness"));
        cancion.setMode(entero(registro, "mode"));
        cancion.setSpeechiness(decimal(registro, "speechiness"));
        cancion.setAcousticness(decimal(registro, "acousticness"));
        cancion.setInstrumentalness(decimal(registro, "instrumentalness"));
        cancion.setLiveness(decimal(registro, "liveness"));
        cancion.setValence(decimal(registro, "valence"));
        cancion.setTempo(decimal(registro, "tempo"));
        cancion.setTimeSignature(entero(registro, "time_signature"));
        cancion.setSpotifyTrackPopularity(entero(registro, "spotify_track_popularity"));

        cancion.setPeakPosition(entero(registro, "peak_position"));
        cancion.setWeeksOnChart(entero(registro, "weeks_on_chart"));
        cancion.setFirstWeek(fecha(registro, "first_week"));
        cancion.setLastWeek(fecha(registro, "last_week"));
        cancion.setChartSuccessTier(texto(registro, "chart_success_tier"));

        cancion.setHitLongevityProfile(texto(registro, "hit_longevity_profile"));
        cancion.setAcousticMoodCluster(texto(registro, "acoustic_mood_cluster"));
        cancion.setMusicalEra(texto(registro, "musical_era"));
        cancion.setEvidenceSpan(texto(registro, "evidence_span"));
        cancion.setCotRationale(texto(registro, "cot_rationale"));
        cancion.setConfidenceScore(decimal(registro, "confidence_score"));

        return cancion;
    }

    /** Devuelve el texto tal como aparece; una celda vacía se representa con null. */
    private String texto(CSVRecord registro, String columna) {
        String valor = registro.get(columna);
        return valor.isBlank() ? null : valor;
    }

    private Double decimal(CSVRecord registro, String columna) {
        String valor = texto(registro, columna);
        // Los tipos envolventes permiten guardar null cuando la celda está vacía.
        return valor == null ? null : new BigDecimal(valor).doubleValue();
    }

    private Integer entero(CSVRecord registro, String columna) {
        String valor = texto(registro, columna);
        // intValueExact evita convertir silenciosamente un valor que no sea entero.
        return valor == null ? null : new BigDecimal(valor).intValueExact();
    }

    private Long enteroLargo(CSVRecord registro, String columna) {
        String valor = texto(registro, columna);
        // BigDecimal acepta valores como 201573.0 y verifica que representen un entero exacto.
        return valor == null ? null : new BigDecimal(valor).longValueExact();
    }

    private Boolean booleano(CSVRecord registro, String columna) {
        String valor = texto(registro, columna);
        if (valor == null) {
            return null;
        }
        if (valor.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (valor.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Valor booleano inválido en " + columna + ": " + valor);
    }

    private LocalDate fecha(CSVRecord registro, String columna) {
        String valor = texto(registro, columna);
        return valor == null ? null : LocalDate.parse(valor);
    }
}
