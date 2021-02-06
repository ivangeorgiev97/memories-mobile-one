package uni.fmi.masters.memories.ui.categories;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;

public class CategoriesFragment extends Fragment {

    ListView categoriesLV;
    CategoriesAdapter categoriesAdapter;
    FloatingActionButton addCategoryB;
    Dialog customDialog;

    private CategoriesViewModel categoriesViewModel;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog = new Dialog(getContext());
            customDialog.setContentView(R.layout.add_edit_category_dialog);

            final EditText categoryNameET = customDialog.findViewById(R.id.categoryNameEditText);
            Button okayB = customDialog.findViewById(R.id.okayButton);
            Button cancelB = customDialog.findViewById(R.id.cancelButton);

            cancelB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.cancel();
                }
            });


            okayB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO - ADD CATEGORY TO BACKEND LOGIC
                        }
                    }).start();
                }
            });

            customDialog.setTitle("Добави категория");
            customDialog.setCanceledOnTouchOutside(false);

            customDialog.show();
        }
    };

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

        addCategoryB = root.findViewById(R.id.addCategoryButton);
        addCategoryB.setOnClickListener(onClickListener);

        return root;
    }
}