package pl.adrian.properties;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class PropertiesManager {
    private static final String RABBIT_MQ_USERNAME = "rabbitMq.username";
    private static final String RABBIT_MQ_PASSWORD = "rabbitMq.password";
    private static final String RABBIT_MQ_VIRTUAL_HOST = "rabbitMq.virtualHost";
    private static final String RABBIT_MQ_HOSTNAME = "rabbitMq.hostname";
    private static final String RABBIT_MQ_PORT = "rabbitMq.port";
    private static final String GENERATOR_OUTPUT_DELAY = "generator.output.delay";
    private static final String GENERATOR_OUTPUT_DATA_LENGTH = "generator.output.data.length";
    private static final String GENERATOR_OUTPUT_EXCHANGE = "generator.output.exchange";
    private static final String GENERATOR_OUTPUT_ROUTING_KEY = "generator.output.routingKey";
    private static final String GENERATOR_OUTPUT_QUEUE = "generator.output.queue";
    private static final String CONVERTER_OUTPUT_EXCHANGE = "converter.output.exchange";
    private static final String CONVERTER_OUTPUT_ROUTING_KEY = "converter.output.routingKey";
    private static final String CONVERTER_OUTPUT_QUEUE = "converter.output.queue";
    private static final String VALIDATOR_DATA_MODULO_BASE = "validator.data.moduloBase";
    private static final String VALIDATOR_OUTPUT_EXCHANGE = "validator.output.exchange";
    private static final String VALIDATOR_OUTPUT_VALID_ROUTING_KEY = "validator.output.valid.routingKey";
    private static final String VALIDATOR_OUTPUT_VALID_QUEUE = "validator.output.valid.queue";
    private static final String VALIDATOR_OUTPUT_INVALID_ROUTING_KEY = "validator.output.invalid.routingKey";
    private static final String VALIDATOR_OUTPUT_INVALID_QUEUE = "validator.output.invalid.queue";

    private static PropertiesManager instance;

    private final Properties properties;

    public static PropertiesManager getInstance() {
        if (instance == null) {
            instance = new PropertiesManager();
        }
        return instance;
    }

    private PropertiesManager() {
        properties = new Properties();
        try {
            var propertiesPath = System.getenv("PROPERTIES_PATH");
            if (propertiesPath != null) {
                properties.load(getClass().getResourceAsStream(propertiesPath));
                log.info("Successfully loaded properties from {}", propertiesPath);
            } else {
                throw new IllegalArgumentException("Missing PROPERTIES_PATH environment variable");
            }
        } catch (Exception exception) {
            throw new PropertiesException("Error occurred while loading properties", exception);
        }
    }

    public String getRabbitMqUsername() {
        return getRequiredProperty(RABBIT_MQ_USERNAME);
    }

    public String getRabbitMqPassword() {
        return getRequiredProperty(RABBIT_MQ_PASSWORD);
    }

    public String getRabbitMqVirtualHost() {
        return getRequiredProperty(RABBIT_MQ_VIRTUAL_HOST);
    }

    public String getRabbitMqHostname() {
        return getRequiredProperty(RABBIT_MQ_HOSTNAME);
    }

    public Integer getRabbitMqPort() {
        return Integer.parseInt(getRequiredProperty(RABBIT_MQ_PORT));
    }

    public Integer getGeneratorOutputDelay() {
        return Integer.parseInt(getRequiredProperty(GENERATOR_OUTPUT_DELAY));
    }

    public Integer getGeneratorOutputDataLength() {
        return Integer.parseInt(getRequiredProperty(GENERATOR_OUTPUT_DATA_LENGTH));
    }

    public String getGeneratorOutputExchange() {
        return getRequiredProperty(GENERATOR_OUTPUT_EXCHANGE);
    }

    public String getGeneratorOutputRoutingKey() {
        return getRequiredProperty(GENERATOR_OUTPUT_ROUTING_KEY);
    }

    public String getGeneratorOutputQueue() {
        return getRequiredProperty(GENERATOR_OUTPUT_QUEUE);
    }

    public String getConverterOutputExchange() {
        return getRequiredProperty(CONVERTER_OUTPUT_EXCHANGE);
    }

    public String getConverterOutputRoutingKey() {
        return getRequiredProperty(CONVERTER_OUTPUT_ROUTING_KEY);
    }

    public String getConverterOutputQueue() {
        return getRequiredProperty(CONVERTER_OUTPUT_QUEUE);
    }

    public Integer getValidatorDataModuloBase() {
        return Integer.parseInt(getRequiredProperty(VALIDATOR_DATA_MODULO_BASE));
    }

    public String getValidatorOutputExchange() {
        return getRequiredProperty(VALIDATOR_OUTPUT_EXCHANGE);
    }

    public String getValidatorOutputValidRoutingKey() {
        return getRequiredProperty(VALIDATOR_OUTPUT_VALID_ROUTING_KEY);
    }

    public String getValidatorOutputValidQueue() {
        return getRequiredProperty(VALIDATOR_OUTPUT_VALID_QUEUE);
    }

    public String getValidatorOutputInvalidRoutingKey() {
        return getRequiredProperty(VALIDATOR_OUTPUT_INVALID_ROUTING_KEY);
    }

    public String getValidatorOutputInvalidQueue() {
        return getRequiredProperty(VALIDATOR_OUTPUT_INVALID_QUEUE);
    }

    private String getRequiredProperty(String key) {
        var value = properties.getProperty(key);
        if (value != null) {
            return value;
        } else {
            throw new PropertiesException("Property \"" + key + "\" not found");
        }
    }
}
