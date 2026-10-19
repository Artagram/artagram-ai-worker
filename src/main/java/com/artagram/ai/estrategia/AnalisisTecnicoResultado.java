package com.artagram.ai.estrategia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalisisTecnicoResultado(
        List<String> puntosFuertes,
        List<String> areasMejora,
        List<String> consejos,
        int scoreEstimado,
        String paletaDetectada,
        String composicion,
        String diagnosticoGeneral
) {
}
