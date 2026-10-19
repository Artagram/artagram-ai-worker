package com.artagram.ai.proveedor;

import com.artagram.ai.prompt.PromptIa;

public interface ProveedorIa {
    String getNombreModelo();
    String completar(PromptIa prompt);
    boolean soportaVision();
}
