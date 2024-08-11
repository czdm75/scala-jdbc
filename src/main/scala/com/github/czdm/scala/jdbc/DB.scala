package com.github.czdm.scala.jdbc

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import scala.collection.mutable
import scala.util.Try
import scala.util.Using

trait DB(conn: Connection) {
  def prepareStmt(template: SqlTemplate): PreparedStatement = {
    val stmt = conn.prepareStatement(template.sql)
    template.params.zipWithIndex.foreach { case (f, i) =>
      f.apply(stmt, i + 1)
    }
    stmt
  }

  def select[T](template: SqlTemplate)(using GetRow[T]): Seq[T] = {
    Using.resource(prepareStmt(template))(select)
  }

  def select[T](stmt: PreparedStatement)(using GetRow[T]): Seq[T] = {
    Using.resource(stmt.executeQuery())(select)
  }

  def select[T](rs: ResultSet)(using GetRow[T]): Seq[T] = {
    Iterator.iterate[(Option[T], ResultSet)]((None, rs)) {
      case (value, rs) => if (rs.next()) then (Some(rs.get[T]), rs) else (None, rs)
    }.drop(1).takeWhile(_._1.isDefined).flatMap(_._1).toSeq
  }

  def update(template: SqlTemplate): Int = Using.resource(prepareStmt(template))(_.executeUpdate())

  def update(stmt: PreparedStatement): Int = stmt.executeUpdate()
}

case class AutoCloseDB(conn: Connection) extends DB(conn) with AutoCloseable {
  override def close(): Unit = conn.close()
}

case class SimpleDB(conn: Connection) extends DB(conn)

object DB {
  def apply(driverClass: String, url: String, user: String, password: String): AutoCloseDB =
    DB(driverClass, url, Some(user, password))

  def apply(driverClass: String, url: String, userPassword: Option[(String, String)] = None): AutoCloseDB = {
    Class.forName(driverClass)
    userPassword match {
      case Some(user, password) => AutoCloseDB(DriverManager.getConnection(url, user, password))
      case None                 => AutoCloseDB(DriverManager.getConnection(url))
    }
  }

  def apply(conn: Connection): DB = SimpleDB(conn)
}