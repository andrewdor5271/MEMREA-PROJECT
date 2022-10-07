package org.mirea.pm.notes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.mirea.pm.notes.R;

import java.text.SimpleDateFormat;

public class NoteListAdapter extends ArrayAdapter<NoteModel> {

    static class ViewHolder {
        private TextView annotation_text;
        private TextView creation_datetime_text;
    }

    private final Context context;
    private final NoteModel[] values;

    public NoteListAdapter(@NonNull Context context, NoteModel[] values) {
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

        mViewHolder.annotation_text.setText(values[position].getText());
        mViewHolder.creation_datetime_text.setText(
                dFormat.format(values[position].getCreationTime())
        );

        return convertView;
    }
}
