package mx.tec.proyectofinal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import mx.tec.proyectofinal.includes.MyToolbar;
import mx.tec.proyectofinal.models.User;
import mx.tec.proyectofinal.providers.AuthProvider;
import mx.tec.proyectofinal.providers.UserProvider;
import mx.tec.proyectofinal.R;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    TextInputEditText regTextEmail, regTextName, regTextPass, regTextPass2;
    AlertDialog registerAlert;

    AuthProvider authProvider;
    UserProvider userProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MyToolbar.show(this, "Registro de datos", true);

        authProvider = new AuthProvider();
        userProvider = new UserProvider();

        regTextEmail = findViewById(R.id.email);
        regTextName = findViewById(R.id.name);
        regTextPass = findViewById(R.id.password);
        regTextPass2 = findViewById(R.id.repeatPass);
        btnRegister = findViewById(R.id.btnRegister2);

        registerAlert = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Cargando").build();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void registerUser() {
        final String name = regTextName.getText().toString();
        final String email = regTextEmail.getText().toString();
        String password = regTextPass.getText().toString();
        String password2 = regTextPass2.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password2.isEmpty() ) {
            if (validarEmail(email)) {
                if( password.length() >= 6) {
                    if(password.equals(password2)){
                        registerAlert.show();
                        authProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                registerAlert.hide();
                                if (task.isSuccessful()) {
                                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    User user = new User(id, name, email);
                                    create(user);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "El correo no es valido", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingresar todos los datos", Toast.LENGTH_SHORT).show();
        }

    }

    void create(User user){
        userProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "El registro se realizó exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Con el boton de atrás ya no pueda regresar a esta pantalla
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


/*    private void saveUser(String id, String name, String email) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);

        regDb.child("Users").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Se registro correctamente", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
}