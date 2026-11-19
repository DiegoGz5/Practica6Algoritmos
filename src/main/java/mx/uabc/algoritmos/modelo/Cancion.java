package mx.uabc.algoritmos.modelo;

import java.time.LocalDate;

/**
 * Representa una fila del conjunto de datos de Billboard y Spotify.
 * Sus atributos corresponden a las columnas del CSV.
 */
public class Cancion {

    // Columnas de identidad de la canción y el artista.
    private String songId;
    private String performer;
    private String song;

    // Metadatos de Spotify; pueden faltar en algunas filas, por eso usan tipos envolventes.
    private String spotifyGenre;
    private String spotifyTrackId;
    private String spotifyTrackPreviewUrl;
    private Long spotifyTrackDurationMs;
    private Boolean spotifyTrackExplicit;
    private String spotifyTrackAlbum;

    // Características continuas de audio; varias se expresan como puntuaciones entre 0 y 1.
    private Double danceability;
    private Double energy;
    private Double loudness;
    private Double speechiness;
    private Double acousticness;
    private Double instrumentalness;
    private Double liveness;
    private Double valence;
    private Double tempo;

    // Características numéricas que representan categorías o conteos.
    private Integer key;
    private Integer mode;
    private Integer timeSignature;
    private Integer spotifyTrackPopularity;

    // Información de desempeño en Billboard; las fechas identifican la primera y última semana registradas.
    private Integer peakPosition;
    private Integer weeksOnChart;
    private LocalDate firstWeek;
    private LocalDate lastWeek;
    private String chartSuccessTier;
    // Etiquetas y descripciones enriquecidas que acompañan los datos originales.
    private String hitLongevityProfile;
    private String acousticMoodCluster;
    private String musicalEra;
    private String evidenceSpan;
    private String cotRationale;
    private Double confidenceScore;

    /**
     * Crea una canción con sus datos básicos de identidad.
     */
    public Cancion(String songId, String performer, String song) {
        this.songId = songId;
        this.performer = performer;
        this.song = song;
    }

    public String getSongId() {
        return songId;
    }

    public String getPerformer() {
        return performer;
    }

    public String getSong() {
        return song;
    }

    public String getSpotifyGenre() {
        return spotifyGenre;
    }

    public void setSpotifyGenre(String spotifyGenre) {
        this.spotifyGenre = spotifyGenre;
    }

    public String getSpotifyTrackId() {
        return spotifyTrackId;
    }

    public void setSpotifyTrackId(String spotifyTrackId) {
        this.spotifyTrackId = spotifyTrackId;
    }

    public String getSpotifyTrackPreviewUrl() {
        return spotifyTrackPreviewUrl;
    }

    public void setSpotifyTrackPreviewUrl(String spotifyTrackPreviewUrl) {
        this.spotifyTrackPreviewUrl = spotifyTrackPreviewUrl;
    }

    public Long getSpotifyTrackDurationMs() {
        return spotifyTrackDurationMs;
    }

    public void setSpotifyTrackDurationMs(Long spotifyTrackDurationMs) {
        this.spotifyTrackDurationMs = spotifyTrackDurationMs;
    }

    public Boolean getSpotifyTrackExplicit() {
        return spotifyTrackExplicit;
    }

    public void setSpotifyTrackExplicit(Boolean spotifyTrackExplicit) {
        this.spotifyTrackExplicit = spotifyTrackExplicit;
    }

    public String getSpotifyTrackAlbum() {
        return spotifyTrackAlbum;
    }

    public void setSpotifyTrackAlbum(String spotifyTrackAlbum) {
        this.spotifyTrackAlbum = spotifyTrackAlbum;
    }

    public Double getDanceability() {
        return danceability;
    }

    public void setDanceability(Double danceability) {
        this.danceability = danceability;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Double getLoudness() {
        return loudness;
    }

    public void setLoudness(Double loudness) {
        this.loudness = loudness;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Double getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(Double speechiness) {
        this.speechiness = speechiness;
    }

    public Double getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(Double acousticness) {
        this.acousticness = acousticness;
    }

    public Double getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(Double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public Double getLiveness() {
        return liveness;
    }

    public void setLiveness(Double liveness) {
        this.liveness = liveness;
    }

    public Double getValence() {
        return valence;
    }

    public void setValence(Double valence) {
        this.valence = valence;
    }

    public Double getTempo() {
        return tempo;
    }

    public void setTempo(Double tempo) {
        this.tempo = tempo;
    }

    public Integer getTimeSignature() {
        return timeSignature;
    }

    public void setTimeSignature(Integer timeSignature) {
        this.timeSignature = timeSignature;
    }

    public Integer getSpotifyTrackPopularity() {
        return spotifyTrackPopularity;
    }

    public void setSpotifyTrackPopularity(Integer spotifyTrackPopularity) {
        this.spotifyTrackPopularity = spotifyTrackPopularity;
    }
    public Integer getPeakPosition() {
        return peakPosition;
    }

    public void setPeakPosition(Integer peakPosition) {
        this.peakPosition = peakPosition;
    }

    public Integer getWeeksOnChart() {
        return weeksOnChart;
    }

    public void setWeeksOnChart(Integer weeksOnChart) {
        this.weeksOnChart = weeksOnChart;
    }

    public LocalDate getFirstWeek() {
        return firstWeek;
    }

    public void setFirstWeek(LocalDate firstWeek) {
        this.firstWeek = firstWeek;
    }

    public LocalDate getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(LocalDate lastWeek) {
        this.lastWeek = lastWeek;
    }

    public String getChartSuccessTier() {
        return chartSuccessTier;
    }

    public void setChartSuccessTier(String chartSuccessTier) {
        this.chartSuccessTier = chartSuccessTier;
    }

    public String getHitLongevityProfile() {
        return hitLongevityProfile;
    }

    public void setHitLongevityProfile(String hitLongevityProfile) {
        this.hitLongevityProfile = hitLongevityProfile;
    }

    public String getAcousticMoodCluster() {
        return acousticMoodCluster;
    }

    public void setAcousticMoodCluster(String acousticMoodCluster) {
        this.acousticMoodCluster = acousticMoodCluster;
    }

    public String getMusicalEra() {
        return musicalEra;
    }

    public void setMusicalEra(String musicalEra) {
        this.musicalEra = musicalEra;
    }

    public String getEvidenceSpan() {
        return evidenceSpan;
    }

    public void setEvidenceSpan(String evidenceSpan) {
        this.evidenceSpan = evidenceSpan;
    }

    public String getCotRationale() {
        return cotRationale;
    }

    public void setCotRationale(String cotRationale) {
        this.cotRationale = cotRationale;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}



