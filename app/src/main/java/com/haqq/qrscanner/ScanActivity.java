package com.haqq.qrscanner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ProgressDialog pd;
    private ZXingScannerView mScannerView;

    private MediaPlayer mp, mp2;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        pd = new ProgressDialog(ScanActivity.this);
        pd.setMessage("Verifying Please Wait......");
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = rawResult.getText();
        MainActivity.data = rawResult.getText();
            GetQR(result);
    }

    private void GetQR(String resultt) {
        pd.show();
        JSONObject request = new JSONObject();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, "http://devfest2019.kano.gdg.ng/api/qr.php?dataa=" + resultt, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pd.hide();
                        try {
                            String msg = response.getString("message");

                            if (response.getInt("tstatus") == 1){
                                mp = MediaPlayer.create(ScanActivity.this, R.raw.gun);
                                mp.start();
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mp.stop();
                                    }
                                }, 3 * 1000);
                            }else if(response.getInt("tstatus") == 2){

                                mp = MediaPlayer.create(ScanActivity.this, R.raw.danger);
                                mp.start();
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mp.stop();
                                    }
                                }, 3 * 1000);
                            }else{
                                mp = MediaPlayer.create(ScanActivity.this, R.raw.danger);
                                mp.start();
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mp.stop();
                                    }
                                }, 3 * 1000);
                            }
                            showAlertDialog(msg);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.hide();

                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }




    private void showAlertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(msg);
        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                onBackPressed();
            }
        });

        builder.show();
    }

}