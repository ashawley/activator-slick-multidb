import org.specs2.mutable.Specification

import slick.jdbc.JdbcBackend.Database

class ConfigSpec extends Specification {

  "H2" should {

    "have a config" in {
      Database.forConfig("h2") must beAnInstanceOf[Database]
    }
  }

  "SQLite" should {

    "have a config" in {
      Database.forConfig("sqlite") must beAnInstanceOf[Database]
    }
  }
}
