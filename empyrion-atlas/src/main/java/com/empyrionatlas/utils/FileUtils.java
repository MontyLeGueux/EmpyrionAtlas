package com.empyrionatlas.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileUtils {
	
	private static final String RE_FOLDER = "REFiles/";
	
	public static void resetREFolder() {
        Path rePath = Paths.get(RE_FOLDER);
        try {
            if (Files.exists(rePath)) {
                deleteFolder(rePath);
            }
            Files.createDirectories(rePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to reset temp folder", e);
        }
    }
	 
	private static void deleteFolder(Path folderPath) throws IOException {
        if (Files.exists(folderPath)) {
            try (Stream<Path> paths = Files.walk(folderPath)) {
                paths.sorted((a, b) -> b.compareTo(a))
                      .forEach(FileUtils::deleteFile);
            }
        }
	}
	 
	 private static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.println("Failed to delete: " + path + " - " + e.getMessage());
        }
    }
}
