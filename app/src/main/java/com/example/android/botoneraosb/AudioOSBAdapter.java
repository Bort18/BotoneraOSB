package com.example.android.botoneraosb;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Juancho on 31/5/2018.
 */

public class AudioOSBAdapter extends ArrayAdapter<AudioOSB> {
    private int mColorResourceId;

    public AudioOSBAdapter(Activity context, ArrayList<AudioOSB> AudioOSB, int color){
        super(context, 0, AudioOSB);
        mColorResourceId = color;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.word_list_views, parent, false);
        }

        AudioOSB audio = getItem(position);


        TextView nombreDelAudioTextView = (TextView) listItemView.findViewById(R.id.nombre_audio);
        nombreDelAudioTextView.setText(audio.getNombreDelAudioId());

        TextView autorDelAudioTextView = (TextView) listItemView.findViewById(R.id.autor_audio);
        autorDelAudioTextView.setText(audio.getAutorDelAudioId());


        ImageView iconView = (ImageView) listItemView.findViewById(R.id.imagen);
        if (audio.hasImage()) {
            iconView.setImageResource(audio.getImagenId());
        } else {
            iconView.setVisibility(View.GONE);
        }

        View textContainer = listItemView.findViewById(R.id.text_container);
        int color = ContextCompat.getColor(getContext(), mColorResourceId);
        textContainer.setBackgroundColor(color);

        return  listItemView;




    }
}
