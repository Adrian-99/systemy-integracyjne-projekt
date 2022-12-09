package pl.adrian.consumers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import pl.adrian.data.GeneratedValue;
import pl.adrian.properties.PropertiesManager;

import java.io.IOException;

@Slf4j
public class ConverterConsumer extends DefaultConsumer {
    private final String outputExchange;
    private final String outputRoutingKey;
    private final ObjectMapper yamlObjectMapper;
    private final ObjectMapper jsonObjectMapper;

    public ConverterConsumer(Channel channel) {
        super(channel);

        var propertiesManager = PropertiesManager.getInstance();
        outputExchange = propertiesManager.getConverterOutputExchange();
        outputRoutingKey = propertiesManager.getConverterOutputRoutingKey();

        yamlObjectMapper = new ObjectMapper(new YAMLFactory());
        jsonObjectMapper = new ObjectMapper(new JsonFactory());
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        var generatedValue = yamlObjectMapper.readValue(body, GeneratedValue.class);
        var jsonBytes = jsonObjectMapper.writeValueAsBytes(generatedValue);
        getChannel().basicPublish(
                outputExchange,
                outputRoutingKey,
                null,
                jsonBytes
        );
        log.info("Converted value {}", generatedValue.value());
    }
}
