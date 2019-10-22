package com.example.mycompass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Arrow arrow; //object for the arrow image

    private ImageView arrowImage; //image of arrow placeholder ImageView

    private SensorManager sensorManager;
    private Sensor accelSens, rotationSens,magnetSens; //all required sensors to use for the compass

    private boolean sensRot = false, sensAccel = false, sensMagnet = false; // booleans to say if sensor is activated

    private int angle = 0; //angle to calculate for compass

    //arrays of floats to store the information about the rotation sensor
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[9];

    private float[] accelerometerReading = new float[3]; //array of floats to calculate the accelerometer's readings to x,y and z directions
    private float[] magnetometerReading = new float[3]; //array of floats to calculate the magnetometer's readings to x,y and z directions

    //says if there is data from accelerometer or magnetometer
    private boolean accelerometerReadingSet = false;
    private boolean magnetometerReadingSet = false;

    private TextView topText, bottomText; //placeholders in the layout for the texts
    private Texts text, value; //objects of texts to be set to the placeholders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allText(); //set texts to the layout
        arrowSettings(); //set arrow objects to the layout
        registerListeners(); //register sensors to be active
    }

    public void arrowSettings(){
        arrowImage = findViewById(R.id.arrow_view);
        arrow = new Arrow(getResources().getIdentifier("arrow","drawable", getPackageName()),arrowImage);
        arrow.showImage();
    }

    public void allText(){
        topText = findViewById(R.id.text);
        bottomText = findViewById(R.id.text_angle);
        text = new Texts(topText);
        value = new Texts(bottomText);
        text.showText("The angle is");
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rotationMatrix,event.values);
            angle = (int) Math.toDegrees(SensorManager.getOrientation(rotationMatrix,orientationAngles)[0]+360)%360;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values,0,magnetometerReading,0,event.values.length);
            magnetometerReadingSet = true;
        } else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values,0,accelerometerReading,0,event.values.length);
            accelerometerReadingSet = true;
        }
        if(magnetometerReadingSet && accelerometerReadingSet){
            SensorManager.getRotationMatrix(rotationMatrix,null,accelerometerReading,magnetometerReading);
            SensorManager.getOrientation(rotationMatrix,orientationAngles);
            angle = (int) ((Math.toDegrees(SensorManager.getOrientation(rotationMatrix,orientationAngles)[0])+360)%360);
        }
        String number = Integer.toString(angle);
        arrow.rotate(-angle);
        value.showText(number);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void registerListeners(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null && sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            accelSens = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            rotationSens = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            magnetSens = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(MainActivity.this, rotationSens, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(MainActivity.this, accelSens, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(MainActivity.this, magnetSens, SensorManager.SENSOR_DELAY_NORMAL);
            sensRot = true;
            sensAccel = true;
            sensMagnet = true;
        } else {
            text.showText("Sensors not found");
        }
    }

    private void unregisterListeners(){
        if (sensRot){
            sensorManager.unregisterListener(MainActivity.this,rotationSens);
            sensRot = false;
        }
        if(sensAccel){
            sensorManager.unregisterListener(MainActivity.this,accelSens);
            sensAccel = false;
        }
        if (sensMagnet){
            sensorManager.unregisterListener(MainActivity.this, magnetSens);
            sensMagnet = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerListeners();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterListeners(); //deactivates sensors to not drain the battery when the app is on pause
    }
}
