package bgu.spl181.net.impl.Users;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private final String username;
    private final String password;
    private String type = "normal";
    private String country;
    private int balance = 0;
    private List<RentedMovie> movies = new ArrayList<>();

    public UserInfo(String UserName, String passWord, String country) {
        this.username = UserName;
        this.password = passWord;
        this.country = country;
    }

    public String getUserName() {
        return username;
    }

    public String getPassWord() {
        return password;
    }

    public boolean getIsAdmin() {
        return type.equals("admin");
    }

    public int getBalance() {
        return balance;
    }

    public List<RentedMovie> getMovies() {
        return movies;
    }

    public String getCountry() {
        return country;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean AddMovies(RentedMovie movie) {
       if(movies.contains(movie))
           return false;
       else {
           movies.add(movie);
           return true;
       }

    }

    public boolean RemMovies(int id) {
        for (RentedMovie movie : movies) {
            if(movie.getId() == id){
                movies.remove(movie);
                return true;
            }
        }
        return false;
    }

}