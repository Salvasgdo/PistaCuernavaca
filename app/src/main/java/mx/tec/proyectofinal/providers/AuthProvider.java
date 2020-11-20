package mx.tec.proyectofinal.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth regAuth;

    public AuthProvider(){
        regAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String password){
        return regAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password){
        return regAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout(){
        regAuth.signOut();
    }

}
