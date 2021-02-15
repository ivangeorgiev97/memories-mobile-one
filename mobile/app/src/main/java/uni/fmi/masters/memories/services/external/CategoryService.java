package uni.fmi.masters.memories.services.external;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import uni.fmi.masters.memories.entities.Category;

public interface CategoryService {
    @GET("categories/getAll")
    Call<List<Category>> getAllCategories();

    @GET("categories/getById/{id}")
    Call<Category> getCategoryById(@Path("id") int id);

    @FormUrlEncoded
    @PUT("categories/update/{id}")
    Call<Category> updateCategory(@Path("id") int id, @Field("name") String name);

    @FormUrlEncoded
    @POST("categories/create")
    Call<Category> addCategory(@Field("id") int id, @Field("name") String name);

    @FormUrlEncoded
    @POST("categories/create")
    Call<Category> addCategoryWithoutId(@Field("name") String name);

    @DELETE("categories/delete/{id}")
    Call<Category> deleteCategory(@Path("id") int id);
}
