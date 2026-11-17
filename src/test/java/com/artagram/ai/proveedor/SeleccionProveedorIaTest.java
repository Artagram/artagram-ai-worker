package com.artagram.ai.proveedor;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/** Escenario 9: el proveedor activo sale de la configuracion, no del codigo. */
class SeleccionProveedorIaTest {

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withBean(ObjectMapper.class)
            .withUserConfiguration(GroqProveedorIa.class, FakeProveedorIa.class);

    @Test
    void porDefectoSeUsaGroq() {
        contexto.run(ctx -> assertThat(ctx.getBean(ProveedorIa.class)).isInstanceOf(GroqProveedorIa.class));
    }

    @Test
    void conIaProveedorFakeSeUsaElSimuladoSinTocarOtrosServicios() {
        contexto.withPropertyValues("artagram.ia.proveedor=fake").run(ctx -> {
            assertThat(ctx).doesNotHaveBean(GroqProveedorIa.class);
            assertThat(ctx.getBean(ProveedorIa.class)).isInstanceOf(FakeProveedorIa.class);
        });
    }
}
