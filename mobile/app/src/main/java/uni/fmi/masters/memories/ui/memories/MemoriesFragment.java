package uni.fmi.masters.memories.ui.memories;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;
import uni.fmi.masters.memories.entities.Memory;
import uni.fmi.masters.memories.services.external.ClientUtils;
import uni.fmi.masters.memories.services.external.MemoryService;
import uni.fmi.masters.memories.services.local.DBHelper;

public class MemoriesFragment extends Fragment {

    ListView memoriesLV;
    MemoriesAdapter memoriesAdapter;
    FloatingActionButton addMemoryB;
    FloatingActionButton syncMemoriesB;
    Dialog customDialog;
    DBHelper dbHelper;
    List<Memory> memories;
    List<Category> categories;
    List<String> categoryNamesList;
    String[] categoryNames;
    List<Integer> categoryIdsList;
    Integer[] categoryIds;
    MemoryService memoryService;
    List<Memory> apiMemories;

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

    private View.OnClickListener onSyncMemoriesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Call<List<Memory>> call = memoryService.getAllMemories();
            call.enqueue(new Callback<List<Memory>>() {
                @Override
                public void onResponse(Call<List<Memory>> call, Response<List<Memory>> response) {
                    if (response.isSuccessful()) {
                        apiMemories = response.body();
                        for (int i = 0; i < apiMemories.size(); i++) {
                            dbHelper.addOrUpdateMemory(apiMemories.get(i));
                            Memory previousMemory = findMemoryById(apiMemories.get(i).getId(), memories);

                            if (previousMemory != null)
                                memories.remove(previousMemory);

                            memories.add(apiMemories.get(i));
                        }

                        memoriesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Memory>> call, Throwable t) {
                    Log.e("ERROR: ", t.getMessage());
                }
            });
        }
    };

    private Memory findMemoryById(int id, List<Memory> memories) {
        for (Memory memory : memories) {
            if (memory.getId() == id) {
                return memory;
            }
        }

        return null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbHelper = new DBHelper(getContext());

        apiMemories = new ArrayList<Memory>();
        memoryService = ClientUtils.getMemoryService();

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

        syncMemoriesB = root.findViewById(R.id.syncMemoriesButton);
        syncMemoriesB.setOnClickListener(onSyncMemoriesClickListener);

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

                TextView noChangesTV = customDialog.findViewById(R.id.noChangesMemoryTextView);
                Button doNotDoNothingB = customDialog.findViewById(R.id.doNotDoNothingMemoryButton);
                Button updateB = customDialog.findViewById(R.id.updateMemoryButton);
                Button duplicateB = customDialog.findViewById(R.id.duplicateMemoryButton);

                checkForChangesB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Memory oldMemory = new Memory(memory);
                        memory.setTitle(memoryTitleET.getText().toString());
                        memory.setDescription(memoryDescriptionET.getText().toString());
                        memory.setCategoryId(categoryIds[memoryCategorySpinner.getSelectedItemPosition()]);
                        memory.setCategory(dbHelper.getCategoryById(memory.getCategoryId()));

                        Call<Memory> call = memoryService.getMemoryById(memory.getId());
                        call.enqueue(new Callback<Memory>() {
                            @Override
                            public void onResponse(Call<Memory> call, Response<Memory> response) {
                                if (response.isSuccessful() &&
                                        (
                                            !response.body().getTitle().equals(memory.getTitle()) ||
                                            !response.body().getDescription().equals(memory.getDescription()) ||
                                            response.body().getCategoryId() != memory.getCategoryId()
                                        )) {
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
                                            memory.setTitle(memoryTitleET.getText().toString());
                                            memory.setDescription(memoryDescriptionET.getText().toString());
                                            memory.setCategoryId(categoryIds[memoryCategorySpinner.getSelectedItemPosition()]);
                                            memory.setCategory(dbHelper.getCategoryById(memory.getCategoryId()));
                                            Call<Memory> updateCall = memoryService.updateMemory(memory.getId(), memory.getTitle(), memory.getDescription(), memory.getCategoryId());
                                            updateCall.enqueue(new Callback<Memory>() {
                                                @Override
                                                public void onResponse(Call<Memory> call, Response<Memory> response) {
                                                    if (response.isSuccessful()) {
                                                        if (dbHelper.updateMemory(memory)) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    memories.set(position, memory);
                                                                    memoriesAdapter.notifyDataSetChanged();

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
                                                public void onFailure(Call<Memory> call, Throwable t) {
                                                    Log.e("ERROR: ", t.getMessage());
                                                }
                                            });
                                        }
                                    });

                                    duplicateB.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            memory.setTitle(memoryTitleET.getText().toString());
                                            memory.setDescription(memoryDescriptionET.getText().toString());
                                            memory.setCategoryId(categoryIds[memoryCategorySpinner.getSelectedItemPosition()]);
                                            memory.setCategory(dbHelper.getCategoryById(memory.getCategoryId()));

                                            Call<Memory> updateCall = memoryService.updateMemory(memory.getId(), memory.getTitle(), memory.getDescription(), memory.getCategoryId());
                                            updateCall.enqueue(new Callback<Memory>() {
                                                @Override
                                                public void onResponse(Call<Memory> call, Response<Memory> response) {
                                                    if (response.isSuccessful()) {
                                                        if (dbHelper.updateMemory(memory)) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    memories.set(position, memory);
                                                                    memoriesAdapter.notifyDataSetChanged();

                                                                    oldMemory.setId(dbHelper.getLastMemoryId() + 1);

                                                                    if (dbHelper.addMemory(oldMemory)) {
                                                                        Call<Memory> addCall = memoryService.addMemory(oldMemory.getId(), oldMemory.getTitle(), oldMemory.getDescription(), oldMemory.getCategoryId());
                                                                        addCall.enqueue(new Callback<Memory>() {
                                                                            @Override
                                                                            public void onResponse(Call<Memory> call, Response<Memory> response) {
                                                                                if (response.isSuccessful()) {
                                                                                    memories.add(oldMemory);
                                                                                    memoriesAdapter.notifyDataSetChanged();
                                                                                }

                                                                                doNotDoNothingB.setVisibility(View.GONE);
                                                                                updateB.setVisibility(View.GONE);
                                                                                duplicateB.setVisibility(View.GONE);
                                                                                deleteB.setVisibility(View.GONE);
                                                                                checkForChangesB.setVisibility(View.GONE);

                                                                                customDialog.hide();
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<Memory> call, Throwable t) {
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
                                                public void onFailure(Call<Memory> call, Throwable t) {
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
                            public void onFailure(Call<Memory> call, Throwable t) {
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