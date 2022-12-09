package pl.adrian.consumers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import pl.adrian.data.GeneratedValue;
import pl.adrian.data.ValidatedValue;
import pl.adrian.properties.PropertiesManager;

import java.io.IOException;

@Slf4j
public class ValidatorConsumer extends DefaultConsumer {
    private final String exchange;
    private final String validRoutingKey;
    private final String invalidRoutingKey;
    private final Integer moduloBase;
    private final Integer moduloDivider;
    private final ObjectMapper objectMapper;

    public ValidatorConsumer(Channel channel) {
        super(channel);

        var propertiesManager = PropertiesManager.getInstance();
        exchange = propertiesManager.getValidatorOutputExchange();
        validRoutingKey = propertiesManager.getValidatorOutputValidRoutingKey();
        invalidRoutingKey = propertiesManager.getValidatorOutputInvalidRoutingKey();
        moduloBase = propertiesManager.getValidatorDataModuloBase();

        if (moduloBase < 2) {
            throw new IllegalArgumentException("Modulo base cannot be lower than 2");
        }

        var moduloDividerValue = 10;
        while (moduloDividerValue < moduloBase) {
            moduloDividerValue *= 10;
        }
        moduloDivider = moduloDividerValue;

        objectMapper = new ObjectMapper(new JsonFactory());
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        var generatedValue = objectMapper.readValue(body, GeneratedValue.class);

        var informationPart = generatedValue.value() / moduloDivider;
        var moduloPart = generatedValue.value() % moduloDivider;
        log.info(
                "Validating value {} - info part: {} - modulo part: {} - testing modulo: {}",
                generatedValue.value(),
                informationPart,
                moduloPart,
                moduloBase
        );

        var validatedValue = new ValidatedValue(
                generatedValue.value(),
                moduloBase,
                informationPart % moduloBase == moduloPart
        );

        getChannel().basicPublish(
                exchange,
                validatedValue.isValid() ? validRoutingKey : invalidRoutingKey,
                null,
                objectMapper.writeValueAsBytes(validatedValue)
        );
        log.info("Validated {} - result: {}", generatedValue.value(), validatedValue.isValid() ? "valid" : "invalid");
    }
}
