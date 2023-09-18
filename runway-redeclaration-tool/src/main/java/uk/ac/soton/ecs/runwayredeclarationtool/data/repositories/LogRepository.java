package uk.ac.soton.ecs.runwayredeclarationtool.data.repositories;

import com.thoughtworks.xstream.XStream;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Calculation;
import uk.ac.soton.ecs.runwayredeclarationtool.data.models.Obstacle;

public class LogRepository {

  public Calculation importFile(String name) {
    try {
      XStream xstream = new XStream();
      xstream.allowTypesByWildcard(new String[]{"uk.ac.soton.ecs.**"});
      xstream.processAnnotations(Calculation.class);

      String importFile = Files.readString(Path.of(name + ".xml"));

      return (Calculation) xstream.fromXML(importFile);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void exportFile(Calculation calculation, String name) {
    try {
      XStream xstream = new XStream();
      File outputFile = new File(name + ".xml");
      outputFile.createNewFile();
      OutputStream os = new FileOutputStream(outputFile);
      xstream.alias("calculation", Calculation.class);
      xstream.alias("obstacle", Obstacle.class);
      xstream.alias("aircraft", Aircraft.class);
      xstream.omitField(User.class, "passwordHash");
      String dataXml = xstream.toXML(calculation);
      os.write(dataXml.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
