package mx.tec.proyectofinal.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import mx.tec.proyectofinal.R;
import mx.tec.proyectofinal.includes.MyToolbar;
import mx.tec.proyectofinal.providers.GoogleApiProvider;
import mx.tec.proyectofinal.utils.DecodePoints;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRuta extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment gMapFragment;

    private double originLat;
    private double originLng;
    private double destinoLat;
    private double destinoLng;

    private LatLng origenLatLng;
    private LatLng destinoLatLng;

    private GoogleApiProvider googleApiProvider;

    private List<LatLng> gPolylinesList;
    private PolylineOptions polylineOptions;

    private TextView textViewOrigen;
    private TextView textViewDestino;
    private TextView textViewDistancia;

    private Bundle parametros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ruta);
        MyToolbar.show(this, "Tus datos", true);

        textViewOrigen = findViewById(R.id.TextViewOrigen);
        textViewDestino = findViewById(R.id.TextViewDestino);
        textViewDistancia = findViewById(R.id.TextViewDistancia);

        gMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        gMapFragment.getMapAsync(this);

        originLat = getIntent().getDoubleExtra("origin_lat", 0);
        originLng = getIntent().getDoubleExtra("origin_lng", 0);
        destinoLat = getIntent().getDoubleExtra("destino_lat", 0);
        destinoLng = getIntent().getDoubleExtra("destino_lng", 0);

        origenLatLng = new LatLng(originLat, originLng);
        destinoLatLng = new LatLng(destinoLat, destinoLng);

        googleApiProvider = new GoogleApiProvider(SearchRuta.this);

        parametros = this.getIntent().getExtras();
        textViewOrigen.setText(parametros.getString("OrigenString"));
        textViewDestino.setText(parametros.getString("DestinoString"));


    }

    private void DrawRoute(){
        googleApiProvider.getDirections(origenLatLng, destinoLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Respuesta del servidor
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    gPolylinesList = DecodePoints.decodePoly(points);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.DKGRAY).width(8f).startCap(new SquareCap()).jointType(JointType.ROUND).addAll(gPolylinesList);
                    gMap.addPolyline(polylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    String distanceText = distance.getString("text");
                    textViewDistancia.setText(distanceText);


                } catch (Exception e) {
                    Log.d("Error","Error encontrado" + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);

        gMap.addMarker(new MarkerOptions().position(origenLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.img_origen)));
        gMap.addMarker(new MarkerOptions().position(destinoLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.img_destino)));

        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(origenLatLng)
                .zoom(14f)
                .build()
        ));
        DrawRoute();

    }
}