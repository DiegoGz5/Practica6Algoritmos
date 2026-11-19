package mx.uabc.algoritmos.ordenamiento;

import mx.uabc.algoritmos.modelo.Cancion;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

/**
 * Relaciona cada encabezado del CSV con la regla para comparar ese dato
 * entre dos canciones.
 */
public class ComparadorCancion {

    // La llave es el encabezado del CSV y el valor sabe comparar ese campo.
    private static final Map<String, Comparator<Cancion>> POR_COLUMNA = Map.ofEntries(
            Map.entry("song_id", por(Cancion::getSongId)),
            Map.entry("performer", por(Cancion::getPerformer)),
            Map.entry("song", por(Cancion::getSong)),
            Map.entry("spotify_genre", por(Cancion::getSpotifyGenre)),
            Map.entry("spotify_track_id", por(Cancion::getSpotifyTrackId)),
            Map.entry("spotify_track_preview_url", por(Cancion::getSpotifyTrackPreviewUrl)),
            Map.entry("spotify_track_duration_ms", por(Cancion::getSpotifyTrackDurationMs)),
            Map.entry("spotify_track_explicit", por(Cancion::getSpotifyTrackExplicit)),
            Map.entry("spotify_track_album", por(Cancion::getSpotifyTrackAlbum)),
            Map.entry("danceability", por(Cancion::getDanceability)),
            Map.entry("energy", por(Cancion::getEnergy)),
            Map.entry("key", por(Cancion::getKey)),
            Map.entry("loudness", por(Cancion::getLoudness)),
            Map.entry("mode", por(Cancion::getMode)),
            Map.entry("speechiness", por(Cancion::getSpeechiness)),
            Map.entry("acousticness", por(Cancion::getAcousticness)),
            Map.entry("instrumentalness", por(Cancion::getInstrumentalness)),
            Map.entry("liveness", por(Cancion::getLiveness)),
            Map.entry("valence", por(Cancion::getValence)),
            Map.entry("tempo", por(Cancion::getTempo)),
            Map.entry("time_signature", por(Cancion::getTimeSignature)),
            Map.entry("spotify_track_popularity", por(Cancion::getSpotifyTrackPopularity)),
            Map.entry("peak_position", por(Cancion::getPeakPosition)),
            Map.entry("weeks_on_chart", por(Cancion::getWeeksOnChart)),
            Map.entry("first_week", por(Cancion::getFirstWeek)),
            Map.entry("last_week", por(Cancion::getLastWeek)),
            Map.entry("chart_success_tier", por(Cancion::getChartSuccessTier)),
            Map.entry("hit_longevity_profile", por(Cancion::getHitLongevityProfile)),
            Map.entry("acoustic_mood_cluster", por(Cancion::getAcousticMoodCluster)),
            Map.entry("musical_era", por(Cancion::getMusicalEra)),
            Map.entry("evidence_span", por(Cancion::getEvidenceSpan)),
            Map.entry("cot_rationale", por(Cancion::getCotRationale)),
            Map.entry("confidence_score", por(Cancion::getConfidenceScore))
    );

    private ComparadorCancion() {
    }

    /** Devuelve la regla de comparación correspondiente a una columna del CSV. */
    public static Comparator<Cancion> paraColumna(String nombreColumna) {
        Comparator<Cancion> comparador = POR_COLUMNA.get(nombreColumna);
        if (comparador == null) {
            throw new IllegalArgumentException("Columna no reconocida: " + nombreColumna);
        }
        return comparador;
    }

    private static <T extends Comparable<? super T>> Comparator<Cancion> por(
            Function<Cancion, T> obtenerValor) {
        // Los valores nulos se colocan al final; los demás se comparan en orden natural.
        return Comparator.comparing(
                obtenerValor,
                Comparator.nullsLast(Comparator.naturalOrder())
        );
    }
}
