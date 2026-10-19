# artagram-ai-worker

Worker asíncrono de Inteligencia Artificial y Visión Multimodal para ArtaGram.

## Tecnologías
- Java 21, Spring Boot 3.3.4
- Spring Data MongoDB (base de datos `db_ia`, colecciones `analisis_ia_cache`)
- Spring AMQP (RabbitMQ cola `ai.ai.analyze` -> evento `ai.completed`)
- Cliente HTTP nativo para visión multimodal hacia Groq Cloud API (`qwen/qwen3.8-27b`)
- `FakeProveedorIa` determinista como fallback si no hay API Key configurada

## Variables de Entorno
Ver `.env.example`.
