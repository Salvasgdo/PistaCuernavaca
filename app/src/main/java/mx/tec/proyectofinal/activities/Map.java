package mx.tec.proyectofinal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.SphericalUtil;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Locale;

import javax.security.auth.login.LoginException;

import mx.tec.proyectofinal.includes.MyToolbar;
import mx.tec.proyectofinal.includes.BottomNavigation;
import mx.tec.proyectofinal.providers.AuthProvider;
import mx.tec.proyectofinal.R;
import mx.tec.proyectofinal.activities.MainActivity;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment gMapFragment;
    AuthProvider authProvider;
    private LatLng latLngActual;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final static int LOCATION_REQUEST_CODE = 1;  //Bandera para saber si se necesita solicitar los permisos de ubicación
    private final static int SETTINGS_REQUEST_CODE = 2;

    private AutocompleteSupportFragment autocompleteSupportFragment;
    private AutocompleteSupportFragment autocompleteSupportFragmentDestino;
    private PlacesClient placesClient;

    private String Origen;
    private LatLng OrigenLatLng;

    private String Destino;
    private LatLng DestinoLatLng;

    private Button buttonSearch;


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    latLngActual = new LatLng(location.getLatitude(), location.getLongitude());
                    limitacion();
                    //Obtener localización del usuario en tiempo real
                    gMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        gMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        gMapFragment.getMapAsync(this);
        authProvider = new AuthProvider();
        MyToolbar.show(this, "Mapa", false);
        //BottomNavigation.showBottomNavigation(this, true);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        Intent intent = new Intent(Map.this, mx.tec.proyectofinal.activities.Map2.class );
                        startActivity(intent);
                        break;
                    case R.id.navigation_search:
                        break;
                    case R.id.navigation_prefer:
                        //Intent intent3 = new Intent(Map.this, MainActivity.class );
                        //startActivity(intent3);
                        Toast.makeText(getApplicationContext(), "Proximamente", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //Iniciar o detener la ubicación del usuario cada vez que lo veamos conveniente

        Places.initialize(getApplicationContext(), "AIzaSyAprJwkK2NOxbByDNQp1--F1938O6EIsWA");
        placesClient = Places.createClient(this);
        instanceAutocompleteOrigin();
        instanceAutocompleteDestino();

        buttonSearch = findViewById(R.id.btnSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMap();
            }
        });

    }

    private void searchMap() {
        if(OrigenLatLng != null && DestinoLatLng != null){
            Intent intent = new Intent(Map.this, mx.tec.proyectofinal.activities.SearchRuta.class );
            intent.putExtra("origin_lat", OrigenLatLng.latitude);
            intent.putExtra("origin_lng", OrigenLatLng.longitude);
            intent.putExtra("destino_lat", DestinoLatLng.latitude);
            intent.putExtra("destino_lng", DestinoLatLng.longitude);
            intent.putExtra("OrigenString", Origen);
            intent.putExtra("DestinoString", Destino);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Debe de seleccionar el lugar de origen y el destino", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.setPadding(0,0,0,130);



        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); //el intervalo de tiempo en el que se va a actualizar la ubicación del usuario en el mapa
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(gpsActived()){
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        gMap.setMyLocationEnabled(true);
                    }else{
                       showAlertGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {   //Está esperando una respuesta positiva
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());  //garantizar una validación
            gMap.setMyLocationEnabled(true);
        }
        else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()){
           showAlertGPS();
        }
    }

    private void showAlertGPS(){  //para activar el gps
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicación para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived(){
        boolean isActived = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActived = true;
        }
        return isActived;
    }

    private void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(gpsActived()){
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    gMap.setMyLocationEnabled(true);
                }
                else{
                   showAlertGPS();
                }
            }
            else{
                checkLocationPermissions();
            }
        }else{
            if(gpsActived()){
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
            else{
                    showAlertGPS();
            }
        }
    }


    private void checkLocationPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicación requiere de los permisos de ubicación")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Map.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void limitacion(){
        LatLng norte = SphericalUtil.computeOffset(latLngActual, 5000, 0);
        LatLng sur = SphericalUtil.computeOffset(latLngActual, 5000, 180);
        autocompleteSupportFragment.setCountry("MX");
        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(sur, norte));
        autocompleteSupportFragmentDestino.setCountry("MX");
        autocompleteSupportFragmentDestino.setLocationBias(RectangularBounds.newInstance(sur, norte));
    }

    private void instanceAutocompleteOrigin(){

        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteOrigen);
        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragment.setHint("Origen");
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Origen = place.getName();
                OrigenLatLng = place.getLatLng();
                Log.d("PLACE", "Name:" + Origen);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("PLACE", "An error occurs:" + status);
            }
        });
    }

    private void instanceAutocompleteDestino(){
        autocompleteSupportFragmentDestino = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteDestino);
        autocompleteSupportFragmentDestino.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteSupportFragmentDestino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragmentDestino.setHint("Destino");
        autocompleteSupportFragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Destino = place.getName();
                DestinoLatLng = place.getLatLng();
                Log.d("PLACE", "Name:" + Destino);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("PLACE", "An error occurs:" + status);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    void logout() {
        authProvider.logout();
        Intent intent = new Intent(Map.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}