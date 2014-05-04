package com.rcatolino.scream;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

import java.lang.Math;

public class Scream extends Activity
{
  private SoundPool sp;
  private int soundStartId;
  private int soundStopId;
  private int streamId;

  private class AccelerometerListener implements SensorEventListener {
    private TextView txtV;
    private double min;
    private boolean freeFall;
    public AccelerometerListener(TextView view) {
      this.txtV = view;
      this.min = 10;
      this.freeFall = false;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
      double norm = Math.sqrt(event.values[0] * event.values[0] +
                              event.values[1] * event.values[1] +
                              event.values[2] * event.values[2]);
      min = (norm < min) ? norm : min;
      if (norm < 1.0 && !freeFall) {
        freeFall = true;
        startPlaying();
      } else if (norm >= 1.0 && freeFall) {
        freeFall = false;
        stopPlaying();
      }

      txtV.setText(event.timestamp + ": " + norm + ", min: " + min);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    soundStartId = sp.load(this, R.raw.arnoldini, 1);
    soundStopId = sp.load(this, R.raw.arnoldend, 2);

    SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    mSensorManager.registerListener(new AccelerometerListener((TextView) findViewById(R.id.txt)),
                                    mSensor, SensorManager.SENSOR_DELAY_GAME);
  }

  private void startPlaying() {
    // soundId, left volume, right volume, priority, loop, rate.
    streamId = sp.play(soundStartId, 1.0f, 1.0f, 1, 0, 1);
  }

  private void stopPlaying() {
    int streamStop = sp.play(soundStopId, 1.0f, 1.0f, 2, 0, 1);
    sp.stop(streamId);
  }
}
