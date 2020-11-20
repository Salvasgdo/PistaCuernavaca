package mx.tec.proyectofinal.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import mx.tec.proyectofinal.models.User;

public class UserProvider {

    DatabaseReference regDb;

    public UserProvider() {
        regDb = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public Task<Void> create(User user){
        Map<String, Object> hashmap = new HashMap<>();
        hashmap.put("name", user.getName());
        hashmap.put("email", user.getEmail());
        return regDb.child(user.getId()).setValue(hashmap);
    }


}
