package com.example.android.botoneraosb;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    /** Handles playback of all the sound files */
    private MediaPlayer mMediaPlayer;

    /** Handles audio focus when playing a sound file */
    private AudioManager mAudioManager;

    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };

    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        // Create and setup the {@link AudioManager} to request audio focus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /*// ARMA ESTO EN OTRO LADO
        final ArrayList<AudioOSB> words = new ArrayList<AudioOSB>();

        // se podrian agregar audios de una lista que se componga sola
        words.add(new AudioOSB(R.string.almacen_la_guilla_apo,R.string.apo, R.raw.almacen_la_guilla_apo, R.drawable.aragon));
        words.add(new AudioOSB(R.string.aaaahh_apo,R.string.apo, R.raw.aaaahhh_apo, R.drawable.aragon));
        words.add(new AudioOSB(R.string.a_mi_no_me_importa_gato_guille,R.string.guille, R.raw.a_mi_no_me_importa_gato_guille, R.drawable.aragon));
        words.add(new AudioOSB(R.string.alto_domingo_guille,R.string.guille, R.raw.alto_domingo_guille, R.drawable.aragon));
        words.add(new AudioOSB(R.string.andate_a_cagar_simon_fede,R.string.fede, R.raw.andata_cagar_simon_fede, R.drawable.aragon));*/

        final ArrayList<AudioOSB> audios = armarListaDeAudios();


        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        AudioOSBAdapter adapter = new AudioOSBAdapter(this, audios, R.color.color_audio);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);

        // Set a click listener to play the audio when the list item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Release the media player if it currently exists because we are about to
                // play a different sound file
                releaseMediaPlayer();

                // Get the {@link Word} object at the given position the user clicked on
                AudioOSB word = audios.get(position);

                // Request audio focus so in order to play the audio file. The app needs to play a
                // short audio file, so we will request audio focus with a short amount of time
                // with AUDIOFOCUS_GAIN_TRANSIENT.
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // We have audio focus now.

                    // Create and setup the {@link MediaPlayer} for the audio resource associated
                    // with the current word
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, word.getAudioId());

                    // Start the audio file
                    mMediaPlayer.start();

                    // Setup a listener on the media player, so that we can stop and release the
                    // media player once the sound has finished playing.
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    private ArrayList<AudioOSB> armarListaDeAudios(){

        final ArrayList<AudioOSB> words = new ArrayList<AudioOSB>();

        // se podrian agregar audios de una lista que se componga sola
        words.add(new AudioOSB(R.string.almacen_la_guilla_apo,R.string.apo, R.raw.almacen_la_guilla_apo, R.drawable.apo));
        words.add(new AudioOSB(R.string.aaaahh_apo,R.string.apo, R.raw.aaaahhh_apo, R.drawable.apo));
        words.add(new AudioOSB(R.string.construcciones_la_checharda,R.string.apo, R.raw.construcciones_la_checharda_apo, R.drawable.apo));
        words.add(new AudioOSB(R.string.fideos_la_simona,R.string.apo, R.raw.fideos_la_simona_apo, R.drawable.apo));

        words.add(new AudioOSB(R.string.estamos_hablando_posta,R.string.bort, R.raw.estamos_hablando_posta_bort, R.drawable.bort));
        words.add(new AudioOSB(R.string.la_puta_que_te_pario_checho,R.string.bort, R.raw.la_puta_que_te_pario_checho_bort, R.drawable.bort));
        words.add(new AudioOSB(R.string.sale_hats,R.string.bort, R.raw.sale_hats_bort, R.drawable.bort));

        words.add(new AudioOSB(R.string.eskedesdekeee,R.string.chaca, R.raw.eskedesdekeee_chaca, R.drawable.chaca));
        words.add(new AudioOSB(R.string.veni_simon_veni,R.string.chaca, R.raw.veni_simon_veni_chaca, R.drawable.chaca));

        words.add(new AudioOSB(R.string.eh_puto,R.string.checho, R.raw.eh_puto_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.estoy_super_puta,R.string.checho, R.raw.estoy_super_puta_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.flashaba_la_realidad,R.string.checho, R.raw.flashaba_la_realidad_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.hola_soy_matias_canton,R.string.checho, R.raw.hola_soy_matias_canton_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.lavadero_artensanal,R.string.checho, R.raw.lavadero_artensanal_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.manejando_la_chata,R.string.checho, R.raw.manejando_la_chata_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.manguerear_las_plantas,R.string.checho, R.raw.manguerear_las_plantas_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.me_puse_a_pensar_en_eso,R.string.checho, R.raw.me_puse_a_pensar_en_eso_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.mostrale_la_pija,R.string.checho, R.raw.mostrale_la_pija_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.ogorek_factory_shop,R.string.checho, R.raw.ogorek_factory_shop_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.pasa_el_nombre_del_tema, R.string.checho, R.raw.pasa_el_nombre_del_tema_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.soy_cattaneo,R.string.checho, R.raw.soy_cattaneo_checho, R.drawable.checho));
        words.add(new AudioOSB(R.string.veni_a_visitarnos_simon,R.string.checho, R.raw.veni_a_visitarnos_simon_checho, R.drawable.checho));


        words.add(new AudioOSB(R.string.andate_a_cagar_simon_fede,R.string.fede, R.raw.andata_cagar_simon_fede, R.drawable.fede));
        words.add(new AudioOSB(R.string.con_el_auto_todo_cargado_de_madera,R.string.fede, R.raw.con_el_auto_todo_cargado_de_madera_fede, R.drawable.fede));
        words.add(new AudioOSB(R.string.cremas_hidratantes_barbaro,R.string.fede, R.raw.cremas_hidratantes_barbaro_fede, R.drawable.fede));
        words.add(new AudioOSB(R.string.esta_lleno_de_gordos_barriendo_la_calle,R.string.fede, R.raw.esta_lleno_de_gordos_barriendo_la_calle_fede, R.drawable.fede));
        words.add(new AudioOSB(R.string.mira_el_dia_que_es,R.string.fede, R.raw.mira_el_dia_que_es_fede, R.drawable.fede));
        words.add(new AudioOSB(R.string.te_va_a_atacar_la_manguera,R.string.fede, R.raw.te_va_a_atacar_la_manguera_fede, R.drawable.fede));


        words.add(new AudioOSB(R.string.a_mi_no_me_importa_gato_guille,R.string.guille, R.raw.a_mi_no_me_importa_gato_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.alto_domingo_guille,R.string.guille, R.raw.alto_domingo_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.dejen_trabajar,R.string.guille, R.raw.dejen_trabajar_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.feliz_navidad_muchachos,R.string.guille, R.raw.feliz_navidad_muchachos_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.fiesta,R.string.guille, R.raw.fiesta_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.la_concha_de_tu_hermana,R.string.guille, R.raw.la_concha_de_tu_hermana_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.laburar_un_sabado,R.string.guille, R.raw.laburar_un_sabado_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.mario_chupame_la_pija,R.string.guille, R.raw.mario_chupame_la_pija_guille, R.drawable.guille));
        words.add(new AudioOSB(R.string.nininini,R.string.guille, R.raw.nininini_guille, R.drawable.guille));

        words.add(new AudioOSB(R.string.pinto_el_cantito,R.string.mario, R.raw.pinto_el_cantito_mario, R.drawable.mario));

        words.add(new AudioOSB(R.string.fumigaciones_la_maurolea_1,R.string.simon, R.raw.fumigaciones_la_maurolea_1_simon, R.drawable.simon));
        words.add(new AudioOSB(R.string.fumigaciones_la_maurolea_2,R.string.simon, R.raw.fumigaciones_la_maurolea_2_simon, R.drawable.simon));
        words.add(new AudioOSB(R.string.no_me_gusta_la_fiesta,R.string.simon, R.raw.no_me_gusta_la_fiesta_simon, R.drawable.simon));
        words.add(new AudioOSB(R.string.se_puede_putear_tranquilo,R.string.simon, R.raw.se_puede_putear_tranquilo_simon, R.drawable.simon));
        words.add(new AudioOSB(R.string.veneno_para_aranias,R.string.simon, R.raw.veneno_para_aranias_simon, R.drawable.simon));


        return words;
    }
}
