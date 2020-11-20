package mx.tec.proyectofinal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import mx.tec.proyectofinal.includes.MyToolbar;
import mx.tec.proyectofinal.providers.AuthProvider;
import mx.tec.proyectofinal.utils.DecodePoints;
import mx.tec.proyectofinal.R;
import mx.tec.proyectofinal.activities.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Map2 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment gMapFragment;
    AuthProvider authProvider;
    private LatLng latLngActual;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Spinner spinner;


    private PolylineOptions polylineOptions;
    //List<LatLng> points;

    GPXParser mParser = new GPXParser();
    Polyline polyline1;

    String[] rutas = {"Ruta1","Ruta2","Ruta3","Ruta4","Ruta5","Ruta5 Ahuatepec","Ruta5 Oriente","Ruta5 Texcal", "Ruta7", "Ruta7 Villa", "Ruta9", "Ruta Morelos", "Interescolar"};
    private List<Integer> colorList = new ArrayList<Integer>();

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    latLngActual = new LatLng(location.getLatitude(), location.getLongitude());
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
        setContentView(R.layout.activity_map2);

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
                        break;
                    case R.id.navigation_search:
                        Intent intent = new Intent(Map2.this, mx.tec.proyectofinal.activities.Map.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_prefer:
                        //Intent intent3 = new Intent(Map2.this, MainActivity.class);
                        //startActivity(intent3);
                        Toast.makeText(getApplicationContext(), "Proximamente", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //Iniciar o detener la ubicación del usuario cada vez que lo veamos conveniente

        JodaTimeAndroid.init(this);
        spinner =(Spinner)findViewById(R.id.check_rutas);
        spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner,rutas));
        spinner.setPrompt("Seleccione una ruta");

        String colors[] = this.getResources().getStringArray(R.array.list_color);
        colorList = new ArrayList<Integer>();

        for (String color : colors) {
            colorList.add(Color.parseColor(color));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.setPadding(0, 0, 0, 130);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); //el intervalo de tiempo en el que se va a actualizar la ubicación del usuario en el mapa
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        gMap.setMyLocationEnabled(true);
        polylineOptions = new PolylineOptions();

        ArrayList<LatLng> points = new ArrayList<LatLng>();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View view, int pos, long id) {
                    if(polyline1 == null){

                    }else{
                        polyline1.remove();
                    }
                    //Toast.makeText(getApplicationContext(), parentView.getItemAtPosition(pos).toString() , Toast.LENGTH_LONG).show();
                    Gpx parsedGpx = null;
                    try {
                         InputStream in = getAssets().open(parentView.getItemAtPosition(pos).toString() + ".gpx");
                         parsedGpx = mParser.parse(in);
                    } catch (IOException | XmlPullParserException e) {
                        e.printStackTrace();
                    }
                    if (parsedGpx != null) {
                        // log stuff
                        List<Track> tracks = parsedGpx.getTracks();
                        for (int i = 0; i < tracks.size(); i++) {
                            Track track = tracks.get(i);
                            //Log.d(TAG, "track " + i + ":");
                            List<TrackSegment> segments = track.getTrackSegments();
                            for (int j = 0; j < segments.size(); j++) {
                                TrackSegment segment = segments.get(j);
                                //Log.d(TAG, "  segment " + j + ":");
                                for (TrackPoint trackPoint : segment.getTrackPoints()) {
                                    // Log.d(TAG, "    point: lat " + trackPoint.getLatitude() + ", lon " + trackPoint.getLongitude());

                                    double LatitudeL[] = {trackPoint.getLatitude()};
                                    double LongitudeL[] = {trackPoint.getLongitude()};

                                    //double LatitudeL[] = {-27.7138761151582, -27.713891873136163, -27.713900171220303, -27.713972255587578, -27.713974770158529, -27.713967896997929, -27.713976949453354, -27.713974099606276, -27.713983403518796, -27.713974267244339, -27.713965885341167, -27.713977117091417, -27.71568794734776, -27.715969746932387, -27.719649067148566, -27.748650452122092};
                                    //double LongitudeL[] = {-48.703893441706896, -48.703868128359318, -48.703850442543626, -48.703786907717586, -48.703775340691209, -48.703786404803395, -48.703784644603729, -48.70379202067852, -48.70379101485014, -48.703799732029438, -48.703791350126266, -48.703792775049806, -48.703289357945323, -48.703037817031145, -48.701020879670978, -48.710173834115267};
                                    Log.d("TAG", Arrays.toString(LatitudeL) + ", " + Arrays.toString(LongitudeL));
                                    for (int k = 0; k < LatitudeL.length; k++) {
                                        points.add(new LatLng(LatitudeL[k], LongitudeL[k]));
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e("TAG", "Error parsing gpx track!");
                    }
                    PolylineOptions polyline_options = new PolylineOptions().addAll(points);
                    polyline1 = googleMap.addPolyline(polyline_options);
                    points.clear();
                    polyline1.setColor(colorList.get(pos));
                    stylePolyline(polyline1);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


    }
    private void stylePolyline(Polyline polyline) {
        polyline.setEndCap(new RoundCap());
        polyline.setJointType(JointType.ROUND);
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
        Intent intent = new Intent(Map2.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}