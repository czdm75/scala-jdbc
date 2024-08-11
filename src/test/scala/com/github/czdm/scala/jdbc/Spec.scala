package com.github.czdm.scala.jdbc

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import java.sql.{DriverManager, ResultSet}
import scala.language.implicitConversions
import scala.util.Using
import com.github.czdm.scala.jdbc.SqlTemplate.sql
import com.github.czdm.scala.jdbc.SetValue.given
import com.github.czdm.scala.jdbc.GetValue.given

case class Data(a: Int, b: String)

class Spec extends AnyFunSuite {

  test("main") {
    Using.resource(DB("org.h2.Driver", "jdbc:h2:mem:test;TRACE_LEVEL_FILE=4")) { db =>
      val data = Data(1, "a")
      val sql  = sql"select ${data.a}, ${data.b}"

      assertResult(Seq(data))(db.select[Data](sql))
    }
  }
}