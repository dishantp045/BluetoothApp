package edu.temple.bluetoothapp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Beacon beacon;
    private BeaconManager bManager;
    private BluetoothAdapter BTAdapter;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    TextView textview;

    List<Integer> rssiList = new ArrayList<>();

    private final BroadcastReceiver bRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                Log.d("FOUND DEVICES", "GETTING RSSI");
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                rssiList.add(rssi);
                Toast.makeText(getApplicationContext(),""+rssi,Toast.LENGTH_LONG).show();
                Log.i("START RECV", "onReceive:");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .show();
        }
        final ToggleButton scanBtn = findViewById(R.id.toggleButton);
        textview = findViewById(R.id.textView);

        scanBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if(b){
                    registerReceiver(bRecv,filter);
                    BTAdapter.startDiscovery();
                    Log.i("PRESSED BUTTON", "onCheckedChanged: on "+b);
                    int total = getRssiCounts(rssiList);
                    textview.setText(total + " people maybe too close.");
                } else{
                    unregisterReceiver(bRecv);
                    BTAdapter.cancelDiscovery();
                    Log.i("PRESSED BUTTON", "onCheckedChanged: off "+b);
                }
            }
        });


    }

    private int getRssiCounts(List<Integer> list){
        int count = 0;
        Log.i("aaaa", list.size()+"");
        for(int i = 0; i < list.size(); i++){
            int power = ((69-list.get(i))/(10*2));
            double distance = Math.pow(10,power);
            if(distance < 3.0){
                count++;
            }
        }
        return count;
    };

}
