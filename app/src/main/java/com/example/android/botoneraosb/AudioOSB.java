package com.example.android.botoneraosb;

/**
 * Created by Juancho on 31/5/2018.
 */

public class AudioOSB {
        /** El ID de del nombre del Nombre del Audio */
        private int mNombreDelAudioId;

        /** El ID de del nombre del Autor del Audio */
        private int mAutorDelAudioId;

        /** El ID de del Audio */
        private int mAudioId;

        /** El ID de de la imagen del Audio */
        private int mImagenId = NO_IMAGE_PROVIDED;

        /** Constant value that represents no image was provided for this word */
        private static final int NO_IMAGE_PROVIDED = -1;


        public AudioOSB(int nombreDelAudioId, int autorDelAudioId, int audioId) {
            mNombreDelAudioId = nombreDelAudioId;
            mAutorDelAudioId = autorDelAudioId;
            mAudioId = audioId;

        }


        public AudioOSB(int nombreDelAudioId,int autorDelAudioId ,int audioId, int imagenId) {
            mNombreDelAudioId = nombreDelAudioId;
            mAutorDelAudioId = autorDelAudioId ;
            mAudioId = audioId;
            mImagenId = imagenId;
        }

        /**
         * Get the string resource ID for the default translation of the word.
         */
        public int getNombreDelAudioId() {
            return mNombreDelAudioId;
        }

        /**
        * Get the string resource ID for the default translation of the word.
        */
        public int getAutorDelAudioId() {
        return mAutorDelAudioId;
    }

    /**
         * Get the string resource ID for the Miwok translation of the word.
         */
        public int getAudioId() {
            return mAudioId;
        }

        /**
         * Return the image resource ID of the word.
         */
        public int getImagenId() {
            return mImagenId;
        }

        /**
         * Returns whether or not there is an image for this word.
         */
        public boolean hasImage() {
            return mImagenId != NO_IMAGE_PROVIDED;
        }


}
