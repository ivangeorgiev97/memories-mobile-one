package uni.fmi.masters.memories.ui.memories;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Category;
import uni.fmi.masters.memories.entities.Memory;

public class MemoriesFragment extends Fragment {

    ListView memoriesLV;
    MemoriesAdapter memoriesAdapter;
    FloatingActionButton addMemoryB;
    Dialog customDialog;

    private MemoriesViewModel memoriesViewModel;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog = new Dialog(getContext());
            customDialog.setContentView(R.layout.add_edit_memory_dialog);

            final EditText memoryTitleET = customDialog.findViewById(R.id.memoryTitleEditText);
            final EditText memoryDescriptionET = customDialog.findViewById(R.id.memoriesDescriptionTextView);
            final Spinner memoryCategorySpinner =  customDialog.findViewById(R.id.categorySpinner);
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
                            // TODO - ADD MEMORY TO BACKEND LOGIC
                        }
                    }).start();
                }
            });

            customDialog.setTitle("Добави или редактирай спомен");
            customDialog.setCanceledOnTouchOutside(false);

            customDialog.show();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ArrayList<Memory> memories = new ArrayList<>();
        memories.add(new Memory(1, "Memory 1", "Description 1",
                new Category(1, "Category 1")));
        memories.add(new Memory(2, "Memory 2", "Description 2",
                new Category(2, "Category 2")));
        memories.add(new Memory(3, "Memory 3", "Description 3",
                new Category(2, "Category 2")));

        View root = inflater.inflate(R.layout.fragment_memories, container, false);
        memoriesLV = root.findViewById(R.id.memoriesListView);
        memoriesAdapter = new MemoriesAdapter(getContext(), R.layout.memories_list_row, memories);
        memoriesLV.setAdapter(memoriesAdapter);

        addMemoryB = root.findViewById(R.id.addMemoryButton);
        addMemoryB.setOnClickListener(onClickListener);

        memoriesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "delete logic here", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return root;
    }
}