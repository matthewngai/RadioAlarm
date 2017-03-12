package com.example.matthew.morningvibe;

import android.os.Binder;

/**
 * Created by Matthew on 12/31/2015.
 */
    public class DefaultBinder extends Binder {
        StreamService s;
        public DefaultBinder( StreamService s) {
            this.s = s;
        }
        public StreamService getService() {
            return s;
        }
    }

