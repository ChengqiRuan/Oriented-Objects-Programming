package Service;

import reservation_seat.Reservation;
import reservation_seat.Seat;

import java.util.*;

/**
 * Statistical Analysis Service (For Administrator Use)
 *  - Total Reservations / Valid Reservations
 *  - Seat Utilization Rate
 *  - Area Popularity Ranking
 *  - Top N Popular Seats
 *  - Peak Reservation Hours
 */
public class StatisticsService {

    private List<Reservation> reservations;
    private List<Seat> seats;

    public StatisticsService(List<Reservation> reservations, List<Seat> seats) {
        this.reservations = reservations;
        this.seats = seats;
    }

    public int totalReservations() {
        return reservations.size();
    }

    public int activeReservations() {
        int c = 0;
        for (Reservation r : reservations) {
            if ("approved".equals(r.getStatus())) c++;
        }
        return c;
    }

    /** Real-time utilization rate (occupied seats / total seats) */
    public double usageRate() {
        if (seats.isEmpty()) return 0;
        int occupied = 0;
        for (Seat s : seats) {
            if (!s.isAvailable()) occupied++;
        }
        return occupied * 100.0 / seats.size();
    }

    /** Area -> Reservation count */
    public Map<String, Integer> areaPopularity() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Reservation r : reservations) {
            String area = r.getSeat().getArea();
            map.merge(area, 1, Integer::sum);
        }
        return map;
    }

    /** Floor -> Reservation count */
    public Map<Integer, Integer> floorPopularity() {
        Map<Integer, Integer> map = new TreeMap<>();
        for (Reservation r : reservations) {
            int f = r.getSeat().getFloor();
            map.merge(f, 1, Integer::sum);
        }
        return map;
    }

    /** Top N Popular Seats */
    public List<Map.Entry<Integer, Integer>> topSeats(int topN) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Reservation r : reservations) {
            map.merge(r.getSeat().getSeatID(), 1, Integer::sum);
        }
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        if (list.size() > topN) return list.subList(0, topN);
        return list;
    }

    /**
     * Peak reservation hours: Parse the time string "yyyy-MM-dd HH:mm-HH:mm"
     * Extract the hour interval [startH, endH), increment count for each covered hour
     */
    public Map<Integer, Integer> peakHours() {
        Map<Integer, Integer> map = new TreeMap<>();
        for (Reservation r : reservations) {
            String t = r.getTime();
            if (t == null) continue;
            try {
                int spaceIdx = t.indexOf(' ');
                String range = (spaceIdx < 0) ? t : t.substring(spaceIdx + 1).trim();
                String[] parts = range.split("-");
                if (parts.length != 2) continue;
                int startH = Integer.parseInt(parts[0].split(":")[0].trim());
                int endH   = Integer.parseInt(parts[1].split(":")[0].trim());
                if (endH <= startH) endH = startH + 1;
                for (int h = startH; h < endH; h++) {
                    map.merge(h, 1, Integer::sum);
                }
            } catch (Exception ignored) { }
        }
        return map;
    }

    /** Name of the most popular area */
    public String mostPopularArea() {
        Map<String, Integer> m = areaPopularity();
        String best = "N/A";
        int max = 0;
        for (Map.Entry<String, Integer> e : m.entrySet()) {
            if (e.getValue() > max) { max = e.getValue(); best = e.getKey(); }
        }
        return best;
    }

    /** Peak hour (returns -1 if no data) */
    public int peakHour() {
        Map<Integer, Integer> map = peakHours();
        int hour = -1, max = 0;
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (e.getValue() > max) { max = e.getValue(); hour = e.getKey(); }
        }
        return hour;
    }
}
