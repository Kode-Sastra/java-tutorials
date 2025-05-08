package com.kodesastra.ai.tts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

public class FileWriterUtil {
    /**
     * Writes the given byte array to a file at the given path under the runtime classpath.
     *
     * @param content the file content
     * @param path relative path under the classpath root, e.g., "tts-output/twinkle.mp3"
     * @throws IOException if writing fails
     */
    public static void writeFile(byte[] content, Path path) throws IOException {
        // Find the classpath root (usually 'target/test-classes' during Maven build/run)
        URL classpathRoot = FileWriterUtil.class.getClassLoader().getResource("");

        File fullPath = new File(classpathRoot.getPath(), path.toString());

        // Ensure the parent directories exist
        fullPath.getParentFile().mkdirs();

        // Write the content to the file
        try (OutputStream outputStream = new FileOutputStream(fullPath)) {
            outputStream.write(content);
        }
    }

    /**
     * Reads the content of a text file at the given path.
     *
     * @param path the path to the text file
     * @return the content of the file as a String
     * @throws IOException if reading fails
     */
    public static String readFile(Path path) throws IOException {
        // Find the classpath root (usually 'target/test-classes' during Maven build/run)
        URL classpathRoot = FileWriterUtil.class.getClassLoader().getResource("");

        File fullPath = new File(classpathRoot.getPath(), path.toString());
        String text;
        try(FileInputStream fileInputStream = new FileInputStream(fullPath)) {
            byte[] bytes = fileInputStream.readAllBytes();
            text = new String(bytes);
        }
        return text;
    }
}