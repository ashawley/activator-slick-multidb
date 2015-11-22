import org.specs2.concurrent.ExecutionEnv

import slick.driver.H2Driver
import slick.driver.SQLiteDriver
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcDataSource

class DALSpec(implicit context: ExecutionEnv) extends DatabaseSpec {
  
  val h2dal = new DAL(H2Driver)
  val sqlitedal = new DAL(SQLiteDriver)

  "H2" should {

    "have a DAL" in {
      h2dal must beAnInstanceOf[DAL]
    }

    "create" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]

      val sql1 = """
          |create table "USERS" ("USER_NAME" VARCHAR NOT NULL,
          |"PIC_ID" INTEGER NOT NULL,"USER_ID" INTEGER GENERATED
          | BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY)
          |""".stripMargin.replace("\n", "")
      val sql2 = """
          |create table "PICTURES" ("PIC_URL" VARCHAR NOT NULL,
          |"PIC_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START
          | WITH 1) PRIMARY KEY)""".stripMargin.replace("\n", "")

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)

      // Run and check the future
      db.run {
        h2dal.create
      } must beEqualTo((): Unit).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql1)))
      there was one(connection).prepareStatement(argThat(===(sql2)))
    }

  }

  "SQLite" should {

    "have a DAL" in {
      sqlitedal must beAnInstanceOf[DAL]
    }

    "create" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]

      val sql1 = """create table "USERS" ("USER_NAME" VARCHAR(254)
        | NOT NULL,"PIC_ID" INTEGER NOT NULL,"USER_ID" INTEGER PRIMARY
        | KEY AUTOINCREMENT)""".stripMargin.replace("\n", "")

      val sql2 = """create table "PICTURES" ("PIC_URL" VARCHAR(254)
        | NOT NULL,"PIC_ID" INTEGER PRIMARY KEY AUTOINCREMENT)
        |""".stripMargin.replace("\n", "")

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)

      // Run and check the future
      db.run {
        sqlitedal.create
      } must beEqualTo((): Unit).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql1)))
      there was one(connection).prepareStatement(argThat(===(sql1)))
    }

  }
}
