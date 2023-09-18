package uk.ac.soton.ecs.runwayredeclarationtool.data.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import javafx.util.Pair;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;

public class UserRepository {

  private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/db/UserInfo.db";
  private static final String DATABASE_NAME = "userInfo";

  private Dao<User, String> userDao;
  private User selectedUser;

  public User authenticateUser(Pair<String, String> loginCred) {
    try {
      String username = loginCred.getKey();
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] passHash = digest.digest(loginCred.getValue().getBytes(StandardCharsets.UTF_8));
      System.out.println(loginCred.getValue() + bytesToHex(passHash));

      //now search db
      try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
        // create our data-source for the database
        // setup our database and DAOs
        setupDatabase(connectionSource);
      } catch (Exception e) {
        System.err.println("Failed to read user database");
        e.printStackTrace();
      }

      try {
        // construct a query using the QueryBuilder
        QueryBuilder<User, String> statementBuilder = userDao.queryBuilder();
        // find any rows where airport column == provided icao code
        statementBuilder.where().like(User.USERNAME, username).and()
            .like(User.PASSWORDHASH, bytesToHex(passHash));
        // return first result (should only be 1)
        return (userDao.query(statementBuilder.prepare()).get(0));
      } catch (Exception e) {
        System.err.println("Failed to read user database (or no matches)");
        e.printStackTrace();
      }
      return null;


    } catch (Exception e) {
      System.err.println("How on earth did this happen (invalid hashing algorithm)");
    }
    return null;
  }

  public List<User> getAllUsers() {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read user database");
      e.printStackTrace();
    }
    try {
      QueryBuilder<User, String> statementBuilder = userDao.queryBuilder();
      statementBuilder.selectColumns(User.FIRSTNAME, User.LASTNAME, User.EMAIL);
      return (userDao.query(statementBuilder.prepare()));
    } catch (SQLException sqle) {
      System.err.println("Failed to retrieve all users.");
      sqle.printStackTrace();
    }
    return null;
  }

  public User getUserByEmail(String userEmail) {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read user database");
      e.printStackTrace();
    }
    try {
      QueryBuilder<User, String> statementBuilder = userDao.queryBuilder();
      System.out.println("User email post-selection: " + userEmail);
      statementBuilder.where().like(User.EMAIL, userEmail);
      return (userDao.query(statementBuilder.prepare()).get(0));
    } catch (SQLException sqle) {
      System.err.println("Failed to retrieve user by email.");
      sqle.printStackTrace();
    }
    return null;
  }

  public void updateUserAdminAccess(User user, boolean adminAccess) {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read user database");
      e.printStackTrace();
    }
    try {
      UpdateBuilder<User, String> statementBuilder = userDao.updateBuilder();
      statementBuilder.where().eq(User.EMAIL, user.getEmail());
      statementBuilder.updateColumnValue(User.ADMINACCESS, adminAccess);
      statementBuilder.update();
    } catch (SQLException sqle) {
      System.err.println("Failed to update user.");
      sqle.printStackTrace();
    }
  }

  public void removeUser(User user) {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read user database");
      e.printStackTrace();
    }
    try {
      DeleteBuilder<User, String> statementBuilder = userDao.deleteBuilder();
      statementBuilder.where().eq(User.EMAIL, user.getEmail());
      statementBuilder.delete();
    } catch (SQLException sqle) {
      System.err.println("Failed to remove user.");
      sqle.printStackTrace();
    }
  }

  public void addUser(User user) {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read user database");
      e.printStackTrace();
    }
    try {
      userDao.create(user);
    } catch (SQLException sqle) {
      System.err.println("Failed to add user.");
      sqle.printStackTrace();
    }
  }

  public static String hashPassword(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return bytesToHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      System.err.println("Password hashing messed up somehow.");
      return "1234";
    }
  }

  /**
   * Printing off hex string of bytes because external libraries are the bane of my existence so we
   * work with what we have
   *
   * @param hash byte array of what to print
   * @return the string
   */
  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  private void setupDatabase(ConnectionSource connectionSource) throws Exception {
    userDao = DaoManager.createDao(connectionSource, User.class);
  }
}
