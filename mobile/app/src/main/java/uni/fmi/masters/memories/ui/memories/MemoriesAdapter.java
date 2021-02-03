package uni.fmi.masters.memories.ui.memories;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import uni.fmi.masters.memories.entities.Memory;

public class MemoriesAdapter extends ArrayAdapter<Memory> {
    public MemoriesAdapter(@NonNull Context context, int resource, @NonNull List<Memory> objects) {
        super(context, resource, objects);
    }
}
