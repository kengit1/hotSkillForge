import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.main.InstructorDashboardFrame;
import com.skillforge.model.Instructor;
import com.skillforge.model.Student;
import com.skillforge.main.StudentDashboard;
import javax.swing.*;
public class MainFrame extends JFrame {



}
public static void main(String[] args) {

    Student s=new Student("9666","Student","OmarHesham","omar@gmail.com" ,"1234", null,null);
    Instructor i=new Instructor("1234","Instructor","Kokoko","@gmail","23456",null);

    UserDatabaseManager us=new UserDatabaseManager("users.json");
    CoursesDatabaseManager cs=new CoursesDatabaseManager();
    us.add(i);
    InstructorDashboardFrame m=new InstructorDashboardFrame(i,cs,us);

}