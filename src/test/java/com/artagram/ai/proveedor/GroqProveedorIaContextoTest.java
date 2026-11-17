package com.artagram.ai.proveedor;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** Con dos constructores, Spring debe saber cuál usar al crear el bean. */
class GroqProveedorIaContextoTest {

    @Test
    void springCreaElBeanConElConstructorPublico() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.registerBean(ObjectMapper.class, () -> new ObjectMapper());
            ctx.register(FakeProveedorIa.class, GroqProveedorIa.class);
            ctx.refresh();
            assertThat(ctx.getBean(GroqProveedorIa.class).getNombreModelo())
                    .isEqualTo(ctx.getBean(FakeProveedorIa.class).getNombreModelo());
        }
    }
}
