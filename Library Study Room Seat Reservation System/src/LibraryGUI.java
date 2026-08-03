import users.User;
import users.Student;
import users.Admin;
import reservation_seat.Seat;
import reservation_seat.Reservation;
import io.FileIO;
import Service.StudentService;
import Service.AdminService;
import Service.WaitlistService;
import Service.WaitlistEntry;
import Service.StatisticsService;
import Service.CreditService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Library Seat Reservation System - Graphical User Interface
 * Functions:
 *  - Student: Register / Log in / View seats by floor and area / Reserve seats / Cancel reservations / My Reservations
 *             / Credit score display / Join waiting list / Check waiting list status
 *  - Administrator: View all reservations / Manage reservations (Reject) / Release seats / Manage waiting list
 *                   / Seat usage statistics / Popular areas & seats / Peak reservation hours
 *  - Waiting List: Automatically assign seats to the next eligible student when seats are released
 */
public class LibraryGUI {

    // ============== Topic color  ==============
    private static final Color BG_LIGHT   = new Color(245, 248, 252);
    private static final Color HEADER_BG  = new Color(33, 97, 140);
    private static final Color HEADER_FG  = Color.WHITE;
    private static final Color BTN_BG     = new Color(52, 152, 219);
    private static final Color BTN_FG     = Color.WHITE;
    private static final Color BTN_OK     = new Color(39, 174, 96);
    private static final Color BTN_WARN   = new Color(231, 126, 34);
    private static final Color BTN_DANGER = new Color(192, 57, 43);
    private static final Font  FONT_BIG   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_NORM  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font  FONT_MONO  = new Font("Consolas", Font.PLAIN, 13);

    // ============== status ==============
    private JFrame loginFrame;
    private List<User> userList;
    private List<Seat> seatList;
    private List<Reservation> reservationList;       // From：studentService.getAllReservations()
    private StudentService studentService;
    private AdminService adminService;
    private WaitlistService waitlistService;
    private CreditService creditService;

    public LibraryGUI() {
        userList = FileIO.loadUsers();
        seatList = FileIO.loadSeats();
        studentService = new StudentService(userList, seatList);
        adminService   = new AdminService(studentService);
        // Share the same reservation list between GUI and Service to prevent data inconsistency caused by duplicate data.
        reservationList = studentService.getAllReservations();
        waitlistService = new WaitlistService(userList, studentService);
        creditService   = new CreditService();
        creditService.applyTo(userList);

        // Initialization: Only create the admin user by default
        if (userList.isEmpty()) {
            userList.add(new Admin("admin", "admin"));
            FileIO.saveUsers(userList);
        }

        //  Default seats: Floors 1-5, each floor contains Study Area and Discussion Area
        if (seatList.isEmpty()) {
            for (int floor = 1; floor <= 5; floor++) {
                seatList.add(new Seat(floor * 100 + 1, floor, "Study Area"));
                seatList.add(new Seat(floor * 100 + 2, floor, "Study Area"));
                seatList.add(new Seat(floor * 100 + 3, floor, "Study Area"));
                seatList.add(new Seat(floor * 100 + 4, floor, "Discussion Area"));
                seatList.add(new Seat(floor * 100 + 5, floor, "Discussion Area"));
            }
            FileIO.saveSeats(seatList);
        }

        createLoginUI();
    }

