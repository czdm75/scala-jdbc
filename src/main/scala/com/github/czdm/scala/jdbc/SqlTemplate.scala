package com.github.czdm.scala.jdbc

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.util.Try

case class SqlTemplate(sql: String, params: ValueSetter*)

object SqlTemplate {
  extension (sc: StringContext) {
    def sql(args: ValueSetter*): SqlTemplate = SqlTemplate(sc.parts.mkString("?"), args *)
  }
}
