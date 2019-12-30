package backend;

public class User {
    static String username = "Administrator";

    public User() {
        username = "Adminstrator";
    }

    public  String getUsername() {
        return username;
    }


    public  void setUsername(String username1) {

        username = username1;

    }
}
