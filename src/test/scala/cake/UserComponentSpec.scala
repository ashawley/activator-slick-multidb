import org.specs2.concurrent.ExecutionEnv

import slick.driver.H2Driver
import slick.driver.SQLiteDriver
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcDataSource

class UserComponentSpec(implicit context: ExecutionEnv) extends DatabaseSpec {
  
  val h2user = new { val driver = H2Driver } with UserComponent with DriverComponent with PictureComponent 
  val sqliteuser = new { val driver = SQLiteDriver } with UserComponent with DriverComponent with PictureComponent

  "H2" should {

    "have a UserComponent" in {
      h2user must beAnInstanceOf[UserComponent]
    }
    
    "insert user" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """insert into "USERS" ("USER_NAME","PIC_ID")  values (?,?)"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString, any[Array[String]])
        .returns(preparedStatement)
      preparedStatement.executeUpdate().returns(1)
      preparedStatement.getGeneratedKeys().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getInt(1).returns(0)

      // Run and check the future
      val u = User("name1", Picture("http://pics/default", Some(0)), Some(0))
      db.run {
        h2user.insert(u)
      } must beEqualTo(u).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)), any[Array[String]])
    }

    "user head" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """select "USER_NAME", "PIC_ID", "USER_ID" from "USERS""""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)
      preparedStatement.getResultSet().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getString(1).returns("")
      resultSet.getInt(2).returns(0)
      resultSet.getInt(3).returns(0)

      // Run and check the future
      db.run {
        import h2user.driver.api._
        h2user.users.result.head
      } must beEqualTo(("", 0, Some(0))).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }
    
  }

  "SQLite" should {

    "have a UserComponent" in {
      sqliteuser must beAnInstanceOf[UserComponent]
    }

    "insert user" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """insert into "USERS" ("USER_NAME","PIC_ID")  values (?,?)"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString, any[Array[String]])
        .returns(preparedStatement)
      preparedStatement.executeUpdate().returns(1)
      preparedStatement.getGeneratedKeys().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getInt(1).returns(0)

      // Run and check the future
      val u = User("name1", Picture("http://pics/default", Some(0)), Some(0))
      db.run {
        sqliteuser.insert(u)
      } must beEqualTo(u).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)), any[Array[String]])
    }

    "user head" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """select "USER_NAME", "PIC_ID", "USER_ID" from "USERS""""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString).returns(preparedStatement)
      preparedStatement.execute().returns(true)
      preparedStatement.getResultSet().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)
      resultSet.getString(1).returns("")
      resultSet.getInt(2).returns(0)
      resultSet.getInt(3).returns(0)

      // Run and check the future
      db.run {
        import sqliteuser.driver.api._
        sqliteuser.users.result.head
      } must beEqualTo(("", 0, Some(0))).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }
  }
}
