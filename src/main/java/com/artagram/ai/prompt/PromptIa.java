package com.artagram.ai.prompt;

import java.util.Base64;

public class PromptIa {

    private final String systemPrompt;
    private final String instruccionUsuario;
    private final String imagenDataUriOUrl;
    private final int maxTokens;
    private final double temperatura;
    private final boolean modoJson;

    PromptIa(String systemPrompt, String instruccionUsuario, String imagenDataUriOUrl,
             int maxTokens, double temperatura, boolean modoJson) {
        this.systemPrompt = systemPrompt;
        this.instruccionUsuario = instruccionUsuario;
        this.imagenDataUriOUrl = imagenDataUriOUrl;
        this.maxTokens = maxTokens;
        this.temperatura = temperatura;
        this.modoJson = modoJson;
    }

    public static PromptIaBuilder builder() {
        return new PromptIaBuilder();
    }

    public String getSystemPrompt() { return systemPrompt; }
    public String getInstruccionUsuario() { return instruccionUsuario; }
    public String getImagenDataUriOUrl() { return imagenDataUriOUrl; }
    public int getMaxTokens() { return maxTokens; }
    public double getTemperatura() { return temperatura; }
    public boolean isModoJson() { return modoJson; }

    public static class PromptIaBuilder {
        private String systemPrompt = "Eres un crítico y pedagogo de arte profesional en Artagram.";
        private String instruccionUsuario = "";
        private String imagenDataUriOUrl = null;
        private int maxTokens = 400;
        private double temperatura = 0.3;
        private boolean modoJson = true;

        public PromptIaBuilder conSystemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public PromptIaBuilder conInstruccion(String instruccion) {
            this.instruccionUsuario = instruccion;
            return this;
        }

        public PromptIaBuilder conImagenUrl(String url) {
            this.imagenDataUriOUrl = url;
            return this;
        }

        public PromptIaBuilder conImagenBytes(byte[] bytes, String mimeType) {
            if (bytes != null && bytes.length > 0) {
                String b64 = Base64.getEncoder().encodeToString(bytes);
                String mime = (mimeType != null && !mimeType.isBlank()) ? mimeType : "image/jpeg";
                this.imagenDataUriOUrl = "data:" + mime + ";base64," + b64;
            }
            return this;
        }

        public PromptIaBuilder conMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public PromptIaBuilder conTemperatura(double temperatura) {
            this.temperatura = temperatura;
            return this;
        }

        public PromptIaBuilder conModoJson(boolean modoJson) {
            this.modoJson = modoJson;
            return this;
        }

        public PromptIa build() {
            if (instruccionUsuario == null || instruccionUsuario.isBlank()) {
                throw new IllegalStateException("La instrucción de usuario para el prompt no puede estar vacía");
            }
            return new PromptIa(systemPrompt, instruccionUsuario, imagenDataUriOUrl, maxTokens, temperatura, modoJson);
        }
    }
}
