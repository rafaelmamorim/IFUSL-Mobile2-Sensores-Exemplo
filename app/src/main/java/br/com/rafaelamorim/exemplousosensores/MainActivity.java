package br.com.rafaelamorim.exemplousosensores;

import static java.lang.String.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor sensorLuminosidade;
    private Sensor sensorProximidade;
    private Sensor sensorRotacao;
    private Sensor sensorUmidade;
    private Sensor sensorTemperatura;
    private Sensor sensorPressao;
    private TextView textViewSensores;

    private float valorLuminosidade;
    private float valorProximidade;
    private float[] valoresRotacao = new float[3];
    private float valorUmidade;
    private float valorTemperatura;
    private float valorPressao;
    private String infoBateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewSensores = findViewById(R.id.SensorTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLuminosidade = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorProximidade = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorRotacao = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorUmidade = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorTemperatura = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorPressao = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        registrarSensores();
        registrarReceiverBateria();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        carregarValoresIniciais();
    }

    private void registrarSensores() {
        if (sensorLuminosidade != null) {
            sensorManager.registerListener(sensorEventListener, sensorLuminosidade, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorProximidade != null) {
            sensorManager.registerListener(sensorEventListener, sensorProximidade, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorRotacao != null) {
            sensorManager.registerListener(sensorEventListener, sensorRotacao, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorUmidade != null) {
            sensorManager.registerListener(sensorEventListener, sensorUmidade, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorTemperatura != null) {
            sensorManager.registerListener(sensorEventListener, sensorTemperatura, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorPressao != null) {
            sensorManager.registerListener(sensorEventListener, sensorPressao, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_LIGHT:
                    valorLuminosidade = event.values[0];
                    break;
                case Sensor.TYPE_PROXIMITY:
                    valorProximidade = event.values[0];
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    float[] rotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                    SensorManager.getOrientation(rotationMatrix, valoresRotacao);
                    for (int i = 0; i < valoresRotacao.length; i++) {
                        valoresRotacao[i] = (float) Math.toDegrees(valoresRotacao[i]);
                    }
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    valorUmidade = event.values[0];
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    valorTemperatura = event.values[0];
                    break;
                case Sensor.TYPE_PRESSURE:
                    valorPressao = event.values[0];
                    break;
            }
            atualizarTextViewSensores();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void registrarReceiverBateria() {
        IntentFilter filtroBateria = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiverBateria, filtroBateria);
    }

    private final BroadcastReceiver receiverBateria = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nivelBateria = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int estadoBateria = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int saudeBateria = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

            String estadoBateriaStr;
            switch (estadoBateria) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    estadoBateriaStr = "Carregando";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    estadoBateriaStr = "Descarregando";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    estadoBateriaStr = "Carregada";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    estadoBateriaStr = "Não está carregando";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                default:
                    estadoBateriaStr = "Desconhecido";
                    break;
            }

            String saudeBateriaStr;
            switch (saudeBateria) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    saudeBateriaStr = "Fria";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    saudeBateriaStr = "Morta";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    saudeBateriaStr = "Boa";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    saudeBateriaStr = "Superaquecida";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    saudeBateriaStr = "Sobretensão";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                default:
                    saudeBateriaStr = "Desconhecido";
                    break;
            }

            infoBateria = format("Bateria: %d%%\nEstado da Bateria: %s\nSaúde da Bateria: %s", nivelBateria, estadoBateriaStr, saudeBateriaStr);

            atualizarTextViewSensores();
        }
    };

    private void carregarValoresIniciais() {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int nivelBateria = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int estadoBateria = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        String estadoBateriaStr = estadoBateria == BatteryManager.BATTERY_STATUS_CHARGING ? "Carregando" : "Descarregando";

        infoBateria = format("Bateria: %d%%\nEstado da Bateria: %s", nivelBateria, estadoBateriaStr);

        atualizarTextViewSensores();
    }

    private void atualizarTextViewSensores() {
        String infoSensores = format(
                "Luminosidade: %.2f lx\n" +
                        "Proximidade: %.2f cm\n" +
                        "Rotação - Azimute: %.2f°, Pitch: %.2f°, Roll: %.2f°\n" +
                        "Umidade: %.2f %%\n" +
                        "Temperatura: %.2f °C\n" +
                        "Pressão: %.2f hPa",
                valorLuminosidade,
                valorProximidade,
                valoresRotacao[0],
                valoresRotacao[1],
                valoresRotacao[2],
                valorUmidade,
                valorTemperatura,
                valorPressao
        );

        textViewSensores.setText(format("%s\n\n%s", infoBateria, infoSensores));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
        unregisterReceiver(receiverBateria);
    }
}
