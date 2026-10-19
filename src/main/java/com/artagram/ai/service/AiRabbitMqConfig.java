package com.artagram.ai.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiRabbitMqConfig {

    public static final String EXCHANGE = "artagram.events";
    public static final String DLX_EXCHANGE = "artagram.dlx";
    public static final String QUEUE_AI_ANALYZE = "ai.ai.analyze";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue aiAnalyzeQueue() {
        return QueueBuilder.durable(QUEUE_AI_ANALYZE)
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(QUEUE_AI_ANALYZE + ".dlq")
                .build();
    }

    @Bean
    public Binding aiAnalyzeBinding(Queue aiAnalyzeQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(aiAnalyzeQueue).to(eventsExchange).with("ai.analyze");
    }
}
