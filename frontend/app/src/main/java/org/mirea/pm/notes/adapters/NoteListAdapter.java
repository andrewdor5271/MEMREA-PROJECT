package org.mirea.pm.notes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.mirea.pm.notes.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends ArrayAdapter<NoteModel> {

    static class ViewHolder {
        private TextView annotation_text;
        private TextView creation_datetime_text;
    }

    private final Context context;
    private Filter filter;
    private final ArrayList<NoteModel> values;

    public NoteListAdapter(@NonNull Context context, ArrayList<NoteModel> values) {
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

            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dFormat = new SimpleDateFormat(
                context.getResources().getString(R.string.datetime_format)
        );

        mViewHolder.annotation_text.setText(values.get(position).getText());
        mViewHolder.creation_datetime_text.setText(
                dFormat.format(values.get(position).getCreationTime())
        );

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
            if (query.length() > 0) {
                ArrayList<NoteModel> filtered = new ArrayList<>();

                for (NoteModel note : notes) {
                    if (note.toString().toLowerCase().contains(query))
                        filtered.add(note);
                }
                result.count = filtered.size();
                result.values = filtered;
            }
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
