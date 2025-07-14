package org.beaconfire.composite.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbitMQ;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbitMQ
public class RabbitMQConfig {

    public static final String ONBOARDING_QUEUE = "onboarding.queue";
    public static final String ONBOARDING_EXCHANGE = "onboarding.exchange";
    public static final String ONBOARDING_ROUTING_KEY = "onboarding.submit";

    @Bean
    public Queue onboardingQueue() {
        return new Queue(ONBOARDING_QUEUE, true);
    }

    @Bean
    public TopicExchange onboardingExchange() {
        return new TopicExchange(ONBOARDING_EXCHANGE);
    }

    @Bean
    public Binding onboardingBinding() {
        return BindingBuilder
                .bind(onboardingQueue())
                .to(onboardingExchange())
                .with(ONBOARDING_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
