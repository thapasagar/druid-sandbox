package test.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import test.storm.AppConfiguration;

/**
 *
 */
public class KafkaMessageProducer
{
    @SuppressWarnings( { "nls", "resource" } )
    public static void main( String [] args )
    {
        System.out.println("Started...");

        Properties producerProperties;
        KafkaProducer<byte[], byte[]> producer;
        producerProperties = new Properties( System.getProperties() );

        producerProperties.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfiguration.KAFKA_SERVERS_CONFIG );

//        producerProperties.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName() );
//        producerProperties.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName() );

        producerProperties.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName() );
        producerProperties.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName() );

        producer = new KafkaProducer<>( producerProperties );

        final String topic = AppConfiguration.KAFKA_SERVERS_TOPIC;
        final String json =
                ""
                + "{"
                + "\"version\":\"2.0\","
                + "\"secret\":\"****\","
                + "\"events\": [{"
                    + "\"type\": \"SYSTEM\","
                    + "\"level\": 3,"
                    + "\"message\": \"CPU under heavy load for 5 minutes\""
                + "},"
                + "{"
                        + "\"type\": \"SYSTEM\","
                        + "\"level\": 5,"
                        + "\"message\": \"RAM usage above 90 per cent utilization\""
                + "}"
                + ","
                + "{"
                        + "\"type\": \"APPLICATION\","
                        + "\"level\": 7,"
                        + "\"message\": \"RAM usage above 90 per cent utilization\""
                + "}"
                + ","
                + "{"
                        + "\"type\": \"SECURITY\","
                        + "\"level\": 10,"
                        + "\"message\": \"Hack attack!\""
                + "}"
                + "]"
            + "}"
            + "";



        ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord<>(
                topic,
                json.getBytes( StandardCharsets.UTF_8 ) );
        producer.send( producerRecord );

        producer.close();

        System.out.println("DONE!");


    }
}
