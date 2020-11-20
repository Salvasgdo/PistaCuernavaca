package mx.tec.proyectofinal.includes;

import android.app.FragmentManager;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import mx.tec.proyectofinal.R;
import mx.tec.proyectofinal.activities.MainActivity;
import mx.tec.proyectofinal.activities.Map2;
import mx.tec.proyectofinal.activities.SearchRuta;

public class BottomNavigation {

    public static void showBottomNavigation(final AppCompatActivity activity, boolean visible){

        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
        if(visible){
            bottomNav.setVisibility(View.VISIBLE);
        }else{
            bottomNav.setVisibility(View.GONE);
        }

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        Intent intent = new Intent(activity, mx.tec.proyectofinal.activities.Map.class );
                        activity.startActivity(intent);
                        break;
                    case R.id.navigation_search:
                        Intent intent2 = new Intent(activity, Map2.class );
                        activity.startActivity(intent2);
                        break;
                    case R.id.navigation_prefer:
                        Intent intent3 = new Intent(activity, MainActivity.class );
                        activity.startActivity(intent3);
                        break;
                }
                return true;
            }
        });
    }
}
