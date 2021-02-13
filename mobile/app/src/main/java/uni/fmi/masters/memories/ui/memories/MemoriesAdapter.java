package uni.fmi.masters.memories.ui.memories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import uni.fmi.masters.memories.MainMenuActivity;
import uni.fmi.masters.memories.R;
import uni.fmi.masters.memories.entities.Memory;
import uni.fmi.masters.memories.services.local.DBHelper;

public class MemoriesAdapter extends ArrayAdapter<Memory> {
    DBHelper dbHelper;

    public MemoriesAdapter(@NonNull Context context, int resource, @NonNull List<Memory> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((MainMenuActivity)getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.memories_list_row, parent, false);
        }

        TextView idTV = convertView.findViewById(R.id.memoriesIdTextView);
        TextView titleTV = convertView.findViewById(R.id.memoriesTitleTextView);
        TextView categoryTV = convertView.findViewById(R.id.memoriesCategoryTextView);
        TextView descriptionTV = convertView.findViewById(R.id.memoriesDescriptionTextView);

        dbHelper = new DBHelper(getContext());

        idTV.setText(String.valueOf(getItem(position).getId()));
        titleTV.setText(getItem(position).getTitle());
        String categoryName = dbHelper.getCategoryById(getItem(position).getCategoryId()).getName();
        categoryTV.setText(categoryName);
        descriptionTV.setText(getItem(position).getDescription());

        return convertView;
    }
}
