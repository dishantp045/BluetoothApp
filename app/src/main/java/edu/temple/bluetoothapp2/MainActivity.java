package edu.temple.bluetoothapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.altbeacon.beacon.Beacon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Beacon beacon;
    }
}
