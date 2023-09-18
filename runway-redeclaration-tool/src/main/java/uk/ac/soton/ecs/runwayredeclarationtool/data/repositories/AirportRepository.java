package uk.ac.soton.ecs.runwayredeclarationtool.data.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Runway;

public class AirportRepository {

  // private static final String DATABASE_URL = "jdbc:sqlite:AirportInfo.db";
  private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/db/AirportInfo.db";
  private Dao<Runway, String> airportDao;


  /**
   * Gets all runways (rows in db) from a provided ICAO code
   *
   * @param icao code for airport
   * @return all the runways associated with that airport
   */
  public List<Runway> getRunwaysFromIcao(String icao) {

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
      QueryBuilder<Runway, String> statementBuilder = airportDao.queryBuilder();
      // find any rows where airport column == provided icao code
      statementBuilder.where().like(Runway.AIRPORT, icao);
      return airportDao.query(statementBuilder.prepare());
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Retrieve all airports, returning the ICAO code for them (first column in db)
   *
   * @return ICAO code for each aiport (list with duplicates)
   */
  public List<Runway> getAirports() {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // setup our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception throwables) {
      throwables.printStackTrace();
    }

    try {
      // should query for only the airport column of each entry in db
      QueryBuilder<Runway, String> statementBuilder = airportDao.queryBuilder();
      statementBuilder.selectColumns("airport");
      return airportDao.query(statementBuilder.prepare());
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }
    return null;
  }

  public List<Runway> getAllRunways() {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // setup our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception throwables) {
      throwables.printStackTrace();
    }

    try {
      // should query for only the airport column of each entry in db
      QueryBuilder<Runway, String> statementBuilder = airportDao.queryBuilder();
      statementBuilder.selectColumns(Runway.AIRPORT, Runway.RUNWAY_DESIGNATOR, Runway.TORA, Runway.TODA, Runway.ASDA,
              Runway.LDA, Runway.DISPLACED_THRESHOLD);
      return airportDao.query(statementBuilder.prepare());
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }
    return null;
  }

  public List<Runway> getCurrentRunway(String icao, String runwayDesignator) {

    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }

    try {
      // construct a query using the QueryBuilder
      QueryBuilder<Runway, String> statementBuilder = airportDao.queryBuilder();
      // find any rows where airport column == provided icao code AND runwaydesignator column == provided runway designation code
      statementBuilder.where().like(Runway.AIRPORT, icao).and()
          .like(Runway.RUNWAY_DESIGNATOR, runwayDesignator);
      return airportDao.query(statementBuilder.prepare());
    } catch (Exception e) {
      System.err.println("Failed to read database");
      e.printStackTrace();
    }
    return null;
  }

  public void removeRunway(Runway runway){
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read runway database");
      e.printStackTrace();
    }
    try {
      DeleteBuilder<Runway, String> statementBuilder = airportDao.deleteBuilder();
      statementBuilder.where().eq(Runway.AIRPORT, runway.getAirport()).and()
              .eq(Runway.RUNWAY_DESIGNATOR, runway.getRunwayDesignator());
      statementBuilder.delete();
    } catch (SQLException sqle) {
      System.err.println("Failed to remove runway.");
      sqle.printStackTrace();
    }
  }

  public void addRunway(Runway runway) {
    try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
      // create our data-source for the database
      // set up our database and DAOs
      setupDatabase(connectionSource);
    } catch (Exception e) {
      System.err.println("Failed to read airport database");
      e.printStackTrace();
    }
    try {
      airportDao.create(runway);
    } catch (SQLException sqle) {
      System.err.println("Failed to add runway.");
      sqle.printStackTrace();
    }
  }

  /**
   * Setup DAO
   */
  private void setupDatabase(ConnectionSource connectionSource) throws Exception {
    airportDao = DaoManager.createDao(connectionSource, Runway.class);
  }

}
