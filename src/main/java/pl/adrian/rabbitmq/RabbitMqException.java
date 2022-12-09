package pl.adrian.rabbitmq;

public class RabbitMqException extends RuntimeException {

    public RabbitMqException(String message, Throwable cause) {
        super(message, cause);
    }
}
