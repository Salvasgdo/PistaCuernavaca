package mx.tec.proyectofinal.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import mx.tec.proyectofinal.R;


public class MyToolbar {
    public static void show(AppCompatActivity activity, String title, boolean button){

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(button);

    }

}
