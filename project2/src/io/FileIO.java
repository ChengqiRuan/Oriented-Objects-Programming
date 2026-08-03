package io;

import users.User;
import users.Student;
import users.Admin;
import reservation_seat.Seat;
import reservation_seat.Reservation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

    // load users
    public static List<User> loadUsers() {
        List<User> userList = new ArrayList<>();
        File file = new File("users.txt");

        if (!file.exists()) {
            return userList;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String username = parts[0].trim();
                String password = parts[1].trim();
                String role = parts[2].trim();

                if (role.equals("admin")) {
                    userList.add(new Admin(username, password));
                } else {
                    userList.add(new Student(username, password));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    // save users
    public static void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("users.txt"), StandardCharsets.UTF_8))) {
            for (User u : users) {
                String role = (u instanceof Admin) ? "admin" : "student";
                bw.write(u.getUsername() + "," + u.getPassword() + "," + role);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // load seats
    public static List<Seat> loadSeats() {
        List<Seat> seatList = new ArrayList<>();
        File file = new File("seats.txt");

        if (!file.exists()) {
            return seatList;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                int seatId = Integer.parseInt(parts[0].trim());
                int floor = Integer.parseInt(parts[1].trim());
                String area = parts[2].trim();
                boolean available = Boolean.parseBoolean(parts[3].trim());

                Seat seat = new Seat(seatId, floor, area);
                if (!available) {
                    seat.reserve();
                }
                seatList.add(seat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seatList;
    }

    // save seats
    public static void saveSeats(List<Seat> seats) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("seats.txt"), StandardCharsets.UTF_8))) {
            for (Seat s : seats) {
                bw.write(s.getSeatID() + "," + s.getFloor() + "," + s.getArea() + "," + s.isAvailable());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // load reservations
    public static List<Reservation> loadReservations(List<User> users, List<Seat> seats) {
        List<Reservation> reservationList = new ArrayList<>();
        File file = new File("reservations.txt");

        if (!file.exists()) {
            return reservationList;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String stuUsername = parts[0].trim();
                int seatId = Integer.parseInt(parts[1].trim());
                String time = parts[2].trim();
                String status = parts[3].trim();

                Student student = null;
                for (User u : users) {
                    if (u instanceof Student && u.getUsername().equals(stuUsername)) {
                        student = (Student) u;
                        break;
                    }
                }

                Seat seat = null;
                for (Seat s : seats) {
                    if (s.getSeatID() == seatId) {
                        seat = s;
                        break;
                    }
                }

                if (student != null && seat != null) {
                    Reservation res = new Reservation(student, seat, time);
                    res.setStatus(status);
                    reservationList.add(res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservationList;
    }

    //save reservations
    public static void saveReservations(List<Reservation> reservations) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("reservations.txt"), StandardCharsets.UTF_8))) {
            for (Reservation r : reservations) {
                bw.write(r.getStudent().getUsername() + "," +
                        r.getSeat().getSeatID() + "," +
                        r.getTime() + "," +
                        r.getStatus());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}