package com.papb.projekpapb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, noHp;
    Button btnDaftar;
    TextView txtMasuk;
    ProgressDialog progress;

    TextInputEditText password, repassword;

    FirebaseAuth mAuth;
    DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progress = new ProgressDialog(this);
        username = findViewById(R.id.edtNama);
        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPass);
        repassword = findViewById(R.id.edtRePass);
        btnDaftar = findViewById(R.id.btnSelanjutnya);
        txtMasuk = findViewById(R.id.txtMasuk);

        mAuth = FirebaseAuth.getInstance();

        txtMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnDaftar.setOnClickListener(view -> {

            String txt_Username = username.getText().toString();
            String txt_Email = email.getText().toString();
            String txt_Password = password.getText().toString();
            String txt_RePassword = repassword.getText().toString();

            if (TextUtils.isEmpty(txt_Username) || TextUtils.isEmpty(txt_Email) || TextUtils.isEmpty(txt_Password)) {
                Toast.makeText(RegisterActivity.this, "Lengkapi semua field!", Toast.LENGTH_SHORT).show();
            } else if (!txt_Password.equals(txt_RePassword)){
                Toast.makeText(RegisterActivity.this, "Password harus sama!", Toast.LENGTH_SHORT).show();
            }
            else {
                register(txt_Username, txt_Email, txt_Password);
            }
        });
    }

    private void register(final String username, String email, String password) {
        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(false);
        progress.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        mReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("email", email);
                        hashMap.put("urlPhoto", "default");

                        mReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            progress.hide();
                        });
                    } else {
                        progress.hide();
                        Toast.makeText(RegisterActivity.this, "Tidak bisa register dengan Email dan Password ini!", Toast.LENGTH_SHORT).show();
                    }
                });



    }
}
