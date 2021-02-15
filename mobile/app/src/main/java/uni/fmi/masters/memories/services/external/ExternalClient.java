package uni.fmi.masters.memories.services.external;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExternalClient {

    private static Retrofit retrofitClient = null;
    private static Gson gson = null;

    public static Retrofit getExternalClient(String apiUrl) {
        if (gson == null) {
            gson = new GsonBuilder().setLenient().create();
        }

        if (retrofitClient == null) {
            retrofitClient = new Retrofit.Builder().baseUrl(apiUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofitClient;
    }

}
