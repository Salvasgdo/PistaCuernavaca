package mx.tec.proyectofinal.providers;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import mx.tec.pistacuernavaca.retrofit.IGoogleApi;
import mx.tec.pistacuernavaca.retrofit.RetrofitUser;
import mx.tec.proyectofinal.R;
import retrofit2.Call;

public class GoogleApiProvider {

    private Context context;
    public GoogleApiProvider(Context context){
            this.context=context;
    }

    public Call<String> getDirections(LatLng origen, LatLng destino){
        String base = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + origen.latitude + "," + origen.longitude + "&"
                + "destination=" + destino.latitude + "," + destino.longitude + "&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);
        return RetrofitUser.getUser(base).create(IGoogleApi.class).getDirections(base + query);
    }

}
