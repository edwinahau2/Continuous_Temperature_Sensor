package com.example.continuoustempsensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class fragment_tab3 extends Fragment {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    TextView mStatusBlueTv, mPairedTv;
    ImageView mBlueIv;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;

    BluetoothAdapter mBlueAdapater;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        super.onCreate(savedInstanceState);
        mStatusBlueTv = getView().findViewById(R.id.statusBluetoothTv);
        mPairedTv = getView().findViewById(R.id.pairedTv);
        mBlueIv = getView().findViewById(R.id.bluetoothIv);
        mOnBtn = getView().findViewById(R.id.onBtn);
        mOffBtn = getView().findViewById(R.id.offBtn);
        mDiscoverBtn = getView().findViewById(R.id.discoverableBtn);
        mPairedBtn = getView().findViewById(R.id.pairedBtn);
        mBlueAdapater = BluetoothAdapter.getDefaultAdapter();
        if (mBlueAdapater == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        }
        else {
            mStatusBlueTv.setText("Bluetooth is available");
        }
        if (mBlueAdapater.isEnabled()) {
            mBlueIv.setImageResource(R.drawable.ic_action_on);
        }
        else {
            mBlueIv.setImageResource(R.drawable.ic_action_off);
        }
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapater.isEnabled()){
                    Toast.makeText(getActivity(),"Turning On Bluetooth...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else {
                    Toast.makeText(getActivity(),"Bluetooth is already on",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mBlueAdapater.isDiscovering()) {
                    Toast.makeText(getActivity(), "Making Your Device Discoverable", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapater.isEnabled()) {
                    mBlueAdapater.disable();
                    Toast.makeText(getActivity(),"Turning Bluetooth Off",Toast.LENGTH_SHORT).show();
                    mBlueIv.setImageResource(R.drawable.ic_action_off);
                }
                else {
                    Toast.makeText(getActivity(),"Bluetooth is already off",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPairedBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mBlueAdapater.isEnabled()){
                    mPairedTv.setText("Paired Devices");
                    Set<BluetoothDevice> devices = mBlueAdapater.getBondedDevices();
                    for (BluetoothDevice device: devices) {
                        mPairedTv.append("\nDevice: " + device.getName() + "," + device);
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Turn on bluetooth to get paired devices",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode){
        case REQUEST_ENABLE_BT:
            if (resultCode == RESULT_OK){
                mBlueIv.setImageResource(R.drawable.ic_action_on);
                Toast.makeText(getActivity(),"Bluetooth is on",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(),"Couldn't turn on Bluetooth",Toast.LENGTH_SHORT).show();
            }
            break;
    }
    super.onActivityResult(requestCode, resultCode, data);
}

}
