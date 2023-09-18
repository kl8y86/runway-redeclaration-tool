package uk.ac.soton.ecs.runwayredeclarationtool.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.User;

/**
 * The application's database context.
 *
 * @author George Peppard
 */
public class DatabaseContext {

  private Dao<User, String> userDao;

  public DatabaseContext(JdbcConnectionSource connection) throws SQLException {
    userDao = DaoManager.createDao(connection, User.class);

    TableUtils.createTableIfNotExists(connection, User.class);
  }
}
