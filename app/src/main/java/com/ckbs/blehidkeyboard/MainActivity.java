package com.ckbs.blehidkeyboard;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;



public class MainActivity extends AppCompatActivity {



    static final byte ID_KEYBOARD = 1;
    /* see https://www.usb.org/sites/default/files/documents/hid1_11.pdf */

    // 4 buttons, 1 X/Y stick
    //
    //   7 6 5 4 3 2 1 0
    // [ - - - - 4 3 2 1 ] - buttons
    // [  X axis         ]
    // [  Y axis         ]

    private static final byte[] descriptor = new byte[]{

            // HID descriptor
            0x09, // bLength
            0x21, // bDescriptorType - HID
            0x11, 0x01, // bcdHID (little endian - 1.11)
            0x00, // bCountryCode
            0x01, // bNumDescriptors (min 1)
            0x22, // bDescriptorType - Report
            0x30, 0x00, // wDescriptorLength (48)

            // Report descriptor
           /* 0x05, 0x01,        // USAGE_PAGE (Generic Desktop)
            0x09, 0x05,        // USAGE (Game Pad)
            (byte) 0xa1, 0x01, // COLLECTION (Application)
            (byte) 0xa1, 0x00, //   COLLECTION (Physical)
            0x05, 0x09,        //     USAGE_PAGE (Button)
            0x19, 0x01,        //     USAGE_MINIMUM (Button 1)
            0x29, 0x04,        //     USAGE_MAXIMUM (Button 4)
            0x15, 0x00,        //     LOGICAL_MINIMUM (0)
            0x25, 0x01,        //     LOGICAL_MAXIMUM (1)
            0x75, 0x01,        //     REPORT_SIZE (1)
            (byte) 0x95, 0x04, //     REPORT_COUNT (4)
            (byte) 0x81, 0x02, //     INPUT (Data,Var,Abs)
            0x75, 0x04,        //     REPORT_SIZE (4)
            (byte) 0x95, 0x01, //     REPORT_COUNT (1)
            (byte) 0x81, 0x03, //     INPUT (Cnst,Var,Abs)
            0x05, 0x01,        //     USAGE_PAGE (Generic Desktop)
            0x09, 0x30,        //     USAGE (X)
            0x09, 0x31,        //     USAGE (Y)
            0x15, (byte) 0x81, //     LOGICAL_MINIMUM (-127)
            0x25, 0x7f,        //     LOGICAL_MAXIMUM (127)
            0x75, 0x08,        //     REPORT_SIZE (8)
            (byte) 0x95, 0x02, //     REPORT_COUNT (2)
            (byte) 0x81, 0x02, //     INPUT (Data,Var,Abs)
            (byte) 0xc0,       //   END_COLLECTION
            (byte) 0xc0        // END_COLLECTION*/
/*
            0x05, 0x01,                    // USAGE_PAGE (Generic Desktop)
            0x09, 0x02,                    // USAGE (Mouse)
            (byte) 0xa1, 0x01,                    // COLLECTION (Application)
            (byte) 0x85, 0x03,                    //   Report ID (3)
            0x09, 0x01,                    //   USAGE (Pointer)
            (byte) 0xa1, 0x00,                    //   COLLECTION (Physical)
            0x05, 0x09,                    //     USAGE_PAGE (Button)
            0x19, 0x01,                    //     USAGE_MINIMUM (Button 1)
            0x29, 0x03,                    //     USAGE_MAXIMUM (Button 3)
            0x15, 0x00,                    //     LOGICAL_MINIMUM (0)
            0x25, 0x01,                    //     LOGICAL_MAXIMUM (1)
            (byte) 0x95, 0x03,                    //     REPORT_COUNT (3)
            0x75, 0x01,                    //     REPORT_SIZE (1)
            (byte) 0x81, 0x02,                    //     INPUT (Data,Var,Abs)
            (byte) 0x95, 0x01,                    //     REPORT_COUNT (1)
            0x75, 0x05,                    //     REPORT_SIZE (5)
            (byte) 0x81, 0x03,                    //     INPUT (Cnst,Var,Abs)
            0x05, 0x01,                    //     USAGE_PAGE (Generic Desktop)
            0x09, 0x30,                    //     USAGE (X)
            0x09, 0x31,                    //     USAGE (Y)
            0x15, (byte) 0x81,                    //     LOGICAL_MINIMUM (-127)
            0x25, 0x7f,                    //     LOGICAL_MAXIMUM (127)
            0x75, 0x08,                    //     REPORT_SIZE (8)
            (byte) 0x95, 0x02,                    //     REPORT_COUNT (2)
            (byte) 0x81, 0x06,                    //     INPUT (Data,Var,Rel)
            (byte) 0xc0,                          //   END_COLLECTION
            (byte) 0xc0,                          // END_COLLECTION

 */
            /*0x05, 0x01, // USAGE_PAGE (Generic Desktop)
            0x09, 0x06, // USAGE (Keyboard)
            (byte) 0xa1, 0x01, // COLLECTION (Application)
            0x05, 0x07, //   USAGE_PAGE (Keyboard)(Key Codes)
            0x19, (byte)0xe0, //   USAGE_MINIMUM (Keyboard LeftControl)(224)
            0x29, (byte)0xe7, //   USAGE_MAXIMUM (Keyboard Right GUI)(231)
            0x15, 0x00, //   LOGICAL_MINIMUM (0)
            0x25, 0x01, //   LOGICAL_MAXIMUM (1)
            0x75, 0x01, //   REPORT_SIZE (1)
            (byte)0x95, 0x08, //   REPORT_COUNT (8)
            (byte)0x81, 0x02, //   INPUT (Data,Var,Abs) ; Modifier byte
            (byte)0x95, 0x01, //   REPORT_COUNT (1)
            0x75, 0x08, //   REPORT_SIZE (8)
            (byte)0x81, 0x03, //   INPUT (Cnst,Var,Abs) ; Reserved byte
            (byte)0x95, 0x05, //   REPORT_COUNT (5)
            0x75, 0x01, //   REPORT_SIZE (1)
            0x05, 0x08, //   USAGE_PAGE (LEDs)
            0x19, 0x01, //   USAGE_MINIMUM (Num Lock)
            0x29, 0x05, //   USAGE_MAXIMUM (Kana)
            (byte)0x91, 0x02, //   OUTPUT (Data,Var,Abs) ; LED report
            (byte)0x95, 0x01, //   REPORT_COUNT (1)
            0x75, 0x03, //   REPORT_SIZE (3)
            (byte)0x91, 0x03, //   OUTPUT (Cnst,Var,Abs) ; LED report padding
            (byte)0x95, 0x06, //   REPORT_COUNT (6)
            0x75, 0x08, //   REPORT_SIZE (8)
            0x15, 0x00, //   LOGICAL_MINIMUM (0)
            0x25, 0x65, //   LOGICAL_MAXIMUM (101)
            0x05, 0x07, //   USAGE_PAGE (Keyboard)(Key Codes)
            0x19, 0x00, //   USAGE_MINIMUM (Reserved (no event indicated))(0)
            0x29, 0x65, //   USAGE_MAXIMUM (Keyboard Application)(101)
            (byte)0x81, 0x00, //   INPUT (Data,Ary,Abs)
            (byte)0xc0 // END_COLLECTION*/

            (byte) 0x05, (byte) 0x01, // Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x06, // Usage (Keyboard)
            (byte) 0xA1, (byte) 0x01, // Collection (Application)
            (byte) 0x85, ID_KEYBOARD, //    Report ID
            (byte) 0x05, (byte) 0x07, //       Usage page (Key Codes)
            (byte) 0x19, (byte) 0xE0, //       Usage minimum (224)
            (byte) 0x29, (byte) 0xE7, //       Usage maximum (231)
            (byte) 0x15, (byte) 0x00, //       Logical minimum (0)
            (byte) 0x25, (byte) 0x01, //       Logical maximum (1)
            (byte) 0x75, (byte) 0x01, //       Report size (1)
            (byte) 0x95, (byte) 0x08, //       Report count (8)
            (byte) 0x81, (byte) 0x02, //       Input (Data, Variable, Absolute) ; Modifier byte
            (byte) 0x75, (byte) 0x08, //       Report size (8)
            (byte) 0x95, (byte) 0x01, //       Report count (1)
            (byte) 0x81, (byte) 0x01, //       Input (Constant)                 ; Reserved byte
            (byte) 0x75, (byte) 0x08, //       Report size (8)
            (byte) 0x95, (byte) 0x06, //       Report count (6)
            (byte) 0x15, (byte) 0x00, //       Logical Minimum (0)
            (byte) 0x25, (byte) 0x65, //       Logical Maximum (101)
            (byte) 0x05, (byte) 0x07, //       Usage page (Key Codes)
            (byte) 0x19, (byte) 0x00, //       Usage Minimum (0)
            (byte) 0x29, (byte) 0x65, //       Usage Maximum (101)
            (byte) 0x81, (byte) 0x00, //       Input (Data, Array)              ; Key array (6 keys)
            (byte) 0xC0,              // End Collection

    };

