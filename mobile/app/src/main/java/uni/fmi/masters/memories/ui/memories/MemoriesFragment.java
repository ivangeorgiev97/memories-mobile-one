package uni.fmi.masters.memories.ui.memories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import uni.fmi.masters.memories.R;

public class MemoriesFragment extends Fragment {

    private MemoriesViewModel memoriesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        memoriesViewModel =
                new ViewModelProvider(this).get(MemoriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_memories, container, false);
        final TextView textView = root.findViewById(R.id.text_memories);
        memoriesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}