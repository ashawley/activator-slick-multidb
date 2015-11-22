import org.specs2.concurrent.ExecutionEnv

import slick.driver.H2Driver
import slick.driver.SQLiteDriver
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcDataSource

class DAOSpec(implicit context: ExecutionEnv) extends DatabaseSpec {
  
  val h2dao = new DAO(H2Driver)
  val sqlitedao = new DAO(SQLiteDriver)

  "H2" should {

    "have a DAO" in {
      h2dao must beAnInstanceOf[DAO]
    }

    "create" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]

      val sql = """
          |create table "PROPS" ("KEY" VARCHAR NOT NULL PRIMARY KEY,
          |"VALUE" VARCHAR NOT NULL)
          |""".stripMargin.replace("\n", "")

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)

      // Run and check the future
      db.run {
        h2dao.create
      } must beEqualTo((): Unit).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

    "insert property" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """insert into "PROPS" ("KEY","VALUE")  values (?,?)"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.executeUpdate().returns(1)
      preparedStatement.getGeneratedKeys().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getString(1).returns("")

      // Run and check the future
      db.run {
        h2dao.insert("foo", "bar")
      } must beEqualTo(1).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

    "get property" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """select "VALUE" from "PROPS" where "KEY" = 'k'"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)
      preparedStatement.getResultSet().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getString(1).returns("")

      // Run and check the future
      db.run {
        h2dao.get("k")
      } must beEqualTo(Some("")).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

  }

  "SQLite" should {

    "have a DAO" in {
      sqlitedao must beAnInstanceOf[DAO]
    }

    "create" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]

      val sql = """create table "PROPS" ("KEY" VARCHAR(254) PRIMARY
        | KEY NOT NULL,"VALUE" VARCHAR(254) NOT NULL)
        |""".stripMargin.replace("\n", "")

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)

      // Run and check the future
      db.run {
        sqlitedao.create
      } must beEqualTo((): Unit).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

    "insert property" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """insert into "PROPS" ("KEY","VALUE")  values (?,?)"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.executeUpdate().returns(1)
      preparedStatement.getGeneratedKeys().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getString(1).returns("")

      // Run and check the future
      db.run {
        sqlitedao.insert("k", "v")
      } must beEqualTo(1).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

    "get property" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """select "VALUE" from "PROPS" where "KEY" = 'k'"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)
      preparedStatement.getResultSet().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getString(1).returns("")

      // Run and check the future
      db.run {
        sqlitedao.get("k")
      } must beEqualTo(Some("")).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

  }
}
