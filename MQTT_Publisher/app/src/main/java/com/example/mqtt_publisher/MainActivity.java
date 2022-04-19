package com.example.mqtt_publisher;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.SUBSCRIBED;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MQTT mqtt = new MQTT();
    private String report = "";
    private TextView txt, qos0, qos1, qos2, resultado;
    private Button clear, calcular;
    private int c0 = 0, c1 = 0, c2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.txt_status);
        qos0 = findViewById(R.id.txt_qos0);
        qos1 = findViewById(R.id.txt_qos1);
        qos2 = findViewById(R.id.txt_qos2);
        resultado = findViewById(R.id.txt_resultado);
        clear = findViewById(R.id.btn_clear);
        calcular = findViewById(R.id.btn_calcular);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qos0.setText("QTD Mensagens Recebidas QOS 0: ");
                qos1.setText("QTD Mensagens Recebidas QOS 1: ");
                qos2.setText("QTD Mensagens Recebidas QOS 2: ");

                c0 = 0;
                c1 = 0;
                c2 = 0;
            }
        });

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int media0 = 0;
                int media1 = 0;
                int media2 = 0;
                try{
                media0 = 100/c0;
                media1 = 100/c1;
                media2 = 100/c2;

                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Teve uma divisao por 0 hem", Toast.LENGTH_LONG).show();
                }
                resultado.setText("Media QoS0: "+media0
                +"\nMedia QoS: "+media1
                +"\nMedia QoS: "+media2);
                Toast.makeText(getApplicationContext(), "Calulado", Toast.LENGTH_LONG).show();
            }
        });
        init();

    }

    public void init(){
        report = "";
        try {
            mqtt.connectMqtt();
            Log.d("Status Conexao: ","Sucesso!");
            report = "Conectado.";
            txt.setText(report);

            mqtt.sub("QoS0");
            mqtt.sub("QoS1");
            mqtt.sub("QoS2");

            mqtt.getClient().toAsync().publishes(SUBSCRIBED, publish ->{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt.setText("Ultima Mensagem Recebida: "+UTF_8.decode(publish.getPayload().get())+ "QoS: "+publish.getQos().getCode());
                    }
                });
                if(publish.getQos().getCode() == 0){
                    c0 ++;
                    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qos0.setText("QTD Mensagens Recebidas QOS 0: "+c0);
                    }
                });
                }
                if(publish.getQos().getCode() == 1){
                    c1 ++;
                    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qos1.setText("QTD Mensagens Recebidas QOS 1: "+c1);
                    }
                });
                }
                if(publish.getQos().getCode() == 2){
                    c2 ++;
                    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qos2.setText("QTD Mensagens Recebidas QOS 2: "+c2);
                    }
                });
                }
            });
        }catch (Exception e){
            txt.setText("Erro ao conectar: "+e.getMessage());
            Log.d("Erro ao conectar: ", e.getMessage());
        }
    }
}