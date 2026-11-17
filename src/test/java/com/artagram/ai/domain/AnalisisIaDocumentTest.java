package com.artagram.ai.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AnalisisIaDocumentTest {

    @Test
    void guardaTodosLosCamposYGeneraIdSiNoViene() {
        UUID pub = UUID.randomUUID();
        UUID autor = UUID.randomUUID();
        AnalisisIaDocument doc = new AnalisisIaDocument(null, pub, autor, "COMPLETADO", "diag", "#fff",
                "comp", "mejora", "sug", 80, "modelo", "{}");

        assertThat(doc.getId()).isNotNull();
        assertThat(doc.getPublicacionId()).isEqualTo(pub);
        assertThat(doc.getAutorId()).isEqualTo(autor);
        assertThat(doc.getEstado()).isEqualTo("COMPLETADO");
        assertThat(doc.getDiagnosticoGeneral()).isEqualTo("diag");
        assertThat(doc.getPaletaDetectada()).isEqualTo("#fff");
        assertThat(doc.getComposicion()).isEqualTo("comp");
        assertThat(doc.getPuntosMejora()).isEqualTo("mejora");
        assertThat(doc.getSugerencias()).isEqualTo("sug");
        assertThat(doc.getPuntuacionArmonia()).isEqualTo(80);
        assertThat(doc.getModeloUtilizado()).isEqualTo("modelo");
        assertThat(doc.getRespuestaRaw()).isEqualTo("{}");
        assertThat(doc.getCreadoEn()).isNotNull();

        doc.setEstado("FALLIDO");
        assertThat(doc.getEstado()).isEqualTo("FALLIDO");

        UUID id = UUID.randomUUID();
        assertThat(new AnalisisIaDocument(id, pub, autor, "X", null, null, null, null, null, null, null, null).getId())
                .isEqualTo(id);
        assertThat(new AnalisisIaDocument().getId()).isNull();
    }
}
