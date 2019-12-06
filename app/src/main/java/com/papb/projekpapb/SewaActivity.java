package com.papb.projekpapb;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import java.util.HashMap;

public class SewaActivity extends AppCompatActivity {

    Button tambah, kurang, sewa, batal;
    TextView namaKendaraan, jenisKendaraan, lamaSewa, hargaSewa, totalSewa, cc;
    EditText namaPenyewa, noKtp;
    int lama=1;
    int total;
    String HargaSewa;
    String mnamaKendaraan;
    String mhargaSewa;
    ProgressDialog progress;
    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "com.papb.projekpapb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewa);
        progress = new ProgressDialog(this);

        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(false);
        progress.show();

        tambah = findViewById(R.id.btn_tambah_sewa);
        kurang = findViewById(R.id.btn_kurang_sewa);
        sewa = findViewById(R.id.btnSewa);
        batal = findViewById(R.id.btnBatal);
        cc = findViewById(R.id.txtCc);
        lamaSewa = findViewById(R.id.txt_lama_pinjam_sewa);
        hargaSewa = findViewById(R.id.txtHargaSewa);
        totalSewa = findViewById(R.id.txt_total_sewa);
        namaKendaraan = findViewById(R.id.txtNamaKendaraan);
        jenisKendaraan = findViewById(R.id.txtMerk);
        namaPenyewa = findViewById(R.id.edtNamaPenyewa);
        noKtp = findViewById(R.id.edtNomorKtp);

        //saved instance
        Intent inten = getIntent();
        Bundle intent = inten.getExtras();

        mnamaKendaraan = intent.getString("namaKendaraan");
        final String mcc = intent.getString("cc");
        final String merk = intent.getString("merk");
        mhargaSewa = intent.getString("hargaSewa");

        hargaSewa.setText("Harga Sewa: "+mhargaSewa);
        namaKendaraan.setText("Nama Kendaraan: "+mnamaKendaraan);
        jenisKendaraan.setText("Merek: "+merk);
        cc.setText(mcc+" CC");
        HargaSewa = mhargaSewa;

        //get SharedPrev
        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);
        final String nama = mPreferences.getString("nama", "");
        final String mNoKtp = mPreferences.getString("noKtp", "");

        namaPenyewa.setText(nama);
        noKtp.setText(mNoKtp);

        if (savedInstanceState != null) {
            String message = savedInstanceState.getString("total");
            int lama = savedInstanceState.getInt("lama");
            totalSewa.setText(message);
            this.lama = lama;
            lamaSewa.setText(lama+"");
        }else{
            lamaSewa.setText(lama+"");
            totalSewa.setText("Total Harga Sewa : Rp." + HargaSewa);
        }

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lama += 1;
                lamaSewa.setText(lama + "");
                int harga = Integer.parseInt(HargaSewa);
                total = lama * harga;
                totalSewa.setText("Total Harga Sewa : Rp." + total);
            }
        });
        kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lama > 1) {
                    lama -= 1;
                    lamaSewa.setText(lama + "");
                    int harga = Integer.parseInt(HargaSewa);
                    total = lama * harga;
                    totalSewa.setText("Total Harga Sewa : Rp." + total);
                }
            }
        });

        progress.dismiss();

        sewa.setOnClickListener(view -> {
            showNotif(mnamaKendaraan, mhargaSewa);
        });

    }


    private void showNotif(String namaKendaraan, String harga) {
        String NOTIFICATION_CHANNEL_ID = "channel_androidnotif";
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Android Notif Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent mIntent = new Intent(this, SewaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fromnotif", "notif");
        mIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_add_black_24dp))
                .setTicker("notif starting")
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Selamat, kendaraan "+namaKendaraan+" berhasil disewa.")
                .setContentText("Dengan total harga "+harga);

        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(115, builder.build());
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        icicle.putString("total", totalSewa.getText().toString());
        icicle.putInt("lama", lama);
        super.onSaveInstanceState(icicle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //set sahredprev
        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("nama", namaPenyewa.getText().toString());
        preferencesEditor.putString("noKtp", noKtp.getText().toString());

//        Toast toast = Toast.makeText(this, namaPenyewa.getText().toString(), Toast.LENGTH_LONG);
//        toast.show();
        preferencesEditor.apply();
    }
}
