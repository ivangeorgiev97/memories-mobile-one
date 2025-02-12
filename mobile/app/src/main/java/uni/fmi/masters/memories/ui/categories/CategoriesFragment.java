package uni.fmi.masters.memories.ui.categories;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;
import uni.fmi.masters.memories.services.external.CategoryService;
import uni.fmi.masters.memories.services.external.ClientUtils;
import uni.fmi.masters.memories.services.local.DBHelper;

public class CategoriesFragment extends Fragment {

    ListView categoriesLV;
    CategoriesAdapter categoriesAdapter;
    FloatingActionButton addCategoryB;
    FloatingActionButton syncCategoriesB;
    Dialog customDialog;
    DBHelper dbHelper;
    List<Category> categories;
    CategoryService categoryService;
    List<Category> apiCategories;

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
                            // OLD WAY
                            // int id = dbHelper.getLastCategoryId() + 1;
                            String name = categoryNameET.getText().toString();

                            Category category = new Category(name);

                            Call<Category> addCallWithoutId = categoryService.addCategoryWithoutId(category.getName());
                            addCallWithoutId.enqueue(new Callback<Category>() {
                                @Override
                                public void onResponse(Call<Category> call, Response<Category> response) {
                                    if (response.isSuccessful()) {
                                        category.setId(response.body().getId());

                                        if (dbHelper.addCategory(category)) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    categories.add(category);
                                                    categoriesAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }

                                    customDialog.hide();
                                }

                                @Override
                                public void onFailure(Call<Category> call, Throwable t) {
                                    Log.e("ERROR: ", t.getMessage());
                                }
                            });
                        }
                    }).start();
                }
            });

            customDialog.setTitle("Добави категория");
            customDialog.setCanceledOnTouchOutside(false);

            customDialog.show();
        }
    };

    private View.OnClickListener onSyncClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Call<List<Category>> call = categoryService.getAllCategories();
            call.enqueue(new Callback<List<Category>>() {
                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                    if (response.isSuccessful()) {
                        apiCategories = response.body();
                        for (int i = 0; i < apiCategories.size(); i++) {
                            dbHelper.addOrUpdateCategory(apiCategories.get(i));
                            Category previousCategory = findCategoryById(apiCategories.get(i).getId(), categories);

                            if (previousCategory != null)
                                categories.remove(previousCategory);

                            categories.add(apiCategories.get(i));
                        }

                        categoriesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                }
            });
        }
    };

    private Category findCategoryById(int id, List<Category> categories) {
        for (Category category : categories) {
            if (category.getId() == id) {
                return category;
            }
        }

        return null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbHelper = new DBHelper(getContext());

        apiCategories = new ArrayList<Category>();
        categoryService = ClientUtils.getCategoryService();

        categories = dbHelper.getAllCategories();

        View root = inflater.inflate(R.layout.fragment_categories, container, false);
        categoriesLV = root.findViewById(R.id.categoriesListView);
        categoriesAdapter = new CategoriesAdapter(getContext(), R.layout.categories_list_row, categories);
        categoriesLV.setAdapter(categoriesAdapter);

        addCategoryB = root.findViewById(R.id.addCategoryButton);
        addCategoryB.setOnClickListener(onClickListener);

        syncCategoriesB = root.findViewById(R.id.syncCategoriesButton);
        syncCategoriesB.setOnClickListener(onSyncClickListener);

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

                TextView noChangesTV = customDialog.findViewById(R.id.noChangesCategoryTextView);
                Button doNotDoNothingB = customDialog.findViewById(R.id.doNotDoNothingCategoryButton);
                Button updateB = customDialog.findViewById(R.id.updateCategoryButton);
                Button duplicateB = customDialog.findViewById(R.id.duplicateCategoryButton);

                checkForChangesB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Category oldCategory = new Category(category);
                        category.setName(categoryNameET.getText().toString());

                        Call<Category> call = categoryService.getCategoryById(category.getId());
                        call.enqueue(new Callback<Category>() {
                            @Override
                            public void onResponse(Call<Category> call, Response<Category> response) {
                                if (response.isSuccessful() && !response.body().getName().equals(category.getName())) {
                                    noChangesTV.setVisibility(View.GONE);
                                    doNotDoNothingB.setVisibility(View.VISIBLE);
                                    updateB.setVisibility(View.VISIBLE);
                                    duplicateB.setVisibility(View.VISIBLE);

                                    doNotDoNothingB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            doNotDoNothingB.setVisibility(View.GONE);
                                            updateB.setVisibility(View.GONE);
                                            duplicateB.setVisibility(View.GONE);
                                            deleteB.setVisibility(View.GONE);
                                            checkForChangesB.setVisibility(View.GONE);

                                            customDialog.hide();
                                        }
                                    });

                                    updateB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            category.setName(categoryNameET.getText().toString());
                                            Call<Category> updateCall = categoryService.updateCategory(category.getId(), category.getName());
                                            updateCall.enqueue(new Callback<Category>() {
                                                @Override
                                                public void onResponse(Call<Category> call, Response<Category> response) {
                                                    if (response.isSuccessful()) {
                                                        if (dbHelper.updateCategory(category)) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    categories.set(position, category);
                                                                    categoriesAdapter.notifyDataSetChanged();

                                                                    doNotDoNothingB.setVisibility(View.GONE);
                                                                    updateB.setVisibility(View.GONE);
                                                                    duplicateB.setVisibility(View.GONE);
                                                                    deleteB.setVisibility(View.GONE);
                                                                    checkForChangesB.setVisibility(View.GONE);
                                                                    customDialog.hide();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Category> call, Throwable t) {
                                                    Log.e("ERROR: ", t.getMessage());
                                                }
                                            });
                                        }
                                    });

                                    duplicateB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            category.setName(categoryNameET.getText().toString());

                                            Call<Category> updateCall = categoryService.updateCategory(category.getId(), category.getName());
                                            updateCall.enqueue(new Callback<Category>() {
                                                @Override
                                                public void onResponse(Call<Category> call, Response<Category> response) {
                                                    if (response.isSuccessful()) {
                                                        if (dbHelper.updateCategory(category)) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    categories.set(position, category);
                                                                    categoriesAdapter.notifyDataSetChanged();

                                                                    oldCategory.setId(dbHelper.getLastCategoryId() + 1);

                                                                    if (dbHelper.addCategory(oldCategory)) {
                                                                        Call<Category> addCall = categoryService.addCategory(oldCategory.getId(), oldCategory.getName());
                                                                        addCall.enqueue(new Callback<Category>() {
                                                                            @Override
                                                                            public void onResponse(Call<Category> call, Response<Category> response) {
                                                                                if (response.isSuccessful()) {
                                                                                    categories.add(oldCategory);
                                                                                    categoriesAdapter.notifyDataSetChanged();
                                                                                }

                                                                                doNotDoNothingB.setVisibility(View.GONE);
                                                                                updateB.setVisibility(View.GONE);
                                                                                duplicateB.setVisibility(View.GONE);
                                                                                deleteB.setVisibility(View.GONE);
                                                                                checkForChangesB.setVisibility(View.GONE);
                                                                                customDialog.hide();
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<Category> call, Throwable t) {
                                                                                Log.e("ERROR: ", t.getMessage());
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Category> call, Throwable t) {
                                                    Log.e("ERROR: ", t.getMessage());
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    noChangesTV.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(Call<Category> call, Throwable t) {
                                Log.e("ERROR: ", t.getMessage());
                                noChangesTV.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

                deleteB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Call<Category> deleteCall = categoryService.deleteCategory(category.getId());
                                deleteCall.enqueue(new Callback<Category>() {
                                    @Override
                                    public void onResponse(Call<Category> call, Response<Category> response) {
                                        if (response.isSuccessful()) {
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
                                                }
                                            });
                                        }

                                        deleteB.setVisibility(View.GONE);
                                        checkForChangesB.setVisibility(View.GONE);

                                        customDialog.hide();
                                    }

                                    @Override
                                    public void onFailure(Call<Category> call, Throwable t) {
                                        Log.e("ERROR: ", t.getMessage());
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