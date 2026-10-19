package com.artagram.ai.proveedor;

import com.artagram.ai.prompt.PromptIa;
import org.springframework.stereotype.Component;

@Component
public class FakeProveedorIa implements ProveedorIa {

    @Override
    public String getNombreModelo() {
        return "artagram-mock-vision-v1";
    }

    @Override
    public boolean soportaVision() {
        return true;
    }

    @Override
    public String completar(PromptIa prompt) {
        return """
                {
                  "puntosFuertes": [
                    "Excelente equilibrio de valores tonales y composición según la regla de los tercios",
                    "Paleta cromática con armonía análoga coherente y transiciones suaves",
                    "Buen manejo del punto focal capturando la atención del espectador"
                  ],
                  "areasMejora": [
                    "Aumentar el contraste entre luces y sombras en planos medios",
                    "Definición sutil de bordes en los elementos secundarios"
                  ],
                  "consejos": [
                    "Aplica veladuras transparentes para intensificar la profundidad espacial.",
                    "Cuida la consistencia de la dirección de la fuente lumínica principal.",
                    "Experimenta con texturas orgánicas en el fondo para balancear las áreas planas."
                  ],
                  "scoreEstimado": 89,
                  "paletaDetectada": "#2B4C7E, #DCE6F2, #E8A87C, #41B3A3",
                  "composicion": "Armonía dinámica con ritmo visual y foco claro en la sección superior derecha.",
                  "diagnosticoGeneral": "Obra sólida con notable madurez técnica y expresiva."
                }
                """;
    }
}
