package com.example.btl.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users ORDER BY totalXp DESC")
    List<User> getAllUsersByXp();

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    @Query("UPDATE users SET totalXp = totalXp + :xp, lessonsDone = lessonsDone + 1 WHERE username = :username")
    void updateProgress(String username, int xp);
}
