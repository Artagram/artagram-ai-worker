package com.artagram.ai.estrategia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.artagram.ai.prompt.PromptIa;
import com.artagram.ai.proveedor.FakeProveedorIa;
import com.artagram.ai.proveedor.ProveedorIa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class AnalisisTecnicoEstrategiaTest {

    private final AnalisisTecnicoEstrategia estrategia = new AnalisisTecnicoEstrategia(new ObjectMapper());

    @Test
    void interpretaElJsonDelProveedor() {
        ProveedorIa proveedor = Mockito.spy(new FakeProveedorIa());

        AnalisisTecnicoResultado resultado = estrategia.analizar("https://img/o.jpg", proveedor);

        assertThat(resultado.scoreEstimado()).isEqualTo(89);
        assertThat(resultado.puntosFuertes()).hasSize(3);
        assertThat(resultado.consejos()).hasSize(3);
        ArgumentCaptor<PromptIa> prompt = ArgumentCaptor.forClass(PromptIa.class);
        Mockito.verify(proveedor).completar(prompt.capture());
        assertThat(prompt.getValue().getImagenDataUriOUrl()).isEqualTo("https://img/o.jpg");
        assertThat(prompt.getValue().isModoJson()).isTrue();
    }

    @Test
    void siLaRespuestaNoEsJsonDevuelveUnAnalisisPorDefecto() {
        ProveedorIa proveedor = mock(ProveedorIa.class);
        when(proveedor.completar(any())).thenReturn("esto no es json");

        AnalisisTecnicoResultado resultado = estrategia.analizar("https://img/o.jpg", proveedor);

        assertThat(resultado.scoreEstimado()).isEqualTo(85);
        assertThat(resultado.diagnosticoGeneral()).isNotBlank();
    }
}
