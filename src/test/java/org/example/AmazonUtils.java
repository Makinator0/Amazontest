package org.example;

public class AmazonUtils {
    public static String extractAsinFromUrl(String url) {
        String[] parts = url.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("dp") && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
