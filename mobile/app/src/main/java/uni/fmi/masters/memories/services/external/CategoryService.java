package uni.fmi.masters.memories.services.external;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import uni.fmi.masters.memories.entities.Category;

public interface CategoryService {
    @GET("categories/getAll")
    Call<List<Category>> getAllCategories();

    @GET("categories/getById/{id}")
    Call<Category> getCategoryById(@Path("id") int id);
}
