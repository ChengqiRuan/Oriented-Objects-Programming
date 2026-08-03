package Service;

import users.User;
import users.Student;
import reservation_seat.Seat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Waiting List Service
 * Data storage: waitlist.txt
 * Line format: username,floor,area,time
 */
public class WaitlistService {
    private static final String FILE = "waitlist.txt";

    private List<WaitlistEntry> waitlist;
    private StudentService studentService;

    public WaitlistService(List<User> users, StudentService studentService) {
        this.studentService = studentService;
        this.waitlist = load(users);
    }

    public List<WaitlistEntry> getWaitlist() {
        return waitlist;
    }

    /** join */
    public boolean joinWaitlist(Student s, int floor, String area, String time) {
        if (!s.canReserve()) return false;
        for (WaitlistEntry w : waitlist) {
            if (w.getStudent().equals(s)) return false; // 已经在等候列表
        }
        waitlist.add(new WaitlistEntry(s, floor, area, time));
        save();
        return true;
    }

    /** leave */
    public boolean leaveWaitlist(Student s) {
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getStudent().equals(s)) {
                waitlist.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    /** Student's position in the waiting list (1-based), returns -1 if not present */
    public int positionOf(Student s) {
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getStudent().equals(s)) return i + 1;
        }
        return -1;
    }

    /**
     * When a seat is released, try to automatically assign the first matching student
     * from the waiting list to this seat.
     * Returns the assigned entry (null if no match found).
     */
    public WaitlistEntry tryAutoAssign(Seat releasedSeat) {
        if (releasedSeat == null || !releasedSeat.isAvailable()) return null;

        for (int i = 0; i < waitlist.size(); i++) {
            WaitlistEntry w = waitlist.get(i);
            boolean floorOk = (w.getFloor() == 0 || w.getFloor() == releasedSeat.getFloor());
            boolean areaOk  = (w.getArea().equalsIgnoreCase("any")
                            || w.getArea().equalsIgnoreCase(releasedSeat.getArea()));
            if (floorOk && areaOk) {
                boolean ok = studentService.reserveSeat(w.getStudent(), releasedSeat, w.getTime());
                if (ok) {
                    waitlist.remove(i);
                    save();
                    return w;
                }
            }
        }
        return null;
    }

    // ============== File IO ==============
    private void save() {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8))) {
            for (WaitlistEntry w : waitlist) {
                bw.write(w.getStudent().getUsername() + "," +
                         w.getFloor() + "," +
                         w.getArea() + "," +
                         w.getTime());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<WaitlistEntry> load(List<User> users) {
        List<WaitlistEntry> list = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String username = parts[0].trim();
                int floor;
                try { floor = Integer.parseInt(parts[1].trim()); }
                catch (NumberFormatException nfe) { floor = 0; }
                String area = parts[2].trim();
                String time = parts[3].trim();

                Student s = null;
                for (User u : users) {
                    if (u instanceof Student && u.getUsername().equals(username)) {
                        s = (Student) u; break;
                    }
                }
                if (s != null) list.add(new WaitlistEntry(s, floor, area, time));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
