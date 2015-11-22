import org.specs2.concurrent.ExecutionEnv

import slick.driver.H2Driver
import slick.driver.SQLiteDriver
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcDataSource

class PictureComponentSpec(implicit context: ExecutionEnv) extends DatabaseSpec {
  
  val h2pic = new { val driver = H2Driver } with PictureComponent with DriverComponent 
  val sqlitepic = new  { val driver = SQLiteDriver } with PictureComponent with DriverComponent

  "H2" should {

    "have a PictureComponent" in {
      h2pic must beAnInstanceOf[PictureComponent]
    }
    
    "insert picture" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """insert into "PICTURES" ("PIC_URL")  values (?)"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString, any[Array[String]])
        .returns(preparedStatement)
      preparedStatement.executeUpdate().returns(1)
      preparedStatement.getGeneratedKeys().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)

      // Run and check the future
      val p = Picture("http://pics/default", Some(0))
      db.run {
        h2pic.insert(p)
      } must beEqualTo(p).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)), any[Array[String]])
    }
    
    "picture head" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """select "PIC_URL", "PIC_ID" from "PICTURES""""

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
        import h2pic.driver.api._
        h2pic.pictures.result.head
      } must beEqualTo(Picture("", Some(0))).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }
  }

  "SQLite" should {

    "have a PictureComponent" in {
      sqlitepic must beAnInstanceOf[PictureComponent]
    }
    
    "insert picture" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """insert into "PICTURES" ("PIC_URL")  values (?)"""

      // Configure stubs
      dataSource.createConnection().returns(connection)
      connection.prepareStatement(anyString, any[Array[String]])
        .returns(preparedStatement)
      preparedStatement.executeUpdate().returns(1)
      preparedStatement.getGeneratedKeys().returns(resultSet)
      resultSet.next().returns(true).thenReturns(false)

      // Run and check the future
      val p = Picture("http://pics/default", Some(0))
      db.run {
        sqlitepic.insert(p)
      } must beEqualTo(p).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)), any[Array[String]])
    }


    "picture head" in { implicit t: Transaction =>

      val Transaction(db: Database, dataSource: JdbcDataSource) = t

      val connection = mock[java.sql.Connection]
      val preparedStatement = mock[java.sql.PreparedStatement]
      val resultSet = mock[java.sql.ResultSet]

      val sql = """select "PIC_URL", "PIC_ID" from "PICTURES""""

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
        import sqlitepic.driver.api._
        sqlitepic.pictures.result.head
      } must beEqualTo(Picture("", Some(0))).await

      // Expectations
      there was one(dataSource).createConnection()
      there was one(connection).prepareStatement(argThat(===(sql)))
    }

  }
}