    // ============== UI Method ==============
    private JButton styledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(BTN_FG);
        b.setFocusPainted(false);
        b.setFont(FONT_BOLD);
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        return b;
    }

    private JLabel header(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(HEADER_BG);
        l.setForeground(HEADER_FG);
        l.setFont(FONT_BIG);
        l.setBorder(new EmptyBorder(14, 10, 14, 10));
        return l;
    }

    // ============== login page ==============
    private void createLoginUI() {
        loginFrame = new JFrame("Library Seat Reservation System");
        loginFrame.setSize(460, 360);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());

        loginFrame.add(header("📚 Library Seat Reservation"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_LIGHT);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(FONT_BOLD);
        JTextField userText = new JTextField(15);
        userText.setFont(FONT_NORM);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(FONT_BOLD);
        JPasswordField passText = new JPasswordField(15);
        passText.setFont(FONT_NORM);

        g.gridx = 0; g.gridy = 0; form.add(userLabel, g);
        g.gridx = 1; g.gridy = 0; form.add(userText,  g);
        g.gridx = 0; g.gridy = 1; form.add(passLabel, g);
        g.gridx = 1; g.gridy = 1; form.add(passText,  g);

        JButton loginBtn    = styledBtn("Login",    BTN_BG);
        JButton registerBtn = styledBtn("Register", BTN_OK);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        btnPanel.setBackground(BG_LIGHT);
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        form.add(btnPanel, g);

        loginFrame.add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel("Default admin: admin / admin", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setBorder(new EmptyBorder(8, 0, 10, 0));
        loginFrame.add(hint, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();
            User user = validateLogin(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(loginFrame, "Invalid account or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loginFrame.setVisible(false);
            if (user instanceof Student) showStudentUI((Student) user);
            else                          showAdminUI();
        });

        registerBtn.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please fill all fields");
                return;
            }
            for (User u : userList) {
                if (u.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(loginFrame, "Username already exists");
                    return;
                }
            }
            userList.add(new Student(username, password));
            FileIO.saveUsers(userList);
            JOptionPane.showMessageDialog(loginFrame,
                    "Registered successfully! You can login now.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        loginFrame.setVisible(true);
    }

    private User validateLogin(String username, String password) {
        for (User u : userList) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password))
                return u;
        }
        return null;
    }

    // ============== student management ==============
    private void showStudentUI(Student student) {
        JFrame f = new JFrame("Student Panel - " + student.getUsername());
        f.setSize(880, 600);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(BG_LIGHT);

        // ---------- Header ----------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        JLabel title = new JLabel("  📖 Welcome, " + student.getUsername());
        title.setFont(FONT_BIG);
        title.setForeground(HEADER_FG);
        title.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel creditLabel = new JLabel(creditText(student) + "    ");
        creditLabel.setFont(FONT_BOLD);
        creditLabel.setForeground(student.canReserve() ? new Color(241, 245, 249) : new Color(255, 230, 230));
        creditLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(creditLabel, BorderLayout.EAST);
        f.add(headerPanel, BorderLayout.NORTH);

        // ---------- Filter row ----------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.setBackground(BG_LIGHT);

        JComboBox<Integer> floorBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JComboBox<String> areaBox   = new JComboBox<>(new String[]{"All", "Study Area", "Discussion Area"});
        JTextField timeText = new JTextField(18);
        timeText.setFont(FONT_NORM);

        JLabel timeHint = new JLabel("Format: 2026-12-31 14:00-16:00");
        timeHint.setForeground(BTN_DANGER);
        timeHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        topPanel.add(new JLabel("Floor:"));
        topPanel.add(floorBox);
        topPanel.add(new JLabel("  Area:"));
        topPanel.add(areaBox);
        topPanel.add(new JLabel("  Time:"));
        topPanel.add(timeText);
        topPanel.add(timeHint);
        f.add(topPanel, BorderLayout.AFTER_LAST_LINE);

        // ---------- Center: seat list ----------
        DefaultListModel<String> seatModel = new DefaultListModel<>();
        JList<String> seatListUI = new JList<>(seatModel);
        seatListUI.setFont(FONT_MONO);
        seatListUI.setFixedCellHeight(24);
        JScrollPane sp = new JScrollPane(seatListUI);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225)),
                "Available Seats", TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_LIGHT);
        center.add(topPanel, BorderLayout.NORTH);
        center.add(sp, BorderLayout.CENTER);
        f.add(center, BorderLayout.CENTER);

        // ---------- Bottom buttons ----------
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        btnPanel.setBackground(BG_LIGHT);
        JButton showBtn     = styledBtn("Show Seats",        BTN_BG);
        JButton reserveBtn  = styledBtn("Reserve",           BTN_OK);
        JButton waitlistBtn = styledBtn("Join Waitlist",     BTN_WARN);
        JButton statusBtn   = styledBtn("Waitlist Status",   BTN_BG);
        JButton myBtn       = styledBtn("My Reservations",   BTN_BG);
        JButton cancelBtn   = styledBtn("Cancel My Booking", BTN_DANGER);
        JButton logoutBtn   = styledBtn("Logout",            new Color(127, 140, 141));
        btnPanel.add(showBtn);
        btnPanel.add(reserveBtn);
        btnPanel.add(waitlistBtn);
        btnPanel.add(statusBtn);
        btnPanel.add(myBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(logoutBtn);
        f.add(btnPanel, BorderLayout.SOUTH);

        // ---------- refresh ----------
        Runnable refreshSeats = () -> {
            int floor = (int) floorBox.getSelectedItem();
            String area = (String) areaBox.getSelectedItem();
            seatModel.clear();
            for (Seat s : currentFloorSeats(floor, area)) {
                seatModel.addElement(formatSeat(s));
            }
            if (seatModel.isEmpty()) {
                seatModel.addElement("(No matching seats)");
            }
        };

        showBtn.addActionListener(e -> refreshSeats.run());

        // ---------- reservation ----------
        reserveBtn.addActionListener(e -> {
            if (!student.canReserve()) {
                JOptionPane.showMessageDialog(f,
                        "Your credit score is too low (<80).\nReservation is not allowed.",
                        "Credit Limit", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int floor = (int) floorBox.getSelectedItem();
            String area = (String) areaBox.getSelectedItem();
            String time = timeText.getText().trim();
            int idx = seatListUI.getSelectedIndex();

            if (time.isEmpty() || idx == -1) {
                JOptionPane.showMessageDialog(f,
                        "Steps:\n  1. Select Floor & Area\n  2. Click 'Show Seats'\n  3. Choose a seat\n  4. Enter time",
                        "Tip", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<Seat> floorSeats = currentFloorSeats(floor, area);
            if (idx >= floorSeats.size()) {
                JOptionPane.showMessageDialog(f, "Please click 'Show Seats' first");
                return;
            }
            Seat selected = floorSeats.get(idx);
            if (!selected.isAvailable()) {
                JOptionPane.showMessageDialog(f,
                        "This seat is already taken.\nYou can join the waitlist instead.",
                        "Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(f,
                    "Reserve seat " + selected.getSeatID() + " at " + time + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = studentService.reserveSeat(student, selected, time);
            if (ok) {
                JOptionPane.showMessageDialog(f,
                        "✅ Reserved successfully (Auto Approved)",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(f,
                        "❌ Reservation failed.\nYou can only have ONE active reservation.",
                        "Failed", JOptionPane.ERROR_MESSAGE);
            }
            refreshSeats.run();
        });

        // ---------- join waiting list ----------
        waitlistBtn.addActionListener(e -> {
            if (!student.canReserve()) {
                JOptionPane.showMessageDialog(f,
                        "Credit score too low (<80). Cannot join waitlist.",
                        "Credit Limit", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String time = timeText.getText().trim();
            if (time.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Please enter a time first");
                return;
            }
            // check status
            for (Reservation r : reservationList) {
                if (r.getStudent().equals(student) && "approved".equals(r.getStatus())) {
                    JOptionPane.showMessageDialog(f,
                            "You already have an active reservation.\nCancel it before joining waitlist.",
                            "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            int floor = (int) floorBox.getSelectedItem();
            String area = (String) areaBox.getSelectedItem();
            String areaKey = "All".equals(area) ? "any" : area;

            boolean ok = waitlistService.joinWaitlist(student, floor, areaKey, time);
            if (ok) {
                int pos = waitlistService.positionOf(student);
                JOptionPane.showMessageDialog(f,
                        "✅ Joined waitlist.\nYour position: #" + pos +
                        "\nYou will be auto-assigned when a matching seat is released.",
                        "Waitlist", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(f,
                        "You are already on the waitlist (or not allowed).",
                        "Failed", JOptionPane.WARNING_MESSAGE);
            }
        });

        // ---------- waiting list status  ----------
        statusBtn.addActionListener(e -> {
            int pos = waitlistService.positionOf(student);
            if (pos < 0) {
                JOptionPane.showMessageDialog(f, "You are not on the waitlist.",
                        "Status", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int choice = JOptionPane.showConfirmDialog(f,
                    "You are at position #" + pos + " on the waitlist.\nDo you want to leave the waitlist?",
                    "Waitlist Status", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                waitlistService.leaveWaitlist(student);
                JOptionPane.showMessageDialog(f, "You left the waitlist.");
            }
        });

        // ---------- my reservations ----------
        myBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Reservation r : reservationList) {
                if (r.getStudent().equals(student)) {
                    sb.append("Seat ").append(r.getSeat().getSeatID())
                      .append("  |  Floor ").append(r.getSeat().getFloor())
                      .append("  |  ").append(r.getSeat().getArea())
                      .append("  |  Time: ").append(r.getTime())
                      .append("  |  Status: ").append(r.getStatus())
                      .append('\n');
                }
            }
            int wpos = waitlistService.positionOf(student);
            if (wpos > 0) {
                sb.append("\n[Waitlist] You are at position #").append(wpos);
            }
            String msg = sb.length() == 0 ? "No reservations" : sb.toString();
            JTextArea ta = new JTextArea(msg);
            ta.setFont(FONT_MONO);
            ta.setEditable(false);
            JScrollPane spx = new JScrollPane(ta);
            spx.setPreferredSize(new Dimension(560, 280));
            JOptionPane.showMessageDialog(f, spx, "My Reservations", JOptionPane.INFORMATION_MESSAGE);
        });

        // ---------- cancel ----------
        cancelBtn.addActionListener(e -> {
            Reservation active = null;
            for (Reservation r : reservationList) {
                if (r.getStudent().equals(student) && "approved".equals(r.getStatus())) {
                    active = r; break;
                }
            }
            if (active == null) {
                JOptionPane.showMessageDialog(f, "No active reservation to cancel.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(f,
                    "Cancel reservation of seat " + active.getSeat().getSeatID() + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Seat releasedSeat = active.getSeat();
            studentService.cancelReservation(active);

            // waiting list service
            WaitlistEntry assigned = waitlistService.tryAutoAssign(releasedSeat);
            if (assigned != null) {
                JOptionPane.showMessageDialog(f,
                        "✅ Cancelled.\nSeat auto-assigned to waitlist user: " + assigned.getStudent().getUsername(),
                        "Done", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(f, "✅ Cancelled successfully.");
            }
            refreshSeats.run();
        });

        logoutBtn.addActionListener(e -> {
            f.dispose();
            loginFrame.setVisible(true);
        });

        f.setVisible(true);
    }

    /**current floor and seats */
    private List<Seat> currentFloorSeats(int floor, String area) {
        List<Seat> result = new ArrayList<>();
        for (Seat s : seatList) {
            if (s.getFloor() != floor) continue;
            if (!"All".equals(area) && !area.equalsIgnoreCase(s.getArea())) continue;
            result.add(s);
        }
        return result;
    }

    private String formatSeat(Seat s) {
        return String.format("Seat %-4d | Floor %d | %-16s | %s",
                s.getSeatID(), s.getFloor(), s.getArea(),
                s.isAvailable() ? "🟢 Available" : "🔴 Occupied");
    }

    private String creditText(Student s) {
        return "💳 Credit: " + s.getCreditScore() + "/100"
                + (s.canReserve() ? "  ✓" : "  (Reserve disabled)");
    }

    // ============== admin management  ==============
    private void showAdminUI() {
        JFrame f = new JFrame("Admin Panel");
        f.setSize(960, 640);
        f.setLocationRelativeTo(null);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(BG_LIGHT);

        f.add(header("🛠 Admin Panel - Library Management"), BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.setBackground(BG_LIGHT);
        JComboBox<Integer> floorBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JButton showSeatsBtn = styledBtn("Show Seat Status", BTN_BG);
        topPanel.add(new JLabel("Floor:"));
        topPanel.add(floorBox);
        topPanel.add(showSeatsBtn);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);

        // ----- Tab1: reservations -----
        DefaultListModel<String> resModel = new DefaultListModel<>();
        JList<String> resList = new JList<>(resModel);
        resList.setFont(FONT_MONO);
        refreshReservationList(resModel);
        tabs.addTab("All Reservations", new JScrollPane(resList));

        // ----- Tab2: seat status  -----
        DefaultListModel<String> seatModel = new DefaultListModel<>();
        JList<String> seatListUI = new JList<>(seatModel);
        seatListUI.setFont(FONT_MONO);
        tabs.addTab("Seat Status", new JScrollPane(seatListUI));

        // ----- Tab3: waiting list -----
        DefaultListModel<String> waitModel = new DefaultListModel<>();
        JList<String> waitListUI = new JList<>(waitModel);
        waitListUI.setFont(FONT_MONO);
        refreshWaitlist(waitModel);
        tabs.addTab("Waitlist", new JScrollPane(waitListUI));

        // ----- Tab4: statistics  -----
        JTextArea statsArea = new JTextArea();
        statsArea.setFont(FONT_MONO);
        statsArea.setEditable(false);
        statsArea.setBackground(Color.WHITE);
        JScrollPane statsScroll = new JScrollPane(statsArea);
        tabs.addTab("Statistics", statsScroll);

        // ----- Buttons -----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        btnPanel.setBackground(BG_LIGHT);
        JButton rejectBtn   = styledBtn("Reject Reservation", BTN_DANGER);
        JButton releaseBtn  = styledBtn("Release Seat",        BTN_WARN);
        JButton removeWait  = styledBtn("Remove From Waitlist", BTN_DANGER);
        JButton creditBtn   = styledBtn("Adjust Credit",        BTN_OK);
        JButton refreshBtn  = styledBtn("Refresh All",         BTN_BG);
        JButton logoutBtn   = styledBtn("Logout",              new Color(127, 140, 141));
        btnPanel.add(rejectBtn);
        btnPanel.add(releaseBtn);
        btnPanel.add(removeWait);
        btnPanel.add(creditBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(logoutBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_LIGHT);
        center.add(topPanel, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);

        f.add(center, BorderLayout.CENTER);
        f.add(btnPanel, BorderLayout.SOUTH);

        // ===== refresh =====
        Runnable refreshSeatsAdmin = () -> {
            int floor = (int) floorBox.getSelectedItem();
            seatModel.clear();
            for (Seat s : seatList) {
                if (s.getFloor() == floor) {
                    seatModel.addElement(formatSeat(s));
                }
            }
        };

        Runnable refreshAll = () -> {
            refreshReservationList(resModel);
            refreshWaitlist(waitModel);
            refreshSeatsAdmin.run();
            statsArea.setText(buildStatistics());
        };

        showSeatsBtn.addActionListener(e -> {
            refreshSeatsAdmin.run();
            tabs.setSelectedIndex(1);
        });

        // ----- reject -----
        rejectBtn.addActionListener(e -> {
            int idx = resList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(f, "Select a reservation in 'All Reservations' tab first");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(f,
                    "Reject this reservation?\nThe seat will be released and offered to waitlist.",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Reservation r = reservationList.get(idx);
            // release when "approved"
            if ("approved".equals(r.getStatus())) {
                r.setStatus("rejected");
                r.getSeat().release();
                FileIO.saveReservations(reservationList);
                FileIO.saveSeats(seatList);
                WaitlistEntry assigned = waitlistService.tryAutoAssign(r.getSeat());
                if (assigned != null) {
                    JOptionPane.showMessageDialog(f,
                            "Rejected. Seat auto-assigned to waitlist user: " + assigned.getStudent().getUsername());
                } else {
                    JOptionPane.showMessageDialog(f, "Rejected & seat released.");
                }
            } else {
                r.setStatus("rejected");
                FileIO.saveReservations(reservationList);
                JOptionPane.showMessageDialog(f, "Reservation marked as rejected.");
            }
            refreshAll.run();
        });

        // ----- release seats -----
        releaseBtn.addActionListener(e -> {
            int idx = seatListUI.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(f, "Select a seat in 'Seat Status' tab first");
                return;
            }
            int floor = (int) floorBox.getSelectedItem();
            List<Seat> tmp = new ArrayList<>();
            for (Seat s : seatList) if (s.getFloor() == floor) tmp.add(s);
            if (idx >= tmp.size()) return;
            Seat seat = tmp.get(idx);

            //  released
            for (Reservation r : reservationList) {
                if (r.getSeat() == seat && "approved".equals(r.getStatus())) {
                    r.setStatus("released");
                }
            }
            seat.release();
            FileIO.saveSeats(seatList);
            FileIO.saveReservations(reservationList);

            WaitlistEntry assigned = waitlistService.tryAutoAssign(seat);
            if (assigned != null) {
                JOptionPane.showMessageDialog(f,
                        "Seat released and auto-assigned to waitlist user: " + assigned.getStudent().getUsername());
            } else {
                JOptionPane.showMessageDialog(f, "Seat released.");
            }
            refreshAll.run();
        });

        // ----- cancel waiting list -----
        removeWait.addActionListener(e -> {
            int idx = waitListUI.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(f, "Select an entry in 'Waitlist' tab first");
                return;
            }
            List<WaitlistEntry> wl = waitlistService.getWaitlist();
            if (idx >= wl.size()) return;
            WaitlistEntry entry = wl.get(idx);
            int confirm = JOptionPane.showConfirmDialog(f,
                    "Remove " + entry.getStudent().getUsername() + " from waitlist?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            waitlistService.leaveWaitlist(entry.getStudent());
            refreshAll.run();
        });

        refreshBtn.addActionListener(e -> refreshAll.run());

        // ----- credit change -----
        creditBtn.addActionListener(e -> {
            List<Student> students = new ArrayList<>();
            for (User u : userList) if (u instanceof Student) students.add((Student) u);
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(f, "No student accounts found.");
                return;
            }
            String[] names = new String[students.size()];
            for (int i = 0; i < students.size(); i++) {
                names[i] = students.get(i).getUsername() + "  (current: "
                        + students.get(i).getCreditScore() + ")";
            }
            String pick = (String) JOptionPane.showInputDialog(f,
                    "Select a student to adjust credit:",
                    "Adjust Credit",
                    JOptionPane.PLAIN_MESSAGE,
                    null, names, names[0]);
            if (pick == null) return;
            int idx = -1;
            for (int i = 0; i < names.length; i++) if (names[i].equals(pick)) { idx = i; break; }
            if (idx < 0) return;
            Student target = students.get(idx);

            String input = JOptionPane.showInputDialog(f,
                    "Enter new credit score (0-100) for " + target.getUsername() + ":",
                    String.valueOf(target.getCreditScore()));
            if (input == null) return;
            int newScore;
            try {
                newScore = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(f, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newScore < 0 || newScore > 100) {
                JOptionPane.showMessageDialog(f, "Score must be 0-100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            creditService.setScore(target, newScore);
            JOptionPane.showMessageDialog(f,
                    "✅ " + target.getUsername() + "'s credit updated to " + newScore
                    + (target.canReserve() ? "  (Reserve allowed)" : "  (Reserve disabled, <80)"),
                    "Done", JOptionPane.INFORMATION_MESSAGE);
        });

        logoutBtn.addActionListener(e -> {
            f.dispose();
            loginFrame.setVisible(true);
        });

        // build statistics
        statsArea.setText(buildStatistics());

        f.setVisible(true);
    }

    private void refreshReservationList(DefaultListModel<String> model) {
        model.clear();
        for (Reservation r : reservationList) {
            model.addElement(String.format("Student:%-12s | Seat:%-4d | Floor:%d | %-16s | %s | %s",
                    r.getStudent().getUsername(),
                    r.getSeat().getSeatID(),
                    r.getSeat().getFloor(),
                    r.getSeat().getArea(),
                    r.getTime(),
                    r.getStatus()));
        }
    }

    private void refreshWaitlist(DefaultListModel<String> model) {
        model.clear();
        List<WaitlistEntry> wl = waitlistService.getWaitlist();
        int i = 1;
        for (WaitlistEntry w : wl) {
            String floor = (w.getFloor() == 0) ? "Any" : String.valueOf(w.getFloor());
            model.addElement(String.format("#%-2d | Student:%-12s | Floor:%-3s | Area:%-16s | Time:%s",
                    i++, w.getStudent().getUsername(), floor, w.getArea(), w.getTime()));
        }
    }

    /** text */
    private String buildStatistics() {
        StatisticsService stats = new StatisticsService(reservationList, seatList);
        StringBuilder sb = new StringBuilder();
        sb.append("=========== Library Statistics ===========\n\n");
        sb.append(String.format("Total reservations    : %d%n", stats.totalReservations()));
        sb.append(String.format("Active reservations   : %d%n", stats.activeReservations()));
        sb.append(String.format("Current usage rate    : %.2f%%%n", stats.usageRate()));
        sb.append(String.format("Most popular area     : %s%n", stats.mostPopularArea()));
        int ph = stats.peakHour();
        sb.append(String.format("Peak hour             : %s%n",
                ph < 0 ? "N/A" : (ph + ":00 ~ " + (ph + 1) + ":00")));
        sb.append('\n');

        sb.append("---- Reservations per Area ----\n");
        Map<String, Integer> ap = stats.areaPopularity();
        if (ap.isEmpty()) sb.append("  (no data)\n");
        for (Map.Entry<String, Integer> e : ap.entrySet()) {
            sb.append(String.format("  %-20s %d%n", e.getKey(), e.getValue()));
        }
        sb.append('\n');

        sb.append("---- Reservations per Floor ----\n");
        Map<Integer, Integer> fp = stats.floorPopularity();
        if (fp.isEmpty()) sb.append("  (no data)\n");
        for (Map.Entry<Integer, Integer> e : fp.entrySet()) {
            sb.append(String.format("  Floor %d              %d%n", e.getKey(), e.getValue()));
        }
        sb.append('\n');

        sb.append("---- Top 5 Popular Seats ----\n");
        List<Map.Entry<Integer, Integer>> top = stats.topSeats(5);
        if (top.isEmpty()) sb.append("  (no data)\n");
        for (Map.Entry<Integer, Integer> e : top) {
            sb.append(String.format("  Seat %-5d           %d times%n", e.getKey(), e.getValue()));
        }
        sb.append('\n');

        sb.append("---- Hourly Booking Distribution ----\n");
        Map<Integer, Integer> peak = stats.peakHours();
        if (peak.isEmpty()) sb.append("  (no data)\n");
        for (Map.Entry<Integer, Integer> e : peak.entrySet()) {
            int h = e.getKey();
            int v = e.getValue();
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < v; i++) bar.append('█');
            sb.append(String.format("  %02d:00  %s (%d)%n", h, bar, v));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryGUI::new);
    }
}
