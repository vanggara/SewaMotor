package com.papb.projekpapb.cari;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.papb.projekpapb.R;
import com.papb.projekpapb.adapter.CustomAdapter;
import com.papb.projekpapb.akun.ProfilFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CariFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    CardView cardView;
    ProgressDialog progress;
    ArrayList<CariViewModel> listOs = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cari, container, false);

        progress = new ProgressDialog(getContext());

        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(false);
        progress.show();
        new AsyncTaskRunner().execute();
        mRecyclerView =
                (RecyclerView) root.findViewById(R.id.recyclerview);
        mAdapter = new CustomAdapter(getContext(), listOs);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new
                LinearLayoutManager(getContext()));
        return root;
    }


    class AsyncTaskRunner extends AsyncTask<Void, Integer, String> {

        protected void onPreExecute() {
            System.out.println("pree");
        }

        protected String doInBackground(Void... parameters) {
// populasikan nama Sisop di sini
            listOs.add(new CariViewModel("Ninja 250", R.drawable.aldos, "Kawasaki", "250", "250000"));
            listOs.add(new CariViewModel("Chopper 150", R.drawable.aldos, "Honda", "150", "150000"));
            return "Sukses";
        }

        protected void onProgressUpdate(Integer... values) {
        }

        protected void onPostExecute(String result) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            System.out.println("hasil :" + result);
        }
    }
}