    private static final int REQUEST_ENABLE_BT = 99;

    private static final String TAG = "MainActivity";

    private BluetoothHidDevice mBtHidDevice;
    private final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mBtDevice;
    private BluetoothHidDeviceAppQosSettings mBluetoothHidDeviceAppQosSettings;

    private Vibrator vibrator;

    private void getProxy() {
        mBtAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            @SuppressLint("NewApi")
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HID_DEVICE) {
                    Log.d(TAG, "Got HID device");
                    mBtHidDevice = (BluetoothHidDevice) proxy;


                    mBluetoothHidDeviceAppQosSettings = new BluetoothHidDeviceAppQosSettings(1, 800, 9, 0, 11250, -1);

                    BluetoothHidDeviceAppSdpSettings sdp = new BluetoothHidDeviceAppSdpSettings(
                            "BleHidMouse",
                            "Android BLE HID Keyboard",
                            "Android",
                            (byte) 0x00,
                            descriptor
                    );


                    mBtHidDevice.registerApp(sdp,null , mBluetoothHidDeviceAppQosSettings, Executors.newSingleThreadExecutor(), new BluetoothHidDevice.Callback() {


                        @Override
                        public void onGetReport(BluetoothDevice device, byte type, byte id, int bufferSize) {
                            Log.v(TAG, "onGetReport: device=" + device + " type=" + type
                                    + " id=" + id + " bufferSize=" + bufferSize);
                        }


                        @Override
                        public void onConnectionStateChanged(BluetoothDevice device, final int state) {
                            Log.v(TAG, "onConnectionStateChanged: device=" + device + " state=" + state);
                            if (device.equals(mBtDevice)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView status = findViewById(R.id.status);
                                        if (state == BluetoothProfile.STATE_DISCONNECTED) {
                                            status.setText(R.string.status_disconnected);
                                            mBtDevice = null;
                                        } else if (state == BluetoothProfile.STATE_CONNECTING) {
                                            status.setText(R.string.status_connecting);
                                        } else if (state == BluetoothProfile.STATE_CONNECTED) {
                                            status.setText(R.string.status_connected);
                                        } else if (state == BluetoothProfile.STATE_DISCONNECTING) {
                                            status.setText(R.string.status_disconnecting);
                                        }
                                    }
                                });
                                //btConnect(device);

                            }
                        }
                    });

                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HID_DEVICE) {
                    Log.d(TAG, "Lost HID device");
                }
            }
        }, BluetoothProfile.HID_DEVICE);
    }

    private Button[] buttons;


    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = getSystemService(Vibrator.class);

        // initialise the keyboard


        // get buttons ready
        buttons = new Button[]{
                findViewById(R.id.button_1),
                findViewById(R.id.button_2),
                findViewById(R.id.button_3),
                findViewById(R.id.button_4),
                findViewById(R.id.button_5),
                findViewById(R.id.button_6),
                findViewById(R.id.button_7),
                findViewById(R.id.button_8),
                findViewById(R.id.button_9),
                findViewById(R.id.button_10),
                findViewById(R.id.button_11),
                findViewById(R.id.button_12),
                findViewById(R.id.button_13),
                findViewById(R.id.button_14),
                findViewById(R.id.button_15),
                findViewById(R.id.button_16),
                findViewById(R.id.button_17),
                findViewById(R.id.button_18),
                findViewById(R.id.button_19),
                findViewById(R.id.button_20),
                findViewById(R.id.button_21),
                findViewById(R.id.button_22),
                findViewById(R.id.button_23),
                findViewById(R.id.button_24),
                findViewById(R.id.button_25),
                findViewById(R.id.button_26),
                findViewById(R.id.button_27),
                findViewById(R.id.button_28),
                findViewById(R.id.button_29),
                findViewById(R.id.button_30),
                findViewById(R.id.button_31),
                findViewById(R.id.button_32),
                findViewById(R.id.button_33),
                findViewById(R.id.button_34),
                findViewById(R.id.button_35),
                findViewById(R.id.button_36),
                findViewById(R.id.button_37),
                findViewById(R.id.button_38),
                findViewById(R.id.button_39),
                findViewById(R.id.button_40),
                findViewById(R.id.button_41),
                findViewById(R.id.button_42),
                findViewById(R.id.button_43),
                findViewById(R.id.button_44),
                findViewById(R.id.button_45),
                findViewById(R.id.button_46),
                findViewById(R.id.button_47),
                findViewById(R.id.button_48),
                findViewById(R.id.button_49),
                findViewById(R.id.button_50),
                findViewById(R.id.button_51),
                findViewById(R.id.button_52),
                findViewById(R.id.button_53),
                findViewById(R.id.button_54),

        };
        for (Button button : buttons) {
            button.setTag(R.id.tag_pressed, false);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Do something
                            if (!(boolean) v.getTag(R.id.tag_pressed)) {
                                vibrator.vibrate(VibrationEffect.createOneShot(40, 50));
                            }
                            v.setTag(R.id.tag_pressed, true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            // No longer down
                            v.setTag(R.id.tag_pressed, false);
                            break;
                        default:
                            return false;
                    }
                    sendReport();
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get bluetooth enabled before continuing
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            btListDevices();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mBtHidDevice != null) {
            btConnect(null); // disconnect
            Spinner btList = findViewById(R.id.devices);
            btList.setSelection(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                btListDevices();
            } else {
                final MainActivity activity = this;
                // TODO handle if the user doesn't like bluetooth
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth Required")
                        .setMessage("Bluetooth is required to run this app. Try again?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finishAndRemoveTask(); // exit
                            }
                        })
                        .show();
            }
        }
    }

    private final ArrayList<BluetoothDevice> mDevices = new ArrayList<>();

    private void btConnect(BluetoothDevice device) {
        Log.i(TAG, "btConnect: device=" + device);

        // disconnect from everything else


        for (BluetoothDevice btDev : mBtHidDevice.getDevicesMatchingConnectionStates(new int[]{
                //BluetoothProfile.STATE_CONNECTING,
                BluetoothProfile.STATE_CONNECTED,
        })) {
            mBtHidDevice.disconnect(btDev);
            Log.i(TAG, "btConnect: disconnect" );
        }

      /*  if(mBtHidDevice.getDevicesMatchingConnectionStates(new int[]{
                BluetoothProfile.STATE_CONNECTING,
                BluetoothProfile.STATE_CONNECTED,
                BluetoothProfile.STATE_DISCONNECTING
        }).isEmpty()
                && device != null) {
            mBtHidDevice.connect(device);
            Log.i(TAG, "btConnect: connect" );
        }*/

        if (device != null) {
            mBtDevice = device;
            mBtHidDevice.connect(device);
        }


    }

    private void btListDevices() {
        getProxy(); // need bluetooth to have been enabled first

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add devices to adapter
        List<String> names = new ArrayList<>();

        // add empty
        names.add("(disconnected)");
        mDevices.add(null);

        for (BluetoothDevice btDev : pairedDevices) {
            names.add(btDev.getName());
            mDevices.add(btDev);
        }

        Spinner btList = findViewById(R.id.devices);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btList.setAdapter(adapter);

        btList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                BluetoothDevice dev = mDevices.get(position);
                Log.d(TAG, "onItemSelected(): " + dev + " " + position + " " + id);
                btConnect(dev);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO handle this
            }
        });
    }

   // private final byte[] keyboardData = "M0ABCDEF".getBytes();

    /*byte[] setValue(int modifier, int key1, int key2, int key3, int key4, int key5, int key6) {
        keyboardData[0] = (byte) modifier;
        keyboardData[1] = 0;
        keyboardData[2] = (byte) key1;
        keyboardData[3] = (byte) key2;
        keyboardData[4] = (byte) key3;
        keyboardData[5] = (byte) key4;
        keyboardData[6] = (byte) key5;
        keyboardData[7] = (byte) key6;
        return keyboardData;
    }*/

    private void sendReport() {

        byte state = 0;
        byte modify = 0;
        if((boolean)buttons[51].getTag(R.id.tag_pressed)){
            modify |=(1<<0);
        }
        if((boolean)buttons[52].getTag(R.id.tag_pressed)){
            modify |=(1<<1);
        }
        if((boolean)buttons[53].getTag(R.id.tag_pressed)){
            modify |=(1<<2);
        }
        for (int i = 0; i < 51; ++i) {
            if ((boolean) buttons[i].getTag(R.id.tag_pressed)) {
                switch (i){
                    case 0:
                        state = (byte) 0x14;
                        break;
                    case 1:
                        state = 0x1a;
                        break;
                    case 2:
                        state = 0x08;
                        break;
                    case 3:
                        state = 0x15;
                        break;
                    case 4:
                        state = 0x17;
                        break;
                    case 5:
                        state = 0x1c;
                        break;
                    case 6:
                        state = 0x18;
                        break;
                    case 7:
                        state = 0x0c;
                        break;
                    case 8:
                        state = 0x12;
                        break;
                    case 9:
                        state = 0x13;
                        break;
                    case 10:
                        state = 0x2f;
                        break;
                    case 11:
                        state = 0x30;
                        break;
                    case 12:
                        state = 0x04;
                        break;
                    case 13:
                        state = 0x16;
                        break;
                    case 14:
                        state = 0x07;
                        break;
                    case 15:
                        state = 0x09;
                        break;
                    case 16:
                        state = 0x0a;
                        break;
                    case 17:
                        state = 0x0b;
                        break;
                    case 18:
                        state = 0x0d;
                        break;
                    case 19:
                        state = 0x0e;
                        break;
                    case 20:
                        state = 0x0f;
                        break;
                    case 21:
                        state = 0x33;
                        break;
                    case 22:
                        state = 0x34;
                        break;
                    case 23:
                        state = 0x1d;
                        break;
                    case 24:
                        state = 0x1b;
                        break;
                    case 25:
                        state = 0x06;
                        break;
                    case 26:
                        state = 0x19;
                        break;
                    case 27:
                        state = 0x05;
                        break;
                    case 28:
                        state = 0x11;
                        break;
                    case 29:
                        state = 0x10;
                        break;
                    case 30:
                        state = 0x36;
                        break;
                    case 31:
                        state = 0x37;
                        break;
                    case 32:
                        state = 0x38;
                        break;
                    case 33:
                        state = 0x28;
                        break;
                    case 34:
                        state = 0x2a;
                        break;
                    case 35:
                        state = (byte) 0x90;
                        break;
                    case 36:
                        state = 0x1e;
                        break;
                    case 37:
                        state = 0x1f;
                        break;
                    case 38:
                        state = 0x20;
                        break;
                    case 39:
                        state = 0x21;
                        break;
                    case 40:
                        state = 0x22;
                        break;
                    case 41:
                        state = 0x23;
                        break;
                    case 42:
                        state = 0x24;
                        break;
                    case 43:
                        state = 0x25;
                        break;
                    case 44:
                        state = 0x26;
                        break;
                    case 45:
                        state = 0x27;
                        break;
                    case 46:
                        state = 0x2d;
                        break;
                    case 47:
                        state = 0x2e;
                        break;
                    case 48:
                        state = 0x31;
                        break;
                    case 49:
                        state = 0x2c;
                        break;
                    case 50:
                        state = 0x2b;
                        break;

                }
            }
        }

        // get kyboard state

        Log.d(TAG, "sendReport(): " + state +modify);
        for (BluetoothDevice btDev : mBtHidDevice.getConnectedDevices()) {
            mBtHidDevice.sendReport(btDev, 1, new byte[]{
                    modify,
                    0,
                    0,
                    0,
                    0,
                    0,
                    state,


            });
        }
    }
}
