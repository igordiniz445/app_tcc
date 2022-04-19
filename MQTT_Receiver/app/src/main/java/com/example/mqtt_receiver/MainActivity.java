package com.example.mqtt_receiver;

import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3ConnAckException;


public class MainActivity extends AppCompatActivity {

    private MQTT mqtt = new MQTT();
    private Button btn;
    private TextView txt, qos0, qos1, qos2;
    private String report = "";
    private final int QTD_MSG_TESTE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn_iniciar);
        txt = findViewById(R.id.txt_status);
        qos0 = findViewById(R.id.txt_qos0);
        qos1 = findViewById(R.id.txt_qos1);
        qos2 = findViewById(R.id.txt_qos2);
        init();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report += "\nEnviando Mensagens...";
                report += "\nTestando com QOS 0";
                txt.setText(report);
                for(int i=1;i<=QTD_MSG_TESTE;i++){
                    qos0.setText("QTD Mensagens enviadas QOS 0:"+i);
                    mqtt.getClient().publishWith()
                            .topic("QoS0")
                            .payload(UTF_8.encode("-71.7826, 119.2334"))
                            .qos(MqttQos.AT_MOST_ONCE)
                            .send();
                }

                report += "\nTestando com QOS 1";
                txt.setText(report);
                for(int i=1;i<=QTD_MSG_TESTE;i++){
                    qos1.setText("QTD Mensagens enviadas QOS 1:"+i);
                    mqtt.getClient().publishWith()
                            .topic("QoS1")
                            .payload(UTF_8.encode("-71.7826, 119.2334"))
                            .qos(MqttQos.AT_LEAST_ONCE)
                            .send();
                }

                report += "\nTestando com QOS 0";
                txt.setText(report);
                for(int i=1;i<=QTD_MSG_TESTE;i++){
                    qos2.setText("QTD Mensagens enviadas QOS 2:"+i);
                    mqtt.getClient().publishWith()
                            .topic("QoS2")
                            .payload(UTF_8.encode("-71.7826, 119.2334"))
                            .qos(MqttQos.EXACTLY_ONCE)
                            .send();
                }
            }
        });
    }

    public void init() {
        report = "";
        try {
            mqtt.connectMqtt();
            Log.d("Status Conexao: ","Sucesso!");
            report = "Conectado.";
            txt.setText(report);
        }catch (Exception e){
            txt.setText("Erro ao conectar: "+e.getMessage());
            Log.d("Erro ao conectar: ", e.getMessage());
        }
    }


}