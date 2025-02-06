import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class StudentManagement {
    private static final String URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; //;

    private static ArrayList<Student> students = new ArrayList();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println("Cannot connect to MySQL server.");
            return;
        }

        while(true){
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    listStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    System.out.println("\nExiting...");
                    return;
                default:
                    System.out.println("\nInvalid choice. Please enter a number between 1 and 6.");
            }
        }
    }

    private static void displayMenu(){
        System.out.println("\n<< Welcome to Student Management System >>\n");
        System.out.println("1. Add Student");
        System.out.println("2. List Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit\n");
        System.out.print("Enter Your Choice: ");
    }

    private static int getUserChoice(){
        int choice = -1;
        while (true){
            try{
                choice = sc.nextInt();
                sc.nextLine();
                break;
            }
            catch (InputMismatchException e){
                System.out.println("\nInvalid choice. Please enter a number between 1 and 6.\n");
                sc.nextLine();
                displayMenu();
            }
        }
        return choice;
    }

    private static void addStudent(){
        System.out.println("\nAdd User");

        System.out.println("\nEnter Student ID: ");
        String addStudentID = sc.nextLine().trim();

        if(isStudentIDtaken(addStudentID)){
            System.out.println("\nThis student ID is already taken. Please try again.");
            return;
        }

        String fullname;
        while(true){
            System.out.println("\nEnter Student Name: ");
            fullname = sc.nextLine().trim();
            if(fullname.matches("^[a-zA-Z\\s]+$")){
                break;
            }else{
                System.out.println("\nInvalid name. Name can only contain letters and spaces. Please try again.");
            }
        }

        String birthdayStr;
        LocalDate birthday;
        while(true){
            System.out.println("\nEnter Birthday (YYYY-MM-DD): ");
            birthdayStr = sc.nextLine().trim();
            try{
                birthday = LocalDate.parse(birthdayStr);
                break;
            }catch (Exception e){
                System.out.println("\nInvalid date. Please try again.");
            }
        }

        String email;
        while(true){
            System.out.println("\nEnter Email: ");
            email = sc.nextLine().trim();
            if(email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
                break;
            }else{
                System.out.println("\nInvalid email. Please try again.");
            }
        }

        Student student = new Student(addStudentID,fullname,birthday,email);

        try(Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO students (studentID, fullName, birthday, email) VALUES (?, ?, ?, ?)")){
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getFullName());
            stmt.setDate(3, java.sql.Date.valueOf(student.getBirthday()));
            stmt.setString(4, student.getEmail());
            stmt.executeUpdate();
            System.out.println("\nStudent added successfully.\n");

            students.add(student);

        }catch (SQLException e){
            System.out.println("\nERROR adding student: " + e.getMessage());
        }
    }

    private static boolean isStudentIDtaken(String studentId){
        try(Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            PreparedStatement stmt = connection.prepareStatement("SELECT studentID FROM students WHERE studentID = ?")){
            stmt.setString(1, studentId);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next() && rs.getString("studentID").equals(studentId)){
                    return true;
                }
                else{
                    return false;
                }
            }
        } catch (SQLException e){
            System.out.println("\nERROR checking if student ID is taken: " + e.getMessage());
        }
        return false;
    }

    private static void listStudents(){
        System.out.println("\nList Students");
        try(Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students")
        ){
            boolean found = false;

            while(rs.next()){
                found = true;
                System.out.println("\nStudent ID: " + rs.getString("studentID"));
                System.out.println("Full Name: " + rs.getString("fullName"));
                System.out.println("Birthday: " + rs.getDate("birthday"));
                System.out.println("Email: " + rs.getString("email"));
            }
            if(!found){
                System.out.println("\nNo students found.\n");
            }
        } catch (SQLException e){
            System.out.println("\nERROR listing students: " + e.getMessage());
        }
    }

    private static void searchStudent(){
        System.out.println("\nSearch Student");

        System.out.println("\nEnter Student Name to search: ");
        String searchStudentName = sc.nextLine();

        try(Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students WHERE UPPER(name) = UPPER(?)")
        ){
            stmt.setString(1, searchStudentName);

            ResultSet rs = stmt.executeQuery();
            boolean found = false;

            while (rs.next()){
                found = true;
                System.out.println("\nStudent ID: " + rs.getString("studentID"));
                System.out.println("Full Name: " + rs.getString("fullName"));
                System.out.println("Birthday: " + rs.getDate("birthday"));
                System.out.println("Email: " + rs.getString("email"));
            }

            if(!found){
                System.out.println("\nNo students found.\n");
            }
        } catch (SQLException e){
            System.out.println("\nERROR searching student: " + e.getMessage());
        }
    }

    private static void updateStudent() {
        System.out.println("\nUpdate Student");

        System.out.print("\nEnter Student ID to Update: ");
        String updateStudentID = sc.nextLine().trim();

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmtSelect = conn.prepareStatement("SELECT * FROM students WHERE studentID = ?");
             PreparedStatement stmtUpdate = conn.prepareStatement(
                     "UPDATE students SET fullName = ?, birthday = ?, email = ? WHERE studentID = ?")) {

            stmtSelect.setString(1, updateStudentID);
            ResultSet rs = stmtSelect.executeQuery();

            if (rs.next()) {
                String currentName = rs.getString("fullName");
                LocalDate currentBirthday = rs.getDate("birthday").toLocalDate();
                String currentEmail = rs.getString("email");

                String newName = promptForName(currentName);
                LocalDate newBirthday = promptForBirthday(currentBirthday);
                String newEmail = promptForEmail(currentEmail);

                stmtUpdate.setString(1, newName);
                stmtUpdate.setDate(2, java.sql.Date.valueOf(newBirthday));
                stmtUpdate.setString(3, newEmail);
                stmtUpdate.setString(4, updateStudentID);

                int affectedRows = stmtUpdate.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("\nStudent with ID " + updateStudentID + " updated successfully!");

                    for (Student student : students) {
                        if (student.getStudentID() == updateStudentID) {
                            student.setFullName(newName);
                            student.setBirthday(newBirthday);
                            student.setEmail(newEmail);
                            break;
                        }
                    }
                } else {
                    System.out.println("\nNo student found with that ID.");
                }
            } else {
                System.out.println("\nNo student found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("\nError updating student: " + e.getMessage());
        }
    }

    private static String promptForName(String currentName) {
        String newName;

        while (true) {
            System.out.print("\nEnter New Name (Current: " + currentName + "): ");
            newName = sc.nextLine().trim();

            if (newName.isEmpty()) {
                System.out.println("\nName cannot be empty. Please enter a valid name.");
            } else if (!newName.matches("[a-zA-Z ]+")) {
                System.out.println("\nInvalid name. Name can only contain letters and spaces.");
            } else {
                break;
            }
        }

        return newName;
    }

    private static LocalDate promptForBirthday(LocalDate currentBirthday) {
        LocalDate newBirthday = currentBirthday;

        while (true) {
            System.out.print("\nEnter New Birthday (YYYY-MM-DD) (Current: " + currentBirthday + "): ");
            String newBirthdayStr = sc.nextLine().trim();

            if (newBirthdayStr.isEmpty()) {
                break;
            }

            try {
                newBirthday = LocalDate.parse(newBirthdayStr);
                break;
            } catch (Exception e) {
                System.out.println("\nInvalid date format. Please enter the birthday in YYYY-MM-DD format.");
            }
        }

        return newBirthday;
    }

    private static String promptForEmail(String currentEmail) {
        String newEmail;

        while (true) {
            System.out.print("\nEnter New Email (Current: " + currentEmail + "): ");
            newEmail = sc.nextLine().trim();

            if (newEmail.isEmpty()) {
                break;
            }

            if (!newEmail.contains("@") || !newEmail.contains(".")) {
                System.out.println("\nInvalid email format. Please enter a valid email address.");
            } else {
                break;
            }
        }

        return newEmail;
    }

    private static void deleteStudent() {
        System.out.println("\nDelete Student");

        System.out.print("\nEnter Student ID to Delete: ");
        String deleteStudentID = sc.nextLine().trim();


        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE studentID = ?")) {
            stmt.setString(1, deleteStudentID);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("\nStudent with ID " + deleteStudentID + " deleted successfully!");

                students.removeIf(s -> s.getStudentID() == deleteStudentID);
            } else {
                System.out.println("\nNo student found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("\nError deleting student: " + e.getMessage());
        }
    }
}
