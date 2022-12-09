package pl.adrian.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import pl.adrian.properties.PropertiesManager;

import java.io.IOException;

@Slf4j
public class RabbitMqClient implements AutoCloseable {

    private final Connection connection;
    private Channel channel;

    public RabbitMqClient() {
        var propertiesManager = PropertiesManager.getInstance();

        var connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(propertiesManager.getRabbitMqUsername());
        connectionFactory.setPassword(propertiesManager.getRabbitMqPassword());
        connectionFactory.setVirtualHost(propertiesManager.getRabbitMqVirtualHost());
        connectionFactory.setHost(propertiesManager.getRabbitMqHostname());
        connectionFactory.setPort(propertiesManager.getRabbitMqPort());

        try {
            connection = connectionFactory.newConnection();
            log.info("Successfully connected to RabbitMQ");
        } catch (Exception exception) {
            throw new RabbitMqException("Error occurred while connecting to RabbitMQ", exception);
        }
    }

    public Channel getChannel() {
        if (channel == null) {
            try {
                channel = connection.createChannel();
            } catch (IOException exception) {
                throw new RabbitMqException("Error occurred while creating channel", exception);
            }
        }
        return channel;
    }

    @Override
    public void close() throws Exception {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
        log.info("Successfully disconnected from RabbitMQ");
    }
}
