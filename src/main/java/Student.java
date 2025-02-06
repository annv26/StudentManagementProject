
import java.time.LocalDate;

public class Student {
    private String studentID;
    private String fullName;
    private LocalDate birthday;
    private String email;

    // Constructor
    public Student(String studentID, String fullName, LocalDate birthday, String email) {
        this.studentID = studentID;
        this.fullName = fullName;
        this.birthday = birthday;
        this.email = email;
    }

    // Getter() for Student ID
    public String getStudentID() {
        return studentID;
    }

    // Setter() for Student ID
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    // Getter() for Full Name
    public String getFullName() {
        return fullName;
    }

    // Setter() for Full Name
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getter() for Birthday
    public LocalDate getBirthday() {
        return birthday;
    }

    // Setter() for Birthday
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    // Getter() for email
    public String getEmail() {
        return email;
    }

    // Setter() for Gender
    public void setEmail(String email) {
        this.email = email;
    }
}
