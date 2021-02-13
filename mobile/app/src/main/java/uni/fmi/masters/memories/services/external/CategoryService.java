package uni.fmi.masters.memories.services.external;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import uni.fmi.masters.memories.entities.Category;

public interface CategoryService {
    @GET("categories/getAll")
    Call<List<Category>> getAllCategories();
}
