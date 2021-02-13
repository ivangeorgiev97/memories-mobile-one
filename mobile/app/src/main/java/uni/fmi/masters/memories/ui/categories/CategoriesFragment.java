package uni.fmi.masters.memories.ui.categories;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;
import uni.fmi.masters.memories.services.local.DBHelper;

public class CategoriesFragment extends Fragment {

    ListView categoriesLV;
    CategoriesAdapter categoriesAdapter;
    FloatingActionButton addCategoryB;
    Dialog customDialog;
    DBHelper dbHelper;
    List<Category> categories;

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
                            int id = dbHelper.getLastCategoryId() + 1;
                            String name = categoryNameET.getText().toString();

                            Category category = new Category(id, name);

                            if (dbHelper.addCategory(category)) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        categories.add(category);
                                        categoriesAdapter.notifyDataSetChanged();

                                        customDialog.hide();
                                    }
                                });
                            }
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
        dbHelper = new DBHelper(getContext());

        categories = dbHelper.getAllCategories();

        View root = inflater.inflate(R.layout.fragment_categories, container, false);
        categoriesLV = root.findViewById(R.id.categoriesListView);
        categoriesAdapter = new CategoriesAdapter(getContext(), R.layout.categories_list_row, categories);
        categoriesLV.setAdapter(categoriesAdapter);

        addCategoryB = root.findViewById(R.id.addCategoryButton);
        addCategoryB.setOnClickListener(onClickListener);

        categoriesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);

                customDialog = new Dialog(getContext());
                customDialog.setContentView(R.layout.add_edit_category_dialog);

                final EditText categoryNameET = customDialog.findViewById(R.id.categoryNameEditText);
                categoryNameET.setText(category.getName());

                Button okayB = customDialog.findViewById(R.id.okayButton);
                Button cancelB = customDialog.findViewById(R.id.cancelButton);
                Button deleteB = customDialog.findViewById(R.id.deleteButton);
                deleteB.setVisibility(View.VISIBLE);
                Button checkForChangesB = customDialog.findViewById(R.id.checkForChangesCategoriesButton);
                checkForChangesB.setVisibility(View.VISIBLE);

                checkForChangesB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO - ADD LOGIC FOR CHANGES
                    }
                });

                deleteB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper.removeCategory(category.getId());

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(int i = 0 ; i < categories.size(); i++){
                                            if(categories.get(i).getId() == category.getId()){
                                                categories.remove(i);
                                            }
                                        }

                                        categoriesAdapter.notifyDataSetChanged();

                                        deleteB.setVisibility(View.GONE);
                                        checkForChangesB.setVisibility(View.GONE);
                                        customDialog.hide();
                                    }
                                });

                            }
                        }).start();
                    }
                });

                cancelB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteB.setVisibility(View.GONE);
                        checkForChangesB.setVisibility(View.GONE);
                        customDialog.cancel();
                    }
                });

                okayB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                              category.setName(categoryNameET.getText().toString());

                                if (dbHelper.updateCategory(category)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            categories.set(position, category);
                                            categoriesAdapter.notifyDataSetChanged();

                                            deleteB.setVisibility(View.GONE);
                                            checkForChangesB.setVisibility(View.GONE);
                                            customDialog.hide();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });

                customDialog.setTitle("Обнови или изтрий категория");
                customDialog.setCanceledOnTouchOutside(false);

                customDialog.show();

                return false;
            }
        });

        return root;
    }
}