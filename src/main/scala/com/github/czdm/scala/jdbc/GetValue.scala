package com.github.czdm.scala.jdbc

import java.sql.ResultSet
import java.time.{LocalDate, LocalDateTime, LocalTime}
import scala.annotation.targetName
import scala.compiletime.{erasedValue, summonInline}
import scala.deriving.Mirror

trait GetValue[T] {
  def get(rs: ResultSet, index: Int): T
}

object GetValue {
  given GetValue[Boolean] = (rs: ResultSet, index: Int) => rs.getBoolean(index)

  given GetValue[Byte] = (rs: ResultSet, index: Int) => rs.getByte(index)

  given GetValue[Short] = (rs: ResultSet, index: Int) => rs.getShort(index)

  given GetValue[Int] = (rs: ResultSet, index: Int) => rs.getInt(index)

  given GetValue[Long] = (rs: ResultSet, index: Int) => rs.getLong(index)

  given GetValue[Float] = (rs: ResultSet, index: Int) => rs.getFloat(index)

  given GetValue[Double] = (rs: ResultSet, index: Int) => rs.getDouble(index)

  given GetValue[String] = (rs: ResultSet, index: Int) => rs.getString(index)

  given GetValue[LocalDate] = (rs: ResultSet, index: Int) => rs.getDate(index).toLocalDate

  given GetValue[LocalTime] = (rs: ResultSet, index: Int) => rs.getTime(index).toLocalTime

  given GetValue[LocalDateTime] = (rs: ResultSet, index: Int) => rs.getTimestamp(index).toLocalDateTime
}

// implement GetValue for tuples
trait GetValueTuple[T <: Tuple] {
  def get(rs: ResultSet, index: Int): T
}

object GetValueTuple {
  @targetName("cons")
  inline given [H: GetValue, T <: Tuple](using inline ev: GetValueTuple[T]): GetValueTuple[H *: T] =
    (rs: ResultSet, index: Int) => summonInline[GetValue[H]].get(rs, index) *: ev.get(rs, index + 1)

  inline given GetValueTuple[EmptyTuple] = (rs: ResultSet, index: Int) => EmptyTuple
}

// implement GetRow for tuples and derive for case classes
trait GetRow[T] {
  def get(rs: ResultSet): T
}

object GetRow {
  inline given tupleGetRow[T <: Tuple](using inline ev: GetValueTuple[T]): GetRow[T] =
    (rs: ResultSet) => ev.get(rs, 1)

  inline given derived[T](using m: Mirror.ProductOf[T], gen: GetValueTuple[m.MirroredElemTypes]): GetRow[T] =
    (rs: ResultSet) => m.fromProduct(gen.get(rs, 1))
}

extension (rs: ResultSet) {
  def get[T](index: Int)(using getter: GetValue[T]) = getter.get(rs, index)

  def get[T](using getter: GetRow[T]) = getter.get(rs)
}
