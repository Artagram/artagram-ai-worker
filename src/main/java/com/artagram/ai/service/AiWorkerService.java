package com.artagram.ai.service;

import com.artagram.ai.domain.AnalisisIaDocument;
import com.artagram.ai.domain.AnalisisIaMongoRepository;
import com.artagram.ai.estrategia.AnalisisTecnicoEstrategia;
import com.artagram.ai.estrategia.AnalisisTecnicoResultado;
import com.artagram.ai.proveedor.ProveedorIa;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiWorkerService {

    private static final Logger log = LoggerFactory.getLogger(AiWorkerService.class);

    private final AnalisisTecnicoEstrategia estrategia;
    private final ProveedorIa proveedorIa;
    private final AnalisisIaMongoRepository mongoRepo;
    private final RabbitTemplate rabbitTemplate;

    public AiWorkerService(AnalisisTecnicoEstrategia estrategia,
                           ProveedorIa proveedorIa,
                           AnalisisIaMongoRepository mongoRepo,
                           RabbitTemplate rabbitTemplate) {
        this.estrategia = estrategia;
        this.proveedorIa = proveedorIa;
        this.mongoRepo = mongoRepo;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void procesarAnalisis(UUID publicacionId, UUID autorId, String imagenUrl, String titulo, String descripcion) {
        log.info("Iniciando análisis multimodal de IA para publicacionId={}", publicacionId);

        AnalisisTecnicoResultado resultado = estrategia.analizar(imagenUrl, proveedorIa);

        String puntosMejora = resultado.areasMejora() != null ? String.join("\n", resultado.areasMejora()) : "";
        String sugerencias = resultado.consejos() != null ? String.join("\n", resultado.consejos()) : "";
        String composicion = resultado.composicion() != null ? resultado.composicion() : "";
        String diagnostico = resultado.diagnosticoGeneral() != null ? resultado.diagnosticoGeneral() : "Análisis completado";

        AnalisisIaDocument doc = new AnalisisIaDocument(
                UUID.randomUUID(),
                publicacionId,
                autorId,
                "COMPLETADO",
                diagnostico,
                resultado.paletaDetectada(),
                composicion,
                puntosMejora,
                sugerencias,
                resultado.scoreEstimado(),
                proveedorIa.getNombreModelo(),
                resultado.toString()
        );
        mongoRepo.save(doc);

        Map<String, Object> eventoCompletado = Map.of(
                "tipo", "AI_ANALISIS_COMPLETADO",
                "publicacionId", publicacionId.toString(),
                "autorId", autorId.toString(),
                "estado", "COMPLETADO",
                "diagnosticoGeneral", diagnostico,
                "paletaDetectada", resultado.paletaDetectada() != null ? resultado.paletaDetectada() : "",
                "composicion", composicion,
                "puntosMejora", puntosMejora,
                "sugerencias", sugerencias,
                "puntuacionArmonia", resultado.scoreEstimado(),
                "timestamp", Instant.now().toString()
        );

        log.info("Publicando ai.completed para publicacionId={}", publicacionId);
        rabbitTemplate.convertAndSend("artagram.events", "ai.completed", eventoCompletado);
    }
}
