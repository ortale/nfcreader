package com.joseortale.ortalesoft.ips;

import static android.nfc.NdefRecord.createMime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.joseortale.ortalesoft.ips.api.ApiService;
import com.joseortale.ortalesoft.ips.api.RetrofitClient;
import com.joseortale.ortalesoft.ips.helpers.Utils;
import com.joseortale.ortalesoft.ips.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private ImageView ivContactless;
    private ImageView ivResult;
    private ProgressBar progressBar;

    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private ApiService client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivContactless = findViewById(R.id.iv_contactless);
        ivResult = findViewById(R.id.iv_result);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            initNfcAdapter();

            client = RetrofitClient.getClient();

            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            IntentFilter[] readTagFilters = new IntentFilter[]{tagDetected};
            adapter.enableForegroundDispatch(this, pendingIntent, readTagFilters, null);
        } else if (adapter != null && !adapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        } else {
            Toast.makeText(getApplicationContext(), "This device does not support NFC", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            String id = Utils.byteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));

            initLoading();
            client.validateStudent(id).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse != null) {
                        switch (apiResponse.getData().toString()) {
                            case "green":
                                ivResult.setImageDrawable(getResources().getDrawable(R.drawable.img_green, null));
                                break;
                            case "amber":
                                ivResult.setImageDrawable(getResources().getDrawable(R.drawable.img_amber, null));
                                break;
                            case "red":
                                ivResult.setImageDrawable(getResources().getDrawable(R.drawable.img_red, null));
                                break;
                        }

                        finishLoading();
                    } else {
                        Toast.makeText(MainActivity.this, "Some error has occurred", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Some error has occurred", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initLoading() {
        ivResult.setVisibility(View.GONE);
        ivContactless.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
        ivResult.setVisibility(View.VISIBLE);
        ivContactless.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void initNfcAdapter() {
        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        adapter = nfcManager.getDefaultAdapter();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        Intent intent = new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }
}