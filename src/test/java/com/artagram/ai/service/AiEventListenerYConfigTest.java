package com.artagram.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

class AiEventListenerYConfigTest {

    private final AiWorkerService servicio = mock(AiWorkerService.class);
    private final AiEventListener listener = new AiEventListener(servicio);

    @Test
    void pasaLosDatosDelEventoAlServicio() {
        UUID pub = UUID.randomUUID();
        UUID autor = UUID.randomUUID();

        listener.onAnalyzeRequested(Map.of("publicacionId", pub.toString(), "autorId", autor.toString(),
                "imagenUrl", "u", "titulo", "t", "descripcion", "d"));

        verify(servicio).procesarAnalisis(pub, autor, "u", "t", "d");
    }

    @Test
    void unEventoMalFormadoLanzaExcepcionParaElDlq() {
        Map<String, Object> malo = Map.of("publicacionId", "no-es-uuid");

        assertThatThrownBy(() -> listener.onAnalyzeRequested(malo)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(servicio);
    }

    @Test
    void declaraLaColaDeAnalisisConDeadLetter() {
        AiRabbitMqConfig config = new AiRabbitMqConfig();
        TopicExchange exchange = config.eventsExchange();
        Queue cola = config.aiAnalyzeQueue();
        Binding binding = config.aiAnalyzeBinding(cola, exchange);

        assertThat(exchange.getName()).isEqualTo(AiRabbitMqConfig.EXCHANGE);
        assertThat(cola.getName()).isEqualTo(AiRabbitMqConfig.QUEUE_AI_ANALYZE);
        assertThat(cola.getArguments()).containsEntry("x-dead-letter-exchange", AiRabbitMqConfig.DLX_EXCHANGE);
        assertThat(binding.getRoutingKey()).isEqualTo("ai.analyze");
        assertThat(config.jsonMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
    }
}
