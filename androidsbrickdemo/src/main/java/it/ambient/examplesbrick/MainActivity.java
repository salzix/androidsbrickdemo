package it.ambient.examplesbrick;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import it.ambient.androidsbrick.ConnectionHelper;
import it.ambient.androidsbrick.SBrick;
import it.ambient.androidsbrick.ConnectionCallback;
import it.ambient.androidsbrick.command.RotateCommand;
import it.ambient.androidsbrick.command.StopCommand;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, ConnectionCallback {
    private static final String TAG = "MainActivity";

    String[] requestedPermissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public static final int MULTIPLE_PERMISSIONS = 10; // any number
    private static final int REQUEST_ENABLE_BT = 3;

    private ConnectionHelper connectionHelper;
    private Map<String, SBrick> sbricks;
    private String selectedSBrickId;

    private TextView textStatus;
    private Button buttonStartScan;
    private Button buttonStopScan;
    private ArrayList<Integer> steeringButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        buttonStartScan = findViewById(R.id.buttonStartScan);
        buttonStartScan.setOnClickListener(this);
        buttonStopScan = findViewById(R.id.buttonStopScan);
        buttonStopScan.setOnClickListener(this);
        buttonStopScan.setEnabled(false);

        steeringButtons.add(R.id.buttonDrive_A);
        steeringButtons.add(R.id.buttonDriveBack_A);
        steeringButtons.add(R.id.buttonStop_A);

        steeringButtons.add(R.id.buttonDrive_B);
        steeringButtons.add(R.id.buttonDriveBack_B);
        steeringButtons.add(R.id.buttonStop_B);

        steeringButtons.add(R.id.buttonDrive_C);
        steeringButtons.add(R.id.buttonDriveBack_C);
        steeringButtons.add(R.id.buttonStop_C);

        steeringButtons.add(R.id.buttonDrive_D);
        steeringButtons.add(R.id.buttonDriveBack_D);
        steeringButtons.add(R.id.buttonStop_D);

        for (Integer buttonId : steeringButtons) {
            findViewById(buttonId).setOnClickListener(this);
        }
        setSteeringButtonsEnabled(false);

        boolean arePermissionsGranted = initBluetooth();

        if (!arePermissionsGranted) {
            Log.d(TAG, "requesting permissions");
            requestPermissions(requestedPermissions, MULTIPLE_PERMISSIONS);
        }
        textStatus = findViewById(R.id.textStatus);
    }

    @Override
    protected void onResume() {
    super.onResume();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "Missing required Bluetooth LE support.");
            Toast.makeText(this, "Device is missing required Bluetooth LE support.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Step three - send commands to SBrick
     * @param v View
     */
    @Override
    public void onClick(View v) {
        RotateCommand driveCommand;
        StopCommand stopCommand;
        switch (v.getId()) {
            case R.id.buttonStartScan:
                Log.d(TAG, "onClick - buttonStartScan");
                buttonStartScan.setEnabled(false);
                buttonStopScan.setEnabled(true);
                textStatus.setText("Discovering SBricks...");
                connectionHelper.scanForSBricks();
                break;
            case R.id.buttonStopScan:
                Log.d(TAG, "onClick - buttonStopScan");
                buttonStartScan.setEnabled(true);
                buttonStopScan.setEnabled(false);
                textStatus.setText("Discovery stopped.");
                connectionHelper.stopScan();
                break;
            case R.id.buttonDrive_A:
                Log.d(TAG, "onClick - buttonDrive_A");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelA(RotateCommand.DIR_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonDriveBack_A:
                Log.d(TAG, "onClick - buttonDriveBack_A");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelA(RotateCommand.DIR_COUNTER_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonStop_A:
                Log.d(TAG, "onClick - buttonStop_A");
                if (sbricks.isEmpty()) break;
                stopCommand = new StopCommand();
                stopCommand.channelA();
                sbricks.get(selectedSBrickId).execute(stopCommand);
                break;
            case R.id.buttonDrive_B:
                Log.d(TAG, "onClick - buttonDrive_B");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelB(RotateCommand.DIR_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;

            case R.id.buttonDriveBack_B:
                Log.d(TAG, "onClick - buttonDriveBack_B");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelB(RotateCommand.DIR_COUNTER_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonStop_B:
                Log.d(TAG, "onClick - buttonStop_B");
                if (sbricks.isEmpty()) break;
                stopCommand = new StopCommand();
                stopCommand.channelB();
                sbricks.get(selectedSBrickId).execute(stopCommand);
                break;

            case R.id.buttonDrive_C:
                Log.d(TAG, "onClick - buttonDrive_C");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelC(RotateCommand.DIR_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonDriveBack_C:
                Log.d(TAG, "onClick - buttonDriveBack_C");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelC(RotateCommand.DIR_COUNTER_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonStop_C:
                Log.d(TAG, "onClick - buttonStop_C");
                if (sbricks.isEmpty()) break;
                stopCommand = new StopCommand();
                stopCommand.channelC();
                sbricks.get(selectedSBrickId).execute(stopCommand);
                break;

            case R.id.buttonDrive_D:
                Log.d(TAG, "onClick - buttonDrive_D");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelD(RotateCommand.DIR_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonDriveBack_D:
                Log.d(TAG, "onClick - buttonDriveBack_D");
                if (sbricks.isEmpty()) break;
                driveCommand = new RotateCommand();
                driveCommand.channelD(RotateCommand.DIR_COUNTER_CLOCKWISE, (byte) 0xFF);
                sbricks.get(selectedSBrickId).execute(driveCommand);
                break;
            case R.id.buttonStop_D:
                Log.d(TAG, "onClick - buttonStop_D");
                if (sbricks.isEmpty()) break;
                stopCommand = new StopCommand();
                stopCommand.channelD();
                sbricks.get(selectedSBrickId).execute(stopCommand);
                break;
        }
    }

    /**
     * Step two - add SBricks to collection
     * @param sBrickCollection from ConnectionHelper
     */
    @Override
    public void handleSBrickCollection(Map<String, SBrick> sBrickCollection) {
        Log.d(TAG, "handleSBrickCollection");
        buttonStartScan.setEnabled(true);
        buttonStopScan.setEnabled(false);
        Log.d(TAG, "collection size: " + sBrickCollection.size());
        if (sBrickCollection.isEmpty()) {
            textStatus.setText("SBrick(s) not found.");
            Log.w(TAG, "SBrick(s) not found.");
            setSteeringButtonsEnabled(false);
        } else {
            sbricks = sBrickCollection;
            selectedSBrickId = sbricks.keySet().iterator().next();
            textStatus.setText("Found " + sbricks.size()
                    + " SBrick(s). Using first found: " + selectedSBrickId);
            setSteeringButtonsEnabled(true);
        }
    }

    private boolean initBluetooth() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "permission ACCESS_FINE_LOCATION denied");
            return false;
        }
        Log.d(TAG, "permission ACCESS_FINE_LOCATION granted");
        connectionHelper = new ConnectionHelper(this, this);
        return true;
    }

    /**
     * Enables or disables SBrick command buttons
     * @param state
     */
    private void setSteeringButtonsEnabled(boolean state) {
        for (Integer buttonId : steeringButtons) {
            Button currentButton;
            currentButton = findViewById(buttonId);
            currentButton.setEnabled(state);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (String permission : permissions) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            Log.w(TAG, permission + " PERMISSION_DENIED");
                            Toast.makeText(this, "Location permission is required to find nearby SBricks.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else {
                    Log.d(TAG, "onRequestPermissionsResult() PERMISSIONS_GRANTED");
                    for (Integer buttonId : steeringButtons) {
                        findViewById(buttonId).setOnClickListener(this);
                    }
                    buttonStartScan.setEnabled(true);
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean handlePermissionRequests() {
        Log.d(TAG, "requestBluetoothEnable");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        return false;
    }
}
