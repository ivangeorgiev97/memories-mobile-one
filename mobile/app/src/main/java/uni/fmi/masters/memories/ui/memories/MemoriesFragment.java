package uni.fmi.masters.memories.ui.memories;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;
import uni.fmi.masters.memories.entities.Memory;
import uni.fmi.masters.memories.services.local.DBHelper;

public class MemoriesFragment extends Fragment {

    ListView memoriesLV;
    MemoriesAdapter memoriesAdapter;
    FloatingActionButton addMemoryB;
    Dialog customDialog;
    DBHelper dbHelper;
    List<Memory> memories;
    List<Category> categories;
    List<String> categoryNamesList;
    String[] categoryNames;
    List<Integer> categoryIdsList;
    Integer[] categoryIds;

    private MemoriesViewModel memoriesViewModel;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog = new Dialog(getContext());
            customDialog.setContentView(R.layout.add_edit_memory_dialog);

            final EditText memoryTitleET = customDialog.findViewById(R.id.memoryTitleEditText);
            final EditText memoryDescriptionET = customDialog.findViewById(R.id.memoryDescriptionMultilineEditText);

            Spinner memoryCategorySpinner = customDialog.findViewById(R.id.categorySpinner);
            ArrayAdapter<String> memoryCategorySpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.categories_spinner_row, categoryNames);
            memoryCategorySpinner.setAdapter(memoryCategorySpinnerAdapter);

            Button okayB = customDialog.findViewById(R.id.memoryOkayButton);
            Button cancelB = customDialog.findViewById(R.id.memoryCancelButton);

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
                            int id = dbHelper.getLastMemoryId() + 1;
                            String title = memoryTitleET.getText().toString();
                            String description = memoryDescriptionET.getText().toString();
                            int categoryId = categoryIds[memoryCategorySpinner.getSelectedItemPosition()];
                            Category category = dbHelper.getCategoryById(categoryId);

                            Memory memory = new Memory(id, title, description, categoryId, category);

                            if (dbHelper.addMemory(memory)) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        memories.add(memory);
                                        memoriesAdapter.notifyDataSetChanged();

                                        customDialog.hide();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });

            customDialog.setTitle("Добави спомен");
            customDialog.setCanceledOnTouchOutside(false);

            customDialog.show();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbHelper = new DBHelper(getContext());
        categoryNamesList = new ArrayList<String>();
        categoryIdsList = new ArrayList<Integer>();
        categories = dbHelper.getAllCategories();
        memories = dbHelper.getAllMemories();

        for (int i = 0; i < categories.size(); i++) {
            categoryNamesList.add(categories.get(i).getName());
            categoryIdsList.add(categories.get(i).getId());
        }

        categoryNames = new String[categoryNamesList.size()];
        categoryNames = categoryNamesList.toArray(categoryNames);

        categoryIds = new Integer[categoryIdsList.size()];
        categoryIds = categoryIdsList.toArray(categoryIds);

        View root = inflater.inflate(R.layout.fragment_memories, container, false);
        memoriesLV = root.findViewById(R.id.memoriesListView);
        memoriesAdapter = new MemoriesAdapter(getContext(), R.layout.memories_list_row, memories);
        memoriesLV.setAdapter(memoriesAdapter);

        addMemoryB = root.findViewById(R.id.addMemoryButton);
        addMemoryB.setOnClickListener(onClickListener);

        memoriesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Memory memory = (Memory) parent.getItemAtPosition(position);

                customDialog = new Dialog(getContext());
                customDialog.setContentView(R.layout.add_edit_memory_dialog);

                final EditText memoryTitleET = customDialog.findViewById(R.id.memoryTitleEditText);
                memoryTitleET.setText(memory.getTitle());
                final EditText memoryDescriptionET = customDialog.findViewById(R.id.memoryDescriptionMultilineEditText);
                memoryDescriptionET.setText(memory.getDescription());

                Spinner memoryCategorySpinner = customDialog.findViewById(R.id.categorySpinner);
                ArrayAdapter<String> memoryCategorySpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.categories_spinner_row, categoryNames);
                memoryCategorySpinner.setAdapter(memoryCategorySpinnerAdapter);

                String categoryName = dbHelper.getCategoryById(memory.getCategoryId()).getName();
                for (int i = 0; i < categoryNames.length; i++) {
                    if (categoryNames[i].equals(categoryName)) {
                        memoryCategorySpinner.setSelection(i);
                        break;
                    }
                }


                Button okayB = customDialog.findViewById(R.id.memoryOkayButton);
                Button cancelB = customDialog.findViewById(R.id.memoryCancelButton);
                Button deleteB = customDialog.findViewById(R.id.memoryDeleteButton);
                deleteB.setVisibility(View.VISIBLE);
                Button checkForChangesB = customDialog.findViewById(R.id.checkForMemoryChangesButton);
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
                                dbHelper.removeMemory(memory.getId());

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(int i = 0 ; i < memories.size(); i++){
                                            if(memories.get(i).getId() == memory.getId()){
                                                memories.remove(i);
                                            }
                                        }

                                        memoriesAdapter.notifyDataSetChanged();

                                        checkForChangesB.setVisibility(View.GONE);
                                        deleteB.setVisibility(View.GONE);
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
                        checkForChangesB.setVisibility(View.GONE);
                        deleteB.setVisibility(View.GONE);
                        customDialog.cancel();
                    }
                });

                okayB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String title = memoryTitleET.getText().toString();
                                String description = memoryDescriptionET.getText().toString();
                                int categoryId = categoryIds[memoryCategorySpinner.getSelectedItemPosition()];
                                Category category = dbHelper.getCategoryById(categoryId);

                                memory.setTitle(title);
                                memory.setDescription(description);
                                memory.setCategoryId(categoryId);
                                memory.setCategory(category);

                                if (dbHelper.updateMemory(memory)) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            memories.set(position, memory);
                                            memoriesAdapter.notifyDataSetChanged();

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

                customDialog.setTitle("Обнови или изтрий спомен");
                customDialog.setCanceledOnTouchOutside(false);

                customDialog.show();

                return false;
            }
        });

        return root;
    }
}