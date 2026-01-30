package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.ModerationResult;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MessageModerationService {

    // ✅ buraya kendi kelimelerini koy (küçük harf)
    private static final Set<String> BANNED = Set.of(
            "küfür1", "küfür2", "salak", "aptal"
    );

    private static final Pattern URL = Pattern.compile("(https?://|www\\.)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPEAT_CHAR = Pattern.compile("(.)\\1{6,}");
    private static final Pattern MANY_PUNCT = Pattern.compile("[!?.]{6,}");

    public ModerationResult check(String text) {
        if (text == null) return ModerationResult.ok();

        String raw = text.trim();
        if (raw.isEmpty()) return ModerationResult.ok();

        String norm = normalizeTR(raw);

        // 1) profanity dictionary
        List<String> matches = new ArrayList<>();
        for (String w : BANNED) {
            if (norm.contains(w)) matches.add(w);
        }
        if (!matches.isEmpty()) {
            return ModerationResult.block("PROFANITY", matches.size(), matches);
        }

        // 2) spam heuristics
        int urlCount = countMatches(URL, raw);
        if (urlCount >= 2) {
            return ModerationResult.block("SPAM_LINK", urlCount, List.of("urlCount=" + urlCount));
        }

        if (REPEAT_CHAR.matcher(raw).find() || MANY_PUNCT.matcher(raw).find()) {
            return ModerationResult.block("SPAM", 1, List.of("repeat/punct"));
        }

        if (raw.length() > 2000) {
            return ModerationResult.block("SPAM", 1, List.of("too_long"));
        }

        return ModerationResult.ok();
    }

    private int countMatches(Pattern p, String s) {
        int c = 0;
        var m = p.matcher(s);
        while (m.find()) c++;
        return c;
    }

    private String normalizeTR(String input) {
        String t = input.toLowerCase(new Locale("tr", "TR"));
        t = Normalizer.normalize(t, Normalizer.Form.NFKC);
        t = t.replaceAll("\\s+", " ").trim();
        return t;
    }
}
