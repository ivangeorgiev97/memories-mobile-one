package uni.fmi.masters.memories.services.external;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExternalClient {

    private static Retrofit retrofitClient = null;

    public static Retrofit getExternalClient(String apiUrl) {
        if (retrofitClient == null) {
            retrofitClient = new Retrofit.Builder().baseUrl(apiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofitClient;
    }

}
