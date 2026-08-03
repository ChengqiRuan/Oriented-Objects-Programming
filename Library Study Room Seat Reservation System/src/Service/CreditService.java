package Service;

import users.User;
import users.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Student Credit Score Management Service
 * Data storage: credits.txt
 * Line format: username,score
 *
 * Design Note: Since the existing FileIO class cannot be modified, credit scores are persisted separately in credits.txt.
 * When the system starts, {@link #applyTo(List)} is invoked to load scores from the file into corresponding Student objects.
 */
public class CreditService {
    private static final String FILE = "credits.txt";

    private final Map<String, Integer> scoreMap = new HashMap<>();

    public CreditService() {
        load();
    }

    /** Apply credit scores from the file to student objects on startup */
    public void applyTo(List<User> users) {
        for (User u : users) {
            if (u instanceof Student) {
                Integer s = scoreMap.get(u.getUsername());
                if (s != null) {
                    ((Student) u).setCreditScore(s);
                }
            }
        }
    }

    /** change credit */
    public void setScore(Student student, int score) {
        if (score < 0)   score = 0;
        if (score > 100) score = 100;
        student.setCreditScore(score);
        scoreMap.put(student.getUsername(), score);
        save();
    }

    public int getScore(Student student) {
        return student.getCreditScore();
    }

    // ============== File IO ==============
    private void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                try {
                    scoreMap.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) { }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, Integer> e : scoreMap.entrySet()) {
                bw.write(e.getKey() + "," + e.getValue());
                bw.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
