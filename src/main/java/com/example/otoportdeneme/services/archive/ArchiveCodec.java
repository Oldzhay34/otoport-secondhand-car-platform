package com.example.otoportdeneme.services.archive;

import com.example.otoportdeneme.Enums.SenderType;
import com.example.otoportdeneme.models.InquiryMessage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class ArchiveCodec {

    private ArchiveCodec() {}

    // JSONL satırı: {"t":"...","s":"C","sid":123,"c":"..."}
    public static byte[] encodeJsonlGzip(List<InquiryMessage> msgs) {
        StringBuilder sb = new StringBuilder(msgs.size() * 128);

        for (InquiryMessage m : msgs) {
            Instant t = m.getSentAt();
            SenderType st = m.getSenderType();

            Long senderId = null;
            if (st == SenderType.CLIENT && m.getClientSender() != null) senderId = m.getClientSender().getId();
            if (st == SenderType.STORE  && m.getStoreSender()  != null) senderId = m.getStoreSender().getId();

            sb.append("{\"t\":\"").append(t != null ? t.toString() : "").append("\"")
                    .append(",\"s\":\"").append(st == SenderType.CLIENT ? "C" : "S").append("\"")
                    .append(",\"sid\":").append(senderId != null ? senderId : "null")
                    .append(",\"c\":\"").append(escapeJson(m.getContent())).append("\"}")
                    .append("\n");
        }

        byte[] raw = sb.toString().getBytes(StandardCharsets.UTF_8);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gos = new GZIPOutputStream(bos)) {
            gos.write(raw);
            gos.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Archive encode failed", e);
        }
    }

    public static String decodeToJsonl(byte[] gz) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(gz);
             GZIPInputStream gis = new GZIPInputStream(bis);
             InputStreamReader isr = new InputStreamReader(gis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            throw new IllegalStateException("Archive decode failed", e);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\\' -> out.append("\\\\");
                case '"'  -> out.append("\\\"");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> out.append(ch);
            }
        }
        return out.toString();
    }
}
