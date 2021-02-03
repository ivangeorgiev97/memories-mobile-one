package uni.fmi.masters.memories.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;

public class CategoriesFragment extends Fragment {

    ListView categoriesLV;
    CategoriesAdapter categoriesAdapter;

    private CategoriesViewModel categoriesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Category 1"));
        categories.add(new Category(2, "Category 2"));
        categories.add(new Category(3, "Category 3"));

        View root = inflater.inflate(R.layout.fragment_categories, container, false);
        categoriesLV = root.findViewById(R.id.categoriesListView);
        categoriesAdapter = new CategoriesAdapter(getContext(), R.layout.categories_list_row, categories);
        categoriesLV.setAdapter(categoriesAdapter);

        return root;
    }
}