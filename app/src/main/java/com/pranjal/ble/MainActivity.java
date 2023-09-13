package com.pranjal.ble;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    int REQUEST_ENABLE_BT = 101;

    private BluetoothLeScanner bluetoothLeScanner;

    ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private boolean mScanning;
    RecyclerView recyclerView;
    Button buttonShowPaired, buttonScan;
    Context context = this;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    ProgressBar progressBar;

    ArrayList<String> scannedDevices = new ArrayList<>();
    BluetoothManager bluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    RVadapter rVadapter;


    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        buttonShowPaired = findViewById(R.id.showPairedDevices);
        buttonScan = findViewById(R.id.buttonScan);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        getpermission();

        bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        rVadapter = new RVadapter(MainActivity.this, deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(rVadapter);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "your device doesn't supporrt bluetooth...", Toast.LENGTH_LONG).show();

        }

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, scannedDevices);
        listView.setAdapter(arrayAdapter);

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
//            scanLeDevice();
        }



//        buttonShowPaired.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Set<BluetoothDevice> devicesSet = mBluetoothAdapter.getBondedDevices();
//
//                if (devicesSet.size() > 0) {
//                    for (BluetoothDevice d : devicesSet) {
//                        deviceList.add(d);
//                    }
//                }
//
//                RVadapter rVadapter = new RVadapter(MainActivity.this, deviceList);
//                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//                recyclerView.setAdapter(rVadapter);
//                rVadapter.notifyDataSetChanged();
//
//
//            }
//        });

//        buttonScan.setOnClickListener(v -> {
//
//        });
//
//        buttonScan.setOnClickListener(this::hello);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanLeDevice(true);
                progressBar.setVisibility(View.VISIBLE);

            }
        });


    }


    // Stops scanning after 10 seconds.

    int SCAN_PERIOD = 10000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            Toast.makeText(this, "Bluetooth turned on", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void hello(View v){

    }


    public void getpermission() {
        ArrayList<String> permissionlist = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionlist.add(android.Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissionlist.add(android.Manifest.permission.BLUETOOTH);
            }

            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionlist.add(android.Manifest.permission.BLUETOOTH_SCAN);
            }

            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissionlist.add(Manifest.permission.BLUETOOTH_ADMIN);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionlist.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionlist.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            String[] ar = new String[permissionlist.size()];
            for (int i = 0; i < permissionlist.size(); i++) {
                ar[i] = permissionlist.get(i);
            }
            if (ar.length != 0) {
                requestPermissions(ar, 101);
            }
        }
    }

    void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.

            System.out.println("mBluetoothAdapter.startLeScan");

            if (arrayAdapter != null) {
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();
            }

            if (!mScanning) {
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if (mScanning) {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (device.getName().equals("Ashwafit") || device.getName().equals("Fitmust") ||device.getName().equals("Bluno")){
                        System.out.println("onLeScanMethod");
//                        if(device.getName() != ""){
//                            scannedDevices.add(device.getName());
//                            arrayAdapter.notifyDataSetChanged();
//                            progressBar.setVisibility(View.INVISIBLE);
//                        }
                        deviceList.add(device);

                        rVadapter.notifyDataSetChanged();


//                        }
                    }catch (Exception e){

                    }

                }
            });
        }
    };
}