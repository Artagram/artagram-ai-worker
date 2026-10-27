package com.artagram.ai.service;

import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AiEventListener {

    private static final Logger log = LoggerFactory.getLogger(AiEventListener.class);

    private final AiWorkerService aiWorkerService;

    public AiEventListener(AiWorkerService aiWorkerService) {
        this.aiWorkerService = aiWorkerService;
    }

    @RabbitListener(queues = AiRabbitMqConfig.QUEUE_AI_ANALYZE)
    public void onAnalyzeRequested(Map<String, Object> payload) {
        try {
            UUID publicacionId = UUID.fromString((String) payload.get("publicacionId"));
            UUID autorId = UUID.fromString((String) payload.get("autorId"));
            String imagenUrl = (String) payload.get("imagenUrl");
            String titulo = (String) payload.get("titulo");
            String descripcion = (String) payload.get("descripcion");

            log.info("Evento ai.analyze recibido para publicacionId={}", publicacionId);
            aiWorkerService.procesarAnalisis(publicacionId, autorId, imagenUrl, titulo, descripcion);
        } catch (Exception ex) {
            log.error("Error al procesar solicitud ai.analyze: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Fallo al procesar ai.analyze", ex);
        }
    }
}
