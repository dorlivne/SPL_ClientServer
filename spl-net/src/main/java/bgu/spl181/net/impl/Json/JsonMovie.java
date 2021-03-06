package bgu.spl181.net.impl.Json;

import bgu.spl181.net.impl.Blockbuster.Movies;
import bgu.spl181.net.impl.Blockbuster.singleMovieInfo;
import bgu.spl181.net.impl.Users.RentedMovie;
import com.google.gson.*;

import java.io.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonMovie {

    private Movies movies = new Movies();//Need this to Write to Json
    private ReadWriteLock Lock = new ReentrantReadWriteLock();
    private String path;
    private Gson gson = new GsonBuilder().registerTypeAdapter(Integer.class, (JsonSerializer<Integer>) (intger, type, JsonSerializationContext) -> new JsonPrimitive(intger.toString())).setPrettyPrinting().create();
    private JsonObject jsondoc;

    public JsonMovie(String path) {
        this.path = path;
        updateMovies();   //Initialize JsonMovies
    }


    /**
     * this methos writes new information to the json file, has to be thread-safe
     *
     * @return true if suceeded in writing
     */
    private boolean WriteJson() {
        Lock.writeLock().lock();
        try (FileWriter UpDater = new FileWriter(path)) {
            gson.toJson(movies, Movies.class, UpDater);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Lock.writeLock().unlock();
            return true;
        }
    }

    /**
     * updates the movies of the system
     */
    private void updateMovies() {
        this.Lock.readLock().lock();
        try (Reader Reader = new FileReader(path)) {
            movies = gson.fromJson(Reader, movies.getClass());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.Lock.readLock().unlock();
        }
    }

    public boolean addMovie(singleMovieInfo movieToAdd) {
        Lock.writeLock().lock();
        updateMovies();
        if (movies.HasMovie(movieToAdd.getName())) {
            Lock.writeLock().unlock();
            return false;
        }
        movies.addMovie(movieToAdd);
        WriteJson();
        Lock.writeLock().unlock();
        return true;
    }

    public singleMovieInfo getSpecificMovie(String movieName) {
        updateMovies();
        return movies.getSpecificMovie(movieName);
    }

    public boolean hasMovie(String movieName) {
        updateMovies();
        return movies.HasMovie(movieName);
    }

    public boolean isBanned(String movieName, String usersCountry) {
        updateMovies();
        if (hasMovie(movieName)) {
            getSpecificMovie(movieName).getBannedCountries().contains(usersCountry);
            return true;
        }
        return false;
    }

    public boolean hasAviliableCopiesLeft(String movieName) {
        updateMovies();
        if (hasMovie(movieName))
            return (getSpecificMovie(movieName).getAvailableAmount() > 0);
        return false;
    }

    public String getAllMoviesNames() {
        updateMovies();
        String tmp = "";
        for (int index = 0; index < movies.getMovies().size(); index++) {
            tmp = tmp.concat(", \"" + movies.getMovies().get(index).getName() + "\"");
        }

        return tmp;
    }

    public void BlockMovies(Runnable task) {
        this.Lock.writeLock().lock();
        task.run();
        this.Lock.writeLock().unlock();
    }

    ///note that this function is always called from Block Movies task runnable meaning the json is locked for other users
    public RentedMovie RentMovie(String movieName) {
        Lock.writeLock().lock();
        updateMovies();
        RentedMovie ans = this.movies.RentMovie(movieName);
        WriteJson();
        Lock.writeLock().unlock();
        return ans;
    }

    public void addCopy(String movieName){
        Lock.writeLock().lock();
        updateMovies();
        this.movies.getSpecificMovie(movieName).addCopy();
        WriteJson();
        Lock.writeLock().unlock();
    }


    public int getNewMovieId() {
        updateMovies();
        return this.movies.getNewMovieId();
    }

    public void remMovie(singleMovieInfo movieToRemmove) {
        Lock.writeLock().lock();
        updateMovies();
        this.movies.remMovie(movieToRemmove);
        WriteJson();
        Lock.writeLock().unlock();
    }

    public void changePrice(Integer price, String movieName) {
        Lock.writeLock().lock();
        updateMovies();
        this.movies.changePrice(price,movieName);
        WriteJson();
        Lock.writeLock().unlock();
    }
}



