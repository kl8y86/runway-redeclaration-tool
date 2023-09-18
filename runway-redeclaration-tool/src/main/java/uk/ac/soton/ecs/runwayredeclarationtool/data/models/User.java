package uk.ac.soton.ecs.runwayredeclarationtool.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import uk.ac.soton.ecs.runwayredeclarationtool.data.repositories.UserRepository;

/**
 * A user.
 *
 * @author George Peppard
 */
@DatabaseTable(tableName = "userInfo")
public class User {

  public static final String USERNAME = "username";
  public static final String PASSWORDHASH = "passwordHash";
  public static final String ADMINACCESS = "adminAccess";
  public static final String FIRSTNAME = "firstName";
  public static final String LASTNAME = "lastName";
  public static final String EMAIL = "email";

  /**
   * The username of the user.
   */
  @DatabaseField(columnName = USERNAME, canBeNull = false)
  private String username;

  /**
   * The hashed (SHA256) password for the user.
   */
  @DatabaseField(columnName = PASSWORDHASH, canBeNull = false)
  @XStreamOmitField
  private String passwordHash;

  @DatabaseField(columnName = ADMINACCESS, canBeNull = false)
  private boolean adminAccess;

  @DatabaseField(columnName = FIRSTNAME, canBeNull = false)
  private String firstName;

  @DatabaseField(columnName = LASTNAME, canBeNull = false)
  private String lastName;

  @DatabaseField(columnName = EMAIL, canBeNull = false)
  private String email;

  /**
   * ORMLite constructor.
   */
  User() {
  }

  public User(String uname, String firstname, String lastname, String eMail) {
    username = uname;
    firstName = firstname;
    lastName = lastname;
    email = eMail;
  }

  public User(String uname, String firstname, String lastname, String passWord, String eMail) {
    username = uname;
    firstName = firstname;
    lastName = lastname;
    email = eMail;
    passwordHash = UserRepository.hashPassword(passWord);
  }


  public String getUsername() {
    return username;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }


  public String getPasswordHash() {
    return passwordHash;
  }

  public boolean getAdminAccess() {
    return adminAccess;
  }

  public void setAdminAccess(boolean access) {
    adminAccess = access;
  }
}
