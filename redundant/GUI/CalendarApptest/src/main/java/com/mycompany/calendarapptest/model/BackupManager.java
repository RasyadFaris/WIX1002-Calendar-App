package com.mycompany.calendarapptest.model;

import java.io.*;
import java.nio.file.*;

public class BackupManager {
    public static void backup(String destinationPath) throws IOException {
        File backupDir = new File(destinationPath);
        if (!backupDir.exists()) backupDir.mkdirs();
        
        Files.copy(Paths.get("data/event.csv"), Paths.get(destinationPath + "/event_backup.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get("data/recurrent.csv"), Paths.get(destinationPath + "/recurrent_backup.csv"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Backup completed to " + destinationPath);
    }

    public static void restore(String sourcePath) throws IOException {
        Files.copy(Paths.get(sourcePath + "/event_backup.csv"), Paths.get("data/event.csv"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get(sourcePath + "/recurrent_backup.csv"), Paths.get("data/recurrent.csv"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Restore completed from " + sourcePath);
    }
}