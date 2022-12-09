package pl.adrian.launchers;

import pl.adrian.consumers.ValidatorConsumer;
import pl.adrian.properties.PropertiesManager;
import pl.adrian.rabbitmq.RabbitMqClient;
import pl.adrian.utils.RabbitMqInitializer;
import pl.adrian.utils.Utils;

public class Validator {
    public static void main(String[] args) throws Exception {
        var propertiesManager = PropertiesManager.getInstance();

        try (var rabbitMqClient = new RabbitMqClient()) {
            var channel = rabbitMqClient.getChannel();

            RabbitMqInitializer.createExchangesAndQueues(channel);

            channel.basicConsume(
                    propertiesManager.getConverterOutputQueue(),
                    true,
                    new ValidatorConsumer(channel)
            );

            Utils.awaitEnterToExit();
        }
    }
}
