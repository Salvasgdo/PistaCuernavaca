package mx.tec.proyectofinal.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import mx.tec.proyectofinal.R;


public class MainActivity extends AppCompatActivity {

    Button mLogin, mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogin = findViewById(R.id.btnLogin);
        mRegister = findViewById(R.id.btnRegister);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, mx.tec.proyectofinal.activities.Map.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Con el boton de atrás ya no pueda regresar a esta pantalla
            startActivity(intent);
        }

    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, mx.tec.proyectofinal.activities.LoginActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        Intent intent = new Intent(MainActivity.this, mx.tec.proyectofinal.activities.RegisterActivity.class);
        startActivity(intent);
    }

}