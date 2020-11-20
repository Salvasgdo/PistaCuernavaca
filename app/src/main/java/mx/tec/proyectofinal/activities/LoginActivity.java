package mx.tec.proyectofinal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import dmax.dialog.SpotsDialog;
import mx.tec.proyectofinal.includes.MyToolbar;
import mx.tec.proyectofinal.providers.AuthProvider;
import mx.tec.proyectofinal.R;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextEmail, mTextPassword;
    Button btnSend;

    DatabaseReference loginDb;
    AlertDialog loginAlert;
    AuthProvider authProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyToolbar.show(this, "Inicio de sesión", true);

        mTextEmail = findViewById(R.id.textInputEmail);
        mTextPassword = findViewById(R.id.textInputPass);
        btnSend = findViewById(R.id.btnSend);

        authProvider = new AuthProvider();

        loginDb = FirebaseDatabase.getInstance().getReference();

        loginAlert = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Cargando").build();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //mostrar();
            }
        });


    }

    private void mostrar() {
        Toast.makeText(LoginActivity.this, "El login se hizo correctamente", Toast.LENGTH_SHORT).show();
    }

    private void login() throws InterruptedException {
        String email = mTextEmail.getText().toString();
        String password = mTextPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                loginAlert.show();
                Thread.sleep(200);
                authProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "El login se hizo correctamente", Toast.LENGTH_SHORT).show();
                            /*mTextEmail.setText("");
                            mTextPassword.setText("");*/
                            Intent intent = new Intent(LoginActivity.this, mx.tec.proyectofinal.activities.Map.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Con el boton de atrás ya no pueda regresar a esta pantalla
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "El correo o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        loginAlert.dismiss();
                    }
                });
            }
            else{
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "La contraseña y el correo son obligatorios", Toast.LENGTH_SHORT).show();
        }

    }
}