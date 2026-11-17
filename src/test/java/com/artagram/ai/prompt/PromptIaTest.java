package com.artagram.ai.prompt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PromptIaTest {

    @Test
    void usaValoresPorDefectoYPermiteSobrescribirlos() {
        PromptIa base = PromptIa.builder().conInstruccion("Describe").build();
        assertThat(base.getSystemPrompt()).contains("crítico");
        assertThat(base.getMaxTokens()).isEqualTo(400);
        assertThat(base.getTemperatura()).isEqualTo(0.3);
        assertThat(base.isModoJson()).isTrue();
        assertThat(base.getImagenDataUriOUrl()).isNull();

        PromptIa propio = PromptIa.builder()
                .conSystemPrompt("sys")
                .conInstruccion("inst")
                .conImagenUrl("https://img")
                .conMaxTokens(10)
                .conTemperatura(0.9)
                .conModoJson(false)
                .build();
        assertThat(propio.getSystemPrompt()).isEqualTo("sys");
        assertThat(propio.getInstruccionUsuario()).isEqualTo("inst");
        assertThat(propio.getImagenDataUriOUrl()).isEqualTo("https://img");
        assertThat(propio.getMaxTokens()).isEqualTo(10);
        assertThat(propio.getTemperatura()).isEqualTo(0.9);
        assertThat(propio.isModoJson()).isFalse();
    }

    @Test
    void conImagenBytesArmaUnDataUri() {
        PromptIa png = PromptIa.builder().conInstruccion("x").conImagenBytes(new byte[]{1, 2}, "image/png").build();
        PromptIa sinMime = PromptIa.builder().conInstruccion("x").conImagenBytes(new byte[]{1, 2}, " ").build();
        PromptIa vacio = PromptIa.builder().conInstruccion("x").conImagenBytes(new byte[0], "image/png").build();
        PromptIa nulo = PromptIa.builder().conInstruccion("x").conImagenBytes(null, null).build();

        assertThat(png.getImagenDataUriOUrl()).isEqualTo("data:image/png;base64,AQI=");
        assertThat(sinMime.getImagenDataUriOUrl()).startsWith("data:image/jpeg;base64,");
        assertThat(vacio.getImagenDataUriOUrl()).isNull();
        assertThat(nulo.getImagenDataUriOUrl()).isNull();
    }

    @Test
    void sinInstruccionNoSePuedeConstruir() {
        assertThatThrownBy(() -> PromptIa.builder().build()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> PromptIa.builder().conInstruccion(null).build())
                .isInstanceOf(IllegalStateException.class);
    }
}
