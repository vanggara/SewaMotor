package com.papb.projekpapb.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import androidx.cardview.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.papb.projekpapb.R;
import com.papb.projekpapb.SewaActivity;
import com.papb.projekpapb.cari.CariViewModel;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class CustomAdapter extends RecyclerView.Adapter <CustomAdapter.CustomViewHolder> {
    LayoutInflater mInflater;
    ArrayList<CariViewModel> sisop;
    TextView namaKendaraan, merk, cc, hargaSewa;
    ImageView imageView;
    CardView cardView;
    Context context;

    private SharedPreferences mPreferences;
    private final String COUNT_KEY = "count";
    private final String COLOR_KEY = "color";
    private String sharedPrefFile = "com.papb.projekpapb";

    public CustomAdapter(Context context,
                         ArrayList<CariViewModel> sisop) {
        this.mInflater = LayoutInflater.from(context);
        this.sisop = sisop;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view from layout
        View itemView = mInflater.inflate(
                R.layout.rowview, parent, false);
        return new CustomViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
// Retrieve the data for that position
        final CariViewModel current = sisop.get(position);
// Add the data to the view
        imageView.setBackground(ContextCompat.getDrawable(mInflater.getContext(), current.image));
        namaKendaraan.setText(current.namaKendaraan);
        hargaSewa.setText("Rp. "+current.hargaSewa);
        cc.setText(current.cc+" CC");
        merk.setText(current.merk);
// add the Listener to the view of that position if desired
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent (view.getContext(), SewaActivity.class);
                intent.putExtra("namaKendaraan", current.namaKendaraan);
                intent.putExtra("merk", current.merk);
                intent.putExtra("cc", current.cc);
                intent.putExtra("hargaSewa", current.hargaSewa);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of
// data items to display
        return sisop.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private CustomAdapter mAdapter;

        public CustomViewHolder(View itemView, CustomAdapter adapter) {
            super(itemView);
            // Get the layout
            namaKendaraan = (TextView) itemView.findViewById(R.id.namaKendaraan);
            merk = (TextView) itemView.findViewById(R.id.txtMerk);
            cc = (TextView) itemView.findViewById(R.id.txtCc);
            hargaSewa = (TextView) itemView.findViewById(R.id.txtHargaSewa);
            imageView = (ImageView) itemView.findViewById(R.id.logo);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
// Associate with this adapter
            this.mAdapter = adapter;
        }

    }


}