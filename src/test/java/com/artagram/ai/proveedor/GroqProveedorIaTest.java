package com.artagram.ai.proveedor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.artagram.ai.prompt.PromptIa;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GroqProveedorIaTest {

    private final FakeProveedorIa fake = new FakeProveedorIa();
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = mock(HttpClient.class);

    private final PromptIa prompt = PromptIa.builder()
            .conInstruccion("Analiza la obra")
            .conImagenUrl("https://img/obra.jpg")
            .build();

    @AfterEach
    void limpiarInterrupcion() {
        Thread.interrupted();
    }

    private GroqProveedorIa conClave() {
        return new GroqProveedorIa("clave", "modelo-x", fake, mapper, http);
    }

    @SuppressWarnings("unchecked")
    private void responder(int status, String cuerpo) throws Exception {
        HttpResponse<String> respuesta = mock(HttpResponse.class);
        when(respuesta.statusCode()).thenReturn(status);
        when(respuesta.body()).thenReturn(cuerpo);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(respuesta);
    }

    @Test
    void sinClaveUsaElProveedorFalsoYNoLlamaAGroq() {
        GroqProveedorIa groq = new GroqProveedorIa("", "modelo-x", fake, mapper, http);

        assertThat(groq.completar(prompt)).isEqualTo(fake.completar(prompt));
        assertThat(groq.getNombreModelo()).isEqualTo(fake.getNombreModelo());
        assertThat(groq.soportaVision()).isTrue();
        verifyNoInteractions(http);
    }

    @Test
    void conClaveDevuelveElContenidoDeLaRespuestaDeGroq() throws Exception {
        responder(200, "{\"choices\":[{\"message\":{\"content\":\"{\\\"ok\\\":true}\"}}]}");

        assertThat(conClave().completar(prompt)).isEqualTo("{\"ok\":true}");
        assertThat(conClave().getNombreModelo()).isEqualTo("modelo-x");

        ArgumentCaptor<HttpRequest> request = ArgumentCaptor.forClass(HttpRequest.class);
        verify(http).send(request.capture(), any());
        assertThat(request.getValue().headers().firstValue("Authorization")).contains("Bearer clave");
        assertThat(request.getValue().uri().getHost()).isEqualTo("api.groq.com");
    }

    @Test
    void sinModoJsonNiImagenTambienConsulta() throws Exception {
        responder(200, "{\"choices\":[{\"message\":{\"content\":\"texto\"}}]}");
        PromptIa simple = PromptIa.builder().conInstruccion("Hola").conModoJson(false).build();

        assertThat(conClave().completar(simple)).isEqualTo("texto");
    }

    @Test
    void unErrorHttpUsaElFallback() throws Exception {
        responder(500, "caido");

        assertThat(conClave().completar(prompt)).isEqualTo(fake.completar(prompt));
    }

    @Test
    @SuppressWarnings("unchecked")
    void unaExcepcionDeRedUsaElFallback() throws Exception {
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("sin red"));

        assertThat(conClave().completar(prompt)).isEqualTo(fake.completar(prompt));
    }

    @Test
    @SuppressWarnings("unchecked")
    void siSeInterrumpeRestauraElFlagYUsaElFallback() throws Exception {
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("cancelado"));

        assertThat(conClave().completar(prompt)).isEqualTo(fake.completar(prompt));
        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    @Test
    void elConstructorPublicoCreaSuPropioCliente() {
        assertThat(new GroqProveedorIa(null, "m", fake, mapper).getNombreModelo()).isEqualTo(fake.getNombreModelo());
    }
}
