package com.artagram.ai.domain;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("analisis_ia_cache")
public class AnalisisIaDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID publicacionId;

    private UUID autorId;
    private String estado;
    private String diagnosticoGeneral;
    private String paletaDetectada;
    private String composicion;
    private String puntosMejora;
    private String sugerencias;
    private Integer puntuacionArmonia;
    private String modeloUtilizado;
    private String respuestaRaw;
    private Instant creadoEn;

    public AnalisisIaDocument() {
    }

    public AnalisisIaDocument(UUID id, UUID publicacionId, UUID autorId, String estado,
                              String diagnosticoGeneral, String paletaDetectada, String composicion,
                              String puntosMejora, String sugerencias, Integer puntuacionArmonia,
                              String modeloUtilizado, String respuestaRaw) {
        this.id = id != null ? id : UUID.randomUUID();
        this.publicacionId = publicacionId;
        this.autorId = autorId;
        this.estado = estado;
        this.diagnosticoGeneral = diagnosticoGeneral;
        this.paletaDetectada = paletaDetectada;
        this.composicion = composicion;
        this.puntosMejora = puntosMejora;
        this.sugerencias = sugerencias;
        this.puntuacionArmonia = puntuacionArmonia;
        this.modeloUtilizado = modeloUtilizado;
        this.respuestaRaw = respuestaRaw;
        this.creadoEn = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getPublicacionId() { return publicacionId; }
    public UUID getAutorId() { return autorId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDiagnosticoGeneral() { return diagnosticoGeneral; }
    public String getPaletaDetectada() { return paletaDetectada; }
    public String getComposicion() { return composicion; }
    public String getPuntosMejora() { return puntosMejora; }
    public String getSugerencias() { return sugerencias; }
    public Integer getPuntuacionArmonia() { return puntuacionArmonia; }
    public String getModeloUtilizado() { return modeloUtilizado; }
    public String getRespuestaRaw() { return respuestaRaw; }
    public Instant getCreadoEn() { return creadoEn; }
}
