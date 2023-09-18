package uk.ac.soton.ecs.runwayredeclarationtool.data.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Aircraft;

import java.sql.SQLException;
import java.util.List;

public class AircraftRepository {
  private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/db/AircraftInfo.db";
  private Dao<Aircraft, String> aircraftDao;


  public Aircraft getAircraftFromModel(String model) {

    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // setup our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }

    try {
      // construct a query using the QueryBuilder
      QueryBuilder<Aircraft, String> statementBuilder = aircraftDao.queryBuilder();
      // find any rows where airport column == provided icao code
      statementBuilder.where().like(Aircraft.MODEL, model);
      return aircraftDao.query(statementBuilder.prepare()).get(0);
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }
    return null;
  }

  public List<Aircraft> getAllAircrafts() {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // setup our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception throwables) {
      throwables.printStackTrace();
    }

    try {
      QueryBuilder<Aircraft, String> statementBuilder = aircraftDao.queryBuilder();
      statementBuilder.selectColumns("model", "blastDist");
      return aircraftDao.query(statementBuilder.prepare());
    } catch (Exception e) {
      System.err.println("Failed to read aircraft database");
      e.printStackTrace();
    }
    return null;
  }

  public void removeAircraftByModel (Aircraft aircraft){
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read aircraft database");
      e.printStackTrace();
    }
    try {
      DeleteBuilder<Aircraft, String> statementBuilder = aircraftDao.deleteBuilder();
      statementBuilder.where().eq(Aircraft.MODEL, aircraft.getModel());
      statementBuilder.delete();
    } catch (SQLException sqle) {
      System.err.println("Failed to remove aircraft.");
      sqle.printStackTrace();
    }
  }

  public void addAircraft (Aircraft aircraft) {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read aircraft database");
      e.printStackTrace();
    }
    try {
      aircraftDao.create(aircraft);
    } catch (SQLException sqle) {
      System.err.println("Failed to add aircraft.");
      sqle.printStackTrace();
    }
  }

  private void setupDatabase(ConnectionSource connectionSource) throws Exception {
    aircraftDao = DaoManager.createDao(connectionSource, Aircraft.class);
  }

}
