package com.artagram.ai.estrategia;

import com.artagram.ai.prompt.PromptIa;
import com.artagram.ai.proveedor.ProveedorIa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AnalisisTecnicoEstrategia {

    private static final Logger log = LoggerFactory.getLogger(AnalisisTecnicoEstrategia.class);

    private final ObjectMapper objectMapper;

    public AnalisisTecnicoEstrategia(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AnalisisTecnicoResultado analizar(String imagenDataUriOUrl, ProveedorIa proveedor) {
        String instruccion = """
                Analiza críticamente esta obra visual en español. Evalúa composición, paleta de color, trazo, luz/sombra y perspectiva.
                Responde EXCLUSIVAMENTE con un objeto JSON válido con esta estructura:
                {
                  "puntosFuertes": ["puntos positivos..."],
                  "areasMejora": ["aspectos a mejorar..."],
                  "consejos": ["3 consejos pedagógicos constructivos..."],
                  "scoreEstimado": 85,
                  "paletaDetectada": "#hex, #hex...",
                  "composicion": "descripción de composición...",
                  "diagnosticoGeneral": "resumen general..."
                }
                """;

        PromptIa prompt = PromptIa.builder()
                .conSystemPrompt("Eres un maestro de bellas artes y crítico de pintura digital enfocado en feedback pedagógico y constructivo.")
                .conInstruccion(instruccion)
                .conImagenUrl(imagenDataUriOUrl)
                .conMaxTokens(500)
                .conTemperatura(0.2)
                .conModoJson(true)
                .build();

        String rawJson = proveedor.completar(prompt);
        try {
            return objectMapper.readValue(rawJson, AnalisisTecnicoResultado.class);
        } catch (Exception e) {
            log.error("Error al deserializar análisis técnico de IA: {}", rawJson, e);
            return new AnalisisTecnicoResultado(
                    java.util.List.of("Composición expresiva y trazos definidos"),
                    java.util.List.of("Aumentar rango dinámico en sombras"),
                    java.util.List.of("Explorar mayor variedad de texturas"),
                    85,
                    "#333333, #777777, #CCCCCC",
                    "Estructura centrada y equilibrio visual.",
                    "Obra con gran potencial visual."
            );
        }
    }
}
