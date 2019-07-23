package com.planetwalk.ponion;

import com.google.gson.Gson;

public class Gsons {

    private static ThreadLocal<Gson> gsons = new ThreadLocal<Gson>();

    public static Gson value() {
        if (gsons.get() == null) {
            gsons.set(new Gson());
        }
        return gsons.get();
    }
}
