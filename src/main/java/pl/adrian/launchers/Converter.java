package pl.adrian.launchers;

import lombok.extern.slf4j.Slf4j;
import pl.adrian.utils.RabbitMqInitializer;
import pl.adrian.consumers.ConverterConsumer;
import pl.adrian.properties.PropertiesManager;
import pl.adrian.rabbitmq.RabbitMqClient;
import pl.adrian.utils.Utils;

@Slf4j
public class Converter {
    public static void main(String[] args) throws Exception {
        var propertiesManager = PropertiesManager.getInstance();

        try (var rabbitMqClient = new RabbitMqClient()) {
            var channel = rabbitMqClient.getChannel();

            RabbitMqInitializer.createExchangesAndQueues(channel);

            channel.basicConsume(
                    propertiesManager.getGeneratorOutputQueue(),
                    true,
                    new ConverterConsumer(channel)
            );

            Utils.awaitEnterToExit();
        }
    }
}
