package com.papb.projekpapb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1;
    ProgressDialog progress;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Button btn_login, btn_batal;
    EditText txt_Email, txt_Password;
    TextView txt_Daftar;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference mReference;
    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "com.papb.projekpapb";

    protected void onStart() {
        super.onStart();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //mengecek apakah user null?
        if (mUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btnLogin);
        txt_Email = findViewById(R.id.edtEmail);
        txt_Password = findViewById(R.id.edtPassword);
        txt_Daftar = findViewById(R.id.txtDaftar);
        progress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        //get SharedPrev
        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);
        final String email = mPreferences.getString("email", "");
        final String password = mPreferences.getString("password", "");

        txt_Email.setText(email);
        txt_Password.setText(password);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        txt_Daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setMessage("Please Wait");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(true);
                progress.setIndeterminate(false);
                progress.show();
                String txt_email = txt_Email.getText().toString();
                String txt_password = txt_Password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Snackbar.make(findViewById(R.id.layout_login), "Semua field harus terisi!!", Snackbar.LENGTH_SHORT).show();
//                    Toast.makeText(LoginActivity.this, "Semua field harus terisi Fergusso!!", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
                else {
                    mAuth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        progress.dismiss();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progress.dismiss();
//                                        Snackbar.make(findViewById(R.id.layout_login), "Anda tidak terautentifikasi Fergusso!", Snackbar.LENGTH_SHORT).show();
                                        Toast.makeText(LoginActivity.this, "Anda tidak terautentifikasi!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, MY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("error", "Google sign in failed", e);
//                Snackbar.make(findViewById(R.id.layout_login), "Silahkan login dengan metode lain.", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, "Silahkan login dengan metode lain!!", Toast.LENGTH_SHORT).show();
            }

//            handleSignInResult(task);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("message", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("message", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String userid = user.getUid();

                            mReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", user.getDisplayName());
                            hashMap.put("email", user.getEmail());
                            hashMap.put("urlPhoto", "default");

                            mReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("error", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.layout_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        //set sahredprev
        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("email", txt_Email.getText().toString());
        preferencesEditor.putString("password", txt_Password.getText().toString());

//        Toast toast = Toast.makeText(this, namaPenyewa.getText().toString(), Toast.LENGTH_LONG);
//        toast.show();
        preferencesEditor.apply();
    }
}
