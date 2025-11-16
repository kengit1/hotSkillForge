package com.skillforge.model;

import com.skillforge.db.UserDatabaseManager;
import java.util.List;

public class Test {
    private static final String USERS_FILE_PATH = "users.json";

    public static void main(String[] args) {
        System.out.println("Starting user database test...");
        UserDatabaseManager userDB = new UserDatabaseManager(USERS_FILE_PATH);
        Student s=new Student("9666","omar","omarhesham@gmail.com","123213412");
        List<User> userList = s.readfromfile();

        // 3. Check the results
        if (userList == null || userList.isEmpty()) {
            System.err.println("Test FAILED: No data was loaded. Check the file path and file content.");
            return;
        }

        System.out.println("Test SUCCESS: Loaded " + userList.size() + " users.");

        // Print out the loaded users to verify
        for (User user : userList) {
            System.out.println("--------------------");
            System.out.println("User ID:   " + user.getID());
            System.out.println("Username:  " +user.getUserName()); // <-- Fixed to getUsername
            System.out.println("Role:      " + user.getRole());
            System.out.println("Email:     " + user.getEmail());

            if (user instanceof Student) {
                System.out.println("Type:      Student");
            } else if (user instanceof Instructor) {
                System.out.println("Type:      Instructor");
            }
        }
    }
}