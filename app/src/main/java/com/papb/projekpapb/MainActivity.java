package com.papb.projekpapb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.papb.projekpapb.akun.ProfilFragment;
import com.papb.projekpapb.cari.CariFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private  BottomNavigationView nav;
    ImageView GambarProfile;
    TextView Nama, Email, ID;
    Button Logout;
    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "com.papb.projekpapb.akun";

    //hanya untuk menambahkan commit

    GoogleSignInClient mGoogleSignInClient;
    private boolean isNightModeEnabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        nav = findViewById(R.id.nav_view);

        nav.setOnNavigationItemSelectedListener(this);
        nav.setSelectedItemId(R.id.navigation_cari);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);

        Boolean yourLocked = mPreferences.getBoolean("mode", false);
        Toast.makeText(this, yourLocked.toString(), Toast.LENGTH_SHORT).show();
        if(yourLocked){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {

            case R.id.navigation_akun:
                fragment = new ProfilFragment();
                break;

            case R.id.navigation_cari:
                fragment = new CariFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
