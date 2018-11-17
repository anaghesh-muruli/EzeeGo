package anaghesh.ezeego;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Shopping extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SurfaceView cameraPreview;
   TextView amt,item_name, item_price;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    ImageView view_cart;
    Camera camera = Camera.open();
    static public  String beaconStr;
    private TextView scanInfo;
    int quant=1;
    String res;
    String quantstr;
    double total_amt = 0;
    final int RequestCameraPermissionID = 1001;
    int i =0;
    String item[] = new String[100];
    int quants[] = new int[100];
    double price[] = new double[100];
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cameraPreview =  findViewById(R.id.camera_pre);

        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                        setCamFocusMode();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        toolbarSetup();
        cameraPreview =  findViewById(R.id.camera_pre);
        Log.e("error",""+cameraPreview);
        scanInfo = findViewById(R.id.scan_info);
        // setCamFocusMode();
        amt = findViewById(R.id.amt);
        item_name = findViewById(R.id.item_name);
        item_price = findViewById(R.id.item_price);
        view_cart = findViewById(R.id.view_cart);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add(" 1 ");
        categories.add(" 2 ");
        categories.add(" 3 ");
        categories.add(" 4 ");
        categories.add(" 5 ");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        barcodeDetector = new BarcodeDetector.Builder(this)
                //  .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        //Add Event
        //alert();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {


                Camera.Parameters params = camera.getParameters();
                if (params.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                camera.setParameters(params);
                setCamFocusMode();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(Shopping.this,
                            new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                    //  setCamFocusMode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    amt.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.stop( );
                                res = (qrcodes.valueAt(0).displayValue);
                            if(res.equals("12345")) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                                item[i++] = "Gooodday Cookies" ;
                                price[i++] = 20.00 ;
                                quants[i++] = quant ;
                                total_amt = total_amt + quant * 20.00;
                                i++;
                                amt.setText("₹"+total_amt);
                                item_name.setText("Gooodday Cookies");
                                item_price.setText("₹20.00");
                                //vinAlert();

                                restartCamera();
                            }
                            else if(res.equals("12346")) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                                item[i++] = "Parle Lays" ;
                                price[i++] = 10.00 ;

                                quants[i++] = quant ;
                                i++;
                                item_name.setText("Parle Lays");
                                item_price.setText("₹10.00");
                                //vinAlert();

                                restartCamera();
                            }
                            else {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                                item[i++] = "Parle Lays" ;
                                price[i++] = 10.00 ;
                                quants[i++] = quant ;
                                i++;
                                item_name.setText("Parle Lays");
                                item_price.setText("₹10.00");
                                //vinAlert();

                                restartCamera();
                            }
                        }
                    });

                }

            }

        });
    }
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @SuppressLint("MissingPermission")
    public void restartCamera()
    {
        Log.e("method","Restart Camera");
        try {
            cameraSource.start(cameraPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setCamFocusMode(){

        if(null == camera) {
            return;
        }

        /* Set Auto focus */
        Camera.Parameters parameters = camera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else
        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        camera.setParameters(parameters);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        // On selecting a spinner item
         quantstr = parent.getItemAtPosition(i).toString();
         quant = Integer.parseInt(quantstr);
          amt.setText("₹"+total_amt);
        // Showing selected spinner item
       // Toast.makeText(parent.getContext(), "Sesslected: " + quant, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
