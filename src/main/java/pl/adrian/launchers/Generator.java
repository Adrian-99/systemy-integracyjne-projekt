package pl.adrian.launchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import pl.adrian.utils.RabbitMqInitializer;
import pl.adrian.data.GeneratedValue;
import pl.adrian.properties.PropertiesException;
import pl.adrian.properties.PropertiesManager;
import pl.adrian.rabbitmq.RabbitMqClient;
import pl.adrian.utils.Utils;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Generator {
    private static Integer outputDelay;
    private static Integer outputDataLength;
    private static String outputExchange;
    private static String outputRoutingKey;
    private static Long maxValue;
    private static Channel channel;
    private static Random random;
    private static ObjectMapper objectMapper;

    public static void main(String[] args) throws Exception {
        var propertiesManager = PropertiesManager.getInstance();
        outputDelay = propertiesManager.getGeneratorOutputDelay();
        outputDataLength = propertiesManager.getGeneratorOutputDataLength();
        outputExchange = propertiesManager.getGeneratorOutputExchange();
        outputRoutingKey = propertiesManager.getGeneratorOutputRoutingKey();
        validateProperties();

        calculateMaxValue();

        random = new Random();
        objectMapper = new ObjectMapper(
                new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        );

        try (var rabbitMqClient = new RabbitMqClient()) {
            channel = rabbitMqClient.getChannel();

            RabbitMqInitializer.createExchangesAndQueues(channel);

            var executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(
                    Generator::generateNewNumber,
                    0,
                    outputDelay,
                    TimeUnit.MILLISECONDS
            );

            Utils.awaitEnterToExit();

            executor.shutdown();
        }
    }

    private static void validateProperties() {
        if (outputDelay < 1) {
            throw new PropertiesException("Generator data delay cannot be lower than 1");
        }
        if (outputDataLength < 1) {
            throw new PropertiesException("Generator data length cannot be lower than 1");
        }
        if (outputDataLength > 18) {
            throw new PropertiesException("Generator data length cannot be higher than 18");
        }
    }

    private static void calculateMaxValue() {
        maxValue = 10L;
        for (int i = 1; i < outputDataLength; i++) {
            maxValue *= 10;
        }
    }

    private static void generateNewNumber() {
        var generatedValue = new GeneratedValue(random.nextLong(maxValue));
        try {
            var bytes = objectMapper.writeValueAsBytes(generatedValue);
            channel.basicPublish(outputExchange, outputRoutingKey, null, bytes);
            log.info("Sent generated value {}", generatedValue.value());
        } catch (Exception e) {
            log.error("Error while generating value: {}", e.getMessage());
        }
    }
}
