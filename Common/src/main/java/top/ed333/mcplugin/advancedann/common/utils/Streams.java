package top.ed333.mcplugin.advancedann.common.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Streams {

    /////==== read START ====/////
    public static byte @NotNull [] read(@NotNull InputStream inputStream, int maxSize) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(maxSize);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, len);
        return outputStream.toByteArray();
    }

    public static byte @NotNull [] read(InputStream inputStream) throws IOException {
        return read(inputStream, inputStream.available());
    }

    @Contract("_, _ -> new")
    @Deprecated
    public static @NotNull String read(InputStream inputStream, String decoding) throws IOException {
        return new String(read(inputStream), decoding);
    }

    @Contract("_, _, _ -> new")
    @Deprecated
    public static @NotNull String read(InputStream inputStream, String decoding, int maxSize) throws IOException {
        return new String(read(inputStream, maxSize), decoding);
    }

    @Contract("_, _ -> new")
    public static @NotNull String read(InputStream inputStream, Charset charset) throws IOException {
        return new String(read(inputStream), charset);
    }
    ////==== read END ====/////

    ////==== save START ====/////
    public static void save(byte[] bytes, File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File cannot be null!");
        }
        FileOutputStream fOut = new FileOutputStream(file);
        if (bytes == null) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } else {
            fOut.write(bytes);
        }
        fOut.close();
    }

    @Deprecated
    public static void save(@NotNull String content, File dir, String decoding) throws IOException {
        save(content.getBytes(decoding), dir);
    }

    public static void save(@NotNull String content, File dir, Charset charset) throws IOException {
        save(content.getBytes(charset), dir);
    }

    public static void save(String content, File dir) throws IOException {
        save(content, dir, StandardCharsets.UTF_8);
    }

    @Deprecated
    public static void save(InputStream stream, String decoding, File dir) throws IOException {
        save(read(stream, decoding), dir, decoding);
    }
    ////==== save END ====/////
}
