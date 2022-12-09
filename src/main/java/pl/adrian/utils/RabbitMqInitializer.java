package pl.adrian.utils;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.adrian.properties.PropertiesManager;

import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitMqInitializer {
    public static void createExchangesAndQueues(Channel channel) throws IOException {
        var propertiesManager = PropertiesManager.getInstance();

        createExchange(channel, propertiesManager.getGeneratorOutputExchange(), BuiltinExchangeType.FANOUT);
        createQueue(channel, propertiesManager.getGeneratorOutputQueue());
        bindQueueToExchange(
                channel,
                propertiesManager.getGeneratorOutputQueue(),
                propertiesManager.getGeneratorOutputExchange(),
                propertiesManager.getGeneratorOutputRoutingKey()
        );

        createExchange(channel, propertiesManager.getConverterOutputExchange(), BuiltinExchangeType.FANOUT);
        createQueue(channel, propertiesManager.getConverterOutputQueue());
        bindQueueToExchange(
                channel,
                propertiesManager.getConverterOutputQueue(),
                propertiesManager.getConverterOutputExchange(),
                propertiesManager.getConverterOutputRoutingKey()
        );

        createExchange(channel, propertiesManager.getValidatorOutputExchange(), BuiltinExchangeType.DIRECT);
        createQueue(channel, propertiesManager.getValidatorOutputValidQueue());
        bindQueueToExchange(
                channel,
                propertiesManager.getValidatorOutputValidQueue(),
                propertiesManager.getValidatorOutputExchange(),
                propertiesManager.getValidatorOutputValidRoutingKey()
        );
        createQueue(channel, propertiesManager.getValidatorOutputInvalidQueue());
        bindQueueToExchange(
                channel,
                propertiesManager.getValidatorOutputInvalidQueue(),
                propertiesManager.getValidatorOutputExchange(),
                propertiesManager.getValidatorOutputInvalidRoutingKey()
        );
    }

    private static void createExchange(Channel channel, String name, BuiltinExchangeType type) throws IOException {
        channel.exchangeDeclare(name, type, false, false, null);
        log.info("Created exchange '{}'", name);
    }

    private static void createQueue(Channel channel, String name) throws IOException {
        channel.queueDeclare(name, false, false, false, null);
        log.info("Created queue '{}'", name);
    }

    private static void bindQueueToExchange(Channel channel, String queue, String exchange, String routingKey)
            throws IOException {
        channel.queueBind(queue, exchange, routingKey);
        log.info("Bound queue '{}' to exchange '{}' with routing key '{}'", queue, exchange, routingKey);
    }
}
