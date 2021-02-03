package uni.fmi.masters.memories.ui.categories;

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
import uni.fmi.masters.memories.entities.Category;

public class CategoriesAdapter extends ArrayAdapter<Category> {

    public CategoriesAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((MainMenuActivity)getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.categories_list_row, parent, false);
        }

        TextView idTV = convertView.findViewById(R.id.idTextView);
        TextView nameTV = convertView.findViewById(R.id.nameTextView);

        idTV.setText(String.valueOf(getItem(position).getId()));
        nameTV.setText(getItem(position).getName());

        return convertView;
    }
}
