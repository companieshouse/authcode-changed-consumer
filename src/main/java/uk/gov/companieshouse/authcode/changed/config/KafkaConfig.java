package uk.gov.companieshouse.authcode.changed.config;

import consumer.deserialization.AvroDeserializer;
import consumer.serialization.AvroSerializer;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import uk.gov.companieshouse.authcode.cancellation.AuthCodeCancellation;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final AvroDeserializer<AuthCodeCancellation> authCodeCancellationDeserializer;
    private final Map<String, Object> producerProps;
    private final Map<String, Object> consumerProps;

    @Autowired
    public KafkaConfig(AvroDeserializer<AuthCodeCancellation> authCodeCancellationDeserializer,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.authCodeCancellationDeserializer = authCodeCancellationDeserializer;

        this.producerProps = Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "${spring.kafka.bootstrap-servers}",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                AvroSerializer.class);

        this.consumerProps = Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "${spring.kafka.bootstrap-servers}",
                ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, AvroDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest", ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false",
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    }

    @Bean
    public KafkaTemplate<String, AuthCodeCancellation> kafkaAuthCodeCancellationTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthCodeCancellation> listenerContainerFactoryAuthCodeCancellation() {
        ConcurrentKafkaListenerContainerFactory<String, AuthCodeCancellation> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), authCodeCancellationDeserializer));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
