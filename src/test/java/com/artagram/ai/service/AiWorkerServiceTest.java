package com.artagram.ai.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artagram.ai.domain.AnalisisIaDocument;
import com.artagram.ai.domain.AnalisisIaMongoRepository;
import com.artagram.ai.estrategia.AnalisisTecnicoEstrategia;
import com.artagram.ai.estrategia.AnalisisTecnicoResultado;
import com.artagram.ai.proveedor.ProveedorIa;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class AiWorkerServiceTest {

    @Mock
    private AnalisisTecnicoEstrategia estrategia;
    @Mock
    private ProveedorIa proveedorIa;
    @Mock
    private AnalisisIaMongoRepository mongoRepo;
    @Mock
    private RabbitTemplate rabbitTemplate;

    private AiWorkerService service;

    @BeforeEach
    void setUp() {
        service = new AiWorkerService(estrategia, proveedorIa, mongoRepo, rabbitTemplate);
    }

    @Test
    void procesarAnalisisExitosamente() {
        UUID pubId = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();

        when(proveedorIa.getNombreModelo()).thenReturn("mock-model");
        when(estrategia.analizar(any(), eq(proveedorIa))).thenReturn(new AnalisisTecnicoResultado(
                List.of("Fuerte trazo"), List.of("Más contraste"), List.of("Aplicar veladuras"),
                90, "#fff, #000", "Regla de tercios", "Excelente pieza"
        ));

        service.procesarAnalisis(pubId, autorId, "http://img/test.jpg", "Mi Obra", "Descripción");

        verify(mongoRepo).save(any(AnalisisIaDocument.class));
        verify(rabbitTemplate).convertAndSend(eq("artagram.events"), eq("ai.completed"), any(Object.class));
    }
}
