package service;

import java.io.*;
import java.nio.file.*;

public class BackupService {
    public static void backup(String destinationPath) throws IOException {
        File backupDirectory = new File(destinationPath);
        if (!backupDirectory.exists()){
            backupDirectory.mkdirs();
        }

        Files.copy(Paths.get("data/event.csv"), Paths.get(destinationPath + "/event_backup.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get("data/recurrent.csv"), Paths.get(destinationPath + "/recurrent_backup.csv"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Backup completed to " + destinationPath);
    }

    public static void restore(String sourcePath) throws IOException {
        Files.copy(Paths.get(sourcePath + "/event_backup.csv"), Paths.get("data/event.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get(sourcePath + "/recurrent.csv"), Paths.get("data/recurrent.csv"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Restore completed from " + sourcePath);
    }
}