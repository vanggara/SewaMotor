package com.papb.projekpapb.akun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.papb.projekpapb.EditActivity;
import com.papb.projekpapb.LoginActivity;
import com.papb.projekpapb.MainActivity;
import com.papb.projekpapb.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class ProfilFragment extends Fragment {

    private ProfilViewModel profilViewModel;
    ImageView GambarProfile;
    TextView Nama, Email;

    Button Logout, Edit;
    SwitchCompat setting;
    View root;
    StorageReference storage;
    ProgressDialog progress;
    FirebaseUser mUser;
    DatabaseReference mReference;
    String idUser, urlPhoto, nama, email;
    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "com.papb.projekpapb.akun";
    private static final String FIRST_RUN = "mode";
    Boolean switchPref = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_profil, container, false);

        GambarProfile = root.findViewById(R.id.img_profile);
        Nama = root.findViewById(R.id.txt_Nama);
        Email = root.findViewById(R.id.txt_Email);
        Logout = root.findViewById(R.id.btn_keluar);
        Edit = root.findViewById(R.id.btn_edit);
        setting = root.findViewById(R.id.btn_setting);
        progress = new ProgressDialog(getContext());

        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(false);
        progress.show();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        mPreferences = getContext().getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);

        Boolean yourLocked = mPreferences.getBoolean("mode", false);
        Toast.makeText(getContext(), yourLocked.toString(), Toast.LENGTH_SHORT).show();
            if(yourLocked){
                switchPref=true;
                setting.setChecked(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                switchPref=false;
                setting.setChecked(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

        setting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchPref=true;
                    Toast.makeText(getContext(), "Mode Night ON", Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    switchPref=false;
                    Toast.makeText(getContext(), "Mode Night OFF", Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        });

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new AsyncTaskRunner().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("error ", databaseError.getMessage());
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_keluar) {
                    AuthUI.getInstance()
                            .signOut(getContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);//
                                    Toast.makeText(getContext(), "Anda berhasil keluar!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("idUser", idUser);
                startActivity(intent);//
            }
        });
        return root;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    class AsyncTaskRunner extends AsyncTask<DataSnapshot, Integer, String> {

        protected void onPreExecute() {
            System.out.println("pree");
        }

        protected String doInBackground(DataSnapshot... parameters) {
            nama = parameters[0].child("username").getValue().toString();
            email = parameters[0].child("email").getValue().toString();
            urlPhoto = parameters[0].child("urlPhoto").getValue().toString();
            idUser =  parameters[0].child("id").getValue().toString();
            return "Sukses";
        }

        protected void onProgressUpdate(Integer... values) {
        }

        protected void onPostExecute(String result) {
            if(!urlPhoto.equalsIgnoreCase("default")){
                Glide.with(getContext()).load(urlPhoto)
                        .diskCacheStrategy(DiskCacheStrategy.NONE )
                        .skipMemoryCache(true)
                        .into(GambarProfile);
            }
            Nama.setText(nama);
            Email.setText(email);
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            System.out.println("hasil :" + result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //set sahredprev
        mPreferences = getContext().getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        if(switchPref){
            preferencesEditor.putBoolean("mode", true);
        }else{
            preferencesEditor.putBoolean("mode", false);
        }

//        Toast toast = Toast.makeText(this, namaPenyewa.getText().toString(), Toast.LENGTH_LONG);
//        toast.show();
        preferencesEditor.apply();
    }

}