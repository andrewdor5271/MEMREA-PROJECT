package org.mirea.pm.notes_frontend.util_storage;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JwtStorage {
    private static final String FILENAME = "jwt.txt";

    public static void save(Context context, String token) {
        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(token.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String retrieve(Context context) {
        try (FileInputStream fis = context.openFileInput((FILENAME))) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(fis, StandardCharsets.UTF_8));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
