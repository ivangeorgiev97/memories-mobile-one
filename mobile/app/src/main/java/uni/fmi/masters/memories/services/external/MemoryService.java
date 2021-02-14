package uni.fmi.masters.memories.services.external;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import uni.fmi.masters.memories.entities.Memory;

public interface MemoryService {
    @GET("memories/getAll")
    Call<List<Memory>> getAllMemories();

    @GET("memories/getById/{id}")
    Call<Memory> getMemoryById(@Path("id") int id);

    @FormUrlEncoded
    @PUT("memories/update/{id}")
    Call<Memory> update(@Path("id") int id, @Field("title") String name, @Field("description") String description, @Field("category_id") int category_id);

    @FormUrlEncoded
    @POST("memories/create")
    Call<Memory> addMemory(@Field("id") int id, @Field("title") String name, @Field("description") String description, @Field("category_id") int category_id);

    @DELETE("memories/delete/{id}")
    Call<Memory> deleteMemory(@Path("id") int id);
}
