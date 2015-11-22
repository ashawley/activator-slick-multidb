import org.specs2.mutable.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.specification.AfterAll

import org.specs2.specification.ForEach
import org.specs2.execute.AsResult
import org.specs2.execute.Result

import slick.util.AsyncExecutor

import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcDataSource

case class Transaction(
  val database: Database,
  val dataSource: JdbcDataSource
)

abstract class DatabaseSpec(implicit context: ExecutionEnv)
    extends Specification
    with Mockito
    with ForEach[Transaction]
    with AfterAll {

  def foreach[R: AsResult](f: Transaction => R): Result = {
    val dataSource = mock[JdbcDataSource]
    val executor = new AsyncExecutor {
      def executionContext = context.executionContext
      def close = {}
    }
    val db = new Database(dataSource, executor)
    try AsResult(f(Transaction(db, dataSource)))
    finally { db.close }
  }

  "Database" should {
    "be a database" in { implicit t: Transaction =>

      val Transaction(db: Database, _: JdbcDataSource) = t

      db must beAnInstanceOf[Database]
    }
  }

  def afterAll = {
    Util.unloadDrivers
  }
}
