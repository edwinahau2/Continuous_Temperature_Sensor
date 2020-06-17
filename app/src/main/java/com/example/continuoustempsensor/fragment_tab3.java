package com.example.continuoustempsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class fragment_tab3 extends Fragment {
    private static final int RESULT_OK = -1;
    private Button buttonOn, buttonOff, buttonDisc, buttonFind;
    private BluetoothAdapter mBlueAdapter;
    private TextView mStatusBlueTv, mPairedTv;
    private ImageView mBlueIv;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        mStatusBlueTv = view.findViewById(R.id.statusBluetoothTv);
        mPairedTv = view.findViewById(R.id.pairedTv);
        mBlueIv = view.findViewById(R.id.bluetoothIv);
        buttonOn = view.findViewById(R.id.btnON);
        buttonOff = view.findViewById(R.id.btnOFF);
        buttonDisc = view.findViewById(R.id.discoverableBtn);
        buttonFind = view.findViewById(R.id.pairedBtn);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBlueAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        }
        else {
            mStatusBlueTv.setText("Bluetooth is available");
        }
        buttonOn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!mBlueAdapter.isEnabled()){
                    Toast.makeText(getActivity(), "Turning on Bluetooth...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else {
                    Toast.makeText(getActivity(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()){
                    mBlueAdapter.disable();
                    Toast.makeText(getActivity(), "Turning Bluetooth Off...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Bluetooth is already off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonDisc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
        buttonFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if (mBlueAdapter.isDiscovering()){
                    Toast.makeText(getActivity(), "Canceling discovery...", Toast.LENGTH_SHORT).show();
                    mBlueAdapter.cancelDiscovery();
                }
                else {
                    Toast.makeText(getActivity(), "Finding Devices...", Toast.LENGTH_SHORT).show();
                    mBlueAdapter.startDiscovery();
                }
            }
        });
        return view;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), "Bluetooth is on", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Failed to turn on Bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
