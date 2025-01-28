package org.live_server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class FileUtil {

    private FileUtil() {
    }

    // This method returns all possible path (folder,file) ConcurrentHashMap
    public static ConcurrentHashMap<String, Path> generatePath(Path rootPath, String rootFolderText) {

        ConcurrentHashMap<String, Path> pathConcurrentHashMap = new ConcurrentHashMap<>();

        // Walk through the rootPath and find all available folder and files
        try (Stream<Path> pathStream = Files.walk(rootPath)) {
            pathStream.toList().forEach(path -> {

                // Do not include the root folder
                if (path.toString().endsWith(rootFolderText)) {
                    return;
                }

                String actualPath = getActualPath(path, rootFolderText);
                if (!pathConcurrentHashMap.containsKey(actualPath)) {

                    // As we are not showing folders, Do not include folders in the ConcurrentHashMap
                    if (actualPath.contains(".")) {
                        pathConcurrentHashMap.put(actualPath, path);
                    }
                }
            });
            return pathConcurrentHashMap;
        } catch (Exception ex) {
            return new ConcurrentHashMap<>();
        }
    }

    public static String getActualPath(Path path, String rootFolderText) {

        // Get the root folder index from the full path
        int rootFolderIndex = path.toString().indexOf(rootFolderText);

        // Get the path next to root path
        String nextToRootFolder = path.toString()
                .substring(rootFolderIndex + (rootFolderText.length()) + 1);
        String[] actualFolderPath;

        if (SystemUtil.OS.toLowerCase().contains("windows")) {
            actualFolderPath = nextToRootFolder.split("\\\\");
        } else {
            actualFolderPath = nextToRootFolder.split("/");
        }

        // Build path
        StringBuilder pathBuilder = new StringBuilder();
        for (String s : actualFolderPath) {
            pathBuilder.append("/")
                    .append(s);
        }

        return pathBuilder.toString().replaceFirst("/", "");
    }
}
