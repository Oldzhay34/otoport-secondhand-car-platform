package com.example.otoportdeneme.services;

import java.util.Locale;

public final class CatalogKey {
    public static String keyOf(String s){
        if (s == null) return "";
        return s.trim()
                .replaceAll("\\s+"," ")
                .toLowerCase(Locale.ROOT); // Locale.ROOT şart
    }
}
