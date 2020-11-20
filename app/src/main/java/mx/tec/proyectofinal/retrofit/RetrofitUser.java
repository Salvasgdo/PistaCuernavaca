package mx.tec.pistacuernavaca.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitUser {

    private static Retrofit retrofit = null;

    public static Retrofit getUser(String url){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().
                            baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                            .build();
        }
        return retrofit;
    }



}
