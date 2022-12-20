package org.mirea.pm.notes_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.mirea.pm.notes_frontend.MainActivity;
import org.mirea.pm.notes_frontend.R;

import java.util.ArrayList;
import java.util.List;

import org.mirea.pm.notes_frontend.datamodels.NoteModel;

public class NoteListAdapter extends ArrayAdapter<NoteModel> {

    static class ViewHolder {
        private TextView annotation_text;
        private TextView creation_datetime_text;
        private ImageView menu_button;
    }

    private final Context context;
    private Filter filter;
    private final List<NoteModel> values;

    public NoteListAdapter(@NonNull Context context, List<NoteModel> values) {
        super(context, -1,  values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if(convertView == null) {
            mViewHolder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.note_element,
                    parent, false);

            mViewHolder.annotation_text =
                    convertView.findViewById(R.id.annotation_text);
            mViewHolder.creation_datetime_text =
                    convertView.findViewById(R.id.creation_datetime_text);
            mViewHolder.menu_button =
                    convertView.findViewById(R.id.menuButton);

            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.annotation_text.setText(values.get(position).getText());
        mViewHolder.creation_datetime_text.setText(
                values.get(position).getCreationTimeString(context.getResources().getString(R.string.datetime_format))
        );

        mViewHolder.menu_button.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.getMenuInflater().inflate(R.menu.menu_note_element,
                    popup.getMenu());
            popup.show();

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        ((MainActivity)context).deleteNote(values.get(position));
                    default:
                        break;
                }
                return true;
            });
        });

        return convertView;
    }

    public Filter getFilter() {
        if (filter == null)
            filter = new NoteFilter(values);
        return filter;
    }


    private class NoteFilter extends Filter {

        final private ArrayList<NoteModel> notes;

        public NoteFilter(List<NoteModel> objects) {
            notes = new ArrayList<>();
            synchronized (this) {
                notes.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence rawQuery) {
            String query = rawQuery.toString().toLowerCase();
            FilterResults result = new FilterResults();
            ArrayList<NoteModel> filtered = new ArrayList<>();

            for (NoteModel note : notes) {
                if (note.toString().toLowerCase().contains(query))
                    filtered.add(note);
            }
            result.count = filtered.size();
            result.values = filtered;
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            ArrayList<NoteModel> filtered = (ArrayList<NoteModel>) results.values;
            notifyDataSetChanged();
            clear();
            if(filtered != null) {
                for (int i = 0, l = filtered.size(); i < l; i++) {
                    add(filtered.get(i));
                }
            }
            notifyDataSetInvalidated();
        }
    }
}
