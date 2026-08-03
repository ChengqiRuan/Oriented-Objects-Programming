package Service;

import users.Student;

/**
 * Waiting List Entry
 * floor = 0 means any floor
 * area = "any" means any area
 */
public class WaitlistEntry {
    private Student student;
    private int floor;
    private String area;
    private String time;

    public WaitlistEntry(Student student, int floor, String area, String time) {
        this.student = student;
        this.floor = floor;
        this.area = area;
        this.time = time;
    }

    public Student getStudent() { return student; }
    public int getFloor()       { return floor; }
    public String getArea()     { return area; }
    public String getTime()     { return time; }
}
