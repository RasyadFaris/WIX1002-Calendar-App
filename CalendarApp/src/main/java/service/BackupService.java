package service;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class BackupService {
    
    // combine these files into one
    private static final String[] TARGET_FILES = {"data/event.csv", "data/recurrent.csv"};

    public static void backup(String destinationFolder) throws IOException {
        File backupDir = new File(destinationFolder);
        if (!backupDir.exists()) backupDir.mkdirs();

        // One file for everything
        File backupFile = new File(destinationFolder + "/calendar_full_backup.txt");
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(backupFile))) {
            for (String filePath : TARGET_FILES) {
                File src = new File(filePath);
                if (src.exists()) {
                    pw.println("---START:" + src.getName() + "---");
                    
                    List<String> lines = Files.readAllLines(src.toPath());
                    for (String line : lines) pw.println(line);
                    
                    pw.println("---END:" + src.getName() + "---");
                }
            }
        }
        System.out.println("Backup saved to " + backupFile.getAbsolutePath());
    }

    public static void restore(String sourceFolder) throws IOException {
        File backupFile = new File(sourceFolder + "/calendar_full_backup.txt");
        if (!backupFile.exists()) throw new FileNotFoundException("Backup file 'calendar_full_backup.txt' not found in " + sourceFolder);

        try (BufferedReader br = new BufferedReader(new FileReader(backupFile))) {
            String line;
            PrintWriter currentWriter = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("---START:")) {
                    String filename = line.substring(9, line.length() - 3); // extract filename
                    File target = new File("data/" + filename);
                    if (target.getParentFile() != null) target.getParentFile().mkdirs();
                    currentWriter = new PrintWriter(new FileWriter(target));
                } else if (line.startsWith("---END:")) {
                    if (currentWriter != null) {
                        currentWriter.close();
                        currentWriter = null;
                    }
                } else {
                    if (currentWriter != null) {
                        currentWriter.println(line);
                    }
                }
            }
        }
        System.out.println("Restore completed.");
    }
}