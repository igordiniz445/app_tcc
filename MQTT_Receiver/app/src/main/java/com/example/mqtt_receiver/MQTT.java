package com.example.mqtt_receiver;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class MQTT {

    private Mqtt5BlockingClient client;
    final String host = "64ecdba8287a4f8984bbece7f2bc79b7.s1.eu.hivemq.cloud";
    final String username = "tcc_publisher";
    final String password = "TccPublisher4";

    public MQTT(){
        this.client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildBlocking();
    }

    public Mqtt5BlockingClient getClient(){
        return this.client;
    }

    public void connectMqtt(){

            client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                    .send();
    }

    public void sendMessage(MqttQos qos,String topic, String payload){
        this.client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(payload))
                .qos(qos)
                .send();
    }



}