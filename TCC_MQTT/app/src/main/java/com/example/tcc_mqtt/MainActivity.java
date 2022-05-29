package com.example.tcc_mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView status, sent0, sent1, sent2 , received0, received1, received2, txtResult;
    private Button test0, test1, test2, btnResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}