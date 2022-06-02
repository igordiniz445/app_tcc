package com.example.tcc_mqtt;

import static android.content.ContentValues.TAG;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.SUBSCRIBED;
import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;


import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView status, sent0, sent1, sent2 , received0, received1, received2, txtResult;
    private Button test0, test1, test2, btnResult, reset;
    private int countReceived0 = 0, countReceived1 = 0, countReceived2 = 0;
    private String topicQoS0 = "tcc/igor/qos0", topicQoS1 = "tcc/igor/qos1", topicQoS2 = "tcc/igor/qos2";
    private int QTD_MSG_TESTE = 10;
    private Mqtt5BlockingClient client;
    private int countSent0 = 0, countSent1 = 0, countSent2 = 0;

    private String[] msgSentQoS0 = new String[QTD_MSG_TESTE];
    private String[] msgSentQoS1 = new String[QTD_MSG_TESTE];
    private String[] msgSentQoS2 = new String[QTD_MSG_TESTE];

    private String[] msgReceivedQoS0 = new String[QTD_MSG_TESTE];
    private String[] msgReceivedQoS1 = new String[QTD_MSG_TESTE];
    private String[] msgReceivedQoS2 = new String[QTD_MSG_TESTE];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        test0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish(topicQoS0, MqttQos.AT_MOST_ONCE);

            }
        });

        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish(topicQoS1, MqttQos.AT_LEAST_ONCE);
            }
        });

        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish(topicQoS2, MqttQos.EXACTLY_ONCE);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countSent0 = 0;
                countSent1 = 0;
                countSent2 = 0;

                countReceived0 = 0;
                countReceived1 = 0;
                countReceived2 = 0;

                msgSentQoS0 = new String[QTD_MSG_TESTE];
                msgSentQoS1 = new String[QTD_MSG_TESTE];
                msgSentQoS2 = new String[QTD_MSG_TESTE];
                msgReceivedQoS0 = new String[QTD_MSG_TESTE];
                msgReceivedQoS1 = new String[QTD_MSG_TESTE];
                msgReceivedQoS2 = new String[QTD_MSG_TESTE];

            }
        });

        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularResultados(0);
                calcularResultados(1);
                calcularResultados(2);
            }
        });
    }

    private void calcularResultados(int qos) {
        String[] sent;
        String[] received;
        if(qos == 0){
            sent = msgSentQoS0;
            received = msgReceivedQoS0;
        }else if(qos == 1){
            sent = msgSentQoS1;
            received = msgReceivedQoS1;
        }else{
            sent = msgSentQoS2;
            received = msgReceivedQoS2;
        }
        boolean isEmpty = false;
        ArrayList<Long> timeDifferences = new ArrayList<>();
        for (String element:sent) {
            if(element==null){
                Log.wtf("Elemento nullo",""+element);
                isEmpty = true;
            }
                break;
        }
        if(isEmpty) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        try {
            for(int i =0; i<QTD_MSG_TESTE;i++){
//            System.out.println("sent["+i+"]: "+msgSentQoS0[i]);
//            System.out.println("received["+i+"]: "+msgReceivedQoS0[i]);
                String rawDateSent = sent[i].split("-")[1];
                String rawDateReceived = received[i];

                LocalDateTime dateSent = LocalDateTime.parse(rawDateSent, dtf);
                LocalDateTime dateReceived = LocalDateTime.parse(rawDateReceived, dtf);

                Duration duration = Duration.between(dateReceived, dateSent);
                long diff = Math.abs(duration.toMillis());
                Log.wtf("Diferenca : ", ""+diff);

                timeDifferences.add(diff);

            }
        }catch (Exception e){
            Log.wtf("Exception no loop: ",""+e.getMessage());
        }

        Long media = 0l;
        for(Long diff :timeDifferences){
            media += diff;
        }

        if(!timeDifferences.isEmpty()){
            media = media/timeDifferences.size();
            Log.wtf("MEDIA QOS "+qos+": ", ""+media);

        }
    }

    private void publish(String topic, MqttQos qos) {

        for(int i=0;i<QTD_MSG_TESTE;i++){
            final int contador = i+1;
            // ID do payload - data do Payload
            String payload = i+"-"+generateDateString();
            try {
                final byte[] encodedPayload =  payload.getBytes("UTF-8");
                try {
//                    Log.wtf("MSG Enviada: ", payload);
                    client.publishWith()
                            .topic(topic)
                            .payload(encodedPayload)
                            .qos(qos)
                            .send();

                    if(qos.getCode()==0){
                        System.out.println("Payload: "+payload);
                        this.msgSentQoS0[i]= payload;
                        System.out.println("this.msgSentQoS0[i]: "+this.msgSentQoS0[i]);
                    }if(qos.getCode() == 1){
                        this.msgSentQoS1[i] = payload;
                    }if(qos.getCode() == 2){
                        this.msgSentQoS2[i] = payload;
                    }

                    runOnUiThread( () -> {
                        if(topic.equals(topicQoS0)){
                            sent0.setText("Mensagens enviadas : "+contador);
                        }if(topic.equals(topicQoS1)){
                            sent1.setText("Mensagens enviadas : "+contador);
                        }if(topic.equals(topicQoS2)){
                            sent2.setText("Mensagens enviadas : "+contador);
                        }
                    });
                } catch (Exception e) {
                    Log.wtf("ERRO: ", e.getMessage());
                }

            } catch (UnsupportedEncodingException e) {
                Log.wtf("Erro: ", e.getMessage());
            }

        }


    }

    private String generateDateString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private void init() {
        status = findViewById(R.id.txtStatus);
        sent0 = findViewById(R.id.txtMsgSentQoS0);
        sent1 = findViewById(R.id.txtMsgSentQoS1);
        sent2 = findViewById(R.id.txtMsgSentQoS2);
        received0 = findViewById(R.id.txtMsgReceivedQoS0);
        received1 = findViewById(R.id.txtMsgReceivedQoS1);
        received2 = findViewById(R.id.txtMsgReceivedQoS2);
        txtResult = findViewById(R.id.result);

        test0 = findViewById(R.id.btnTestQoS0);
        test1 = findViewById(R.id.btnTestQoS1);
        test2 = findViewById(R.id.btnTestQoS2);
        btnResult = findViewById(R.id.btnCalculateResult);
        reset = findViewById(R.id.btnReset);


        client = com.hivemq.client.mqtt.MqttClient.builder()
                .useMqttVersion5()
                .serverHost("0bd316fb91f74c498bb7721d6d84e328.s2.eu.hivemq.cloud")
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildBlocking();
        try {

            client.connectWith()
                    .simpleAuth()
                    .username("tcc_igor_diniz")
                    .password(UTF_8.encode("Qx3Q_ZnEC8cpe!8"))
                    .applySimpleAuth()
                    .send();

            // We are connected
            status.setText("Status da conexao: CONECTADO");
            subcribe();


        } catch (Exception e) {
            status.setText("Status da conexao: FALHA");
            Log.wtf("Falha ao conecatar: ", e);
        }
    }

    private void subcribe(){
        try {
            this.client.subscribeWith()
                    .topicFilter(topicQoS0)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send();
            this.client.subscribeWith()
                    .topicFilter(topicQoS1)
                    .qos(MqttQos.AT_MOST_ONCE)
                    .send();
            this.client.subscribeWith()
                    .topicFilter(topicQoS2)
                    .qos(MqttQos.EXACTLY_ONCE)
                    .send();
            client.toAsync().publishes(SUBSCRIBED, message -> {

                String receivedMessageDate = generateDateString();
                String payload = ""+UTF_8.decode(message.getPayload().get());
                String[] parts = payload.split("-");
                try {
                    int key = Integer.parseInt(parts[0]);
                    String data = parts[1];
                    if(message.getQos().getCode()==0){
                        if(msgReceivedQoS0[key] != null ){
                            Log.wtf("MSG REPETIDA: ","KEY: "+key+" MESSAGE : "+data);
                        }
                        msgReceivedQoS0[key] = receivedMessageDate;
                    }
                    if(message.getQos().getCode()==1){
                        if(msgReceivedQoS1[key] != null ){
                            Log.wtf("MSG REPETIDA: ","KEY: "+key+" MESSAGE : "+data);
                        }
                        msgReceivedQoS1[key] = receivedMessageDate;
                    }
                    if(message.getQos().getCode()==2){
                        if(msgReceivedQoS2[key] != null ){
                            Log.wtf("MSG REPETIDA: ","KEY: "+key+" MESSAGE : "+data);
                        }
                        msgReceivedQoS2[key] = receivedMessageDate;
                    }

                }catch (Exception e){
                    Log.wtf("Erro ao dividir payload: ", e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(message.getTopic().toString().equals(topicQoS0)){
                            countReceived0++;
                            received0.setText("Mensagens recebidas : "+countReceived0+" MSG: "+payload );
                        }if(message.getTopic().toString().equals(topicQoS1)){
                            countReceived1++;
                            received1.setText("Mensagens recebidas : "+countReceived1+" MSG: "+ payload);
                        }if(message.getTopic().toString().equals(topicQoS2)){
                            countReceived2++;
                            received2.setText("Mensagens recebidas : "+countReceived2+" MSG: "+ payload);
                        }
                    }
                });

            });

        } catch (Exception e) {
            Log.wtf("Erro ao receber msg: ", e.getMessage());
        }
    }



}