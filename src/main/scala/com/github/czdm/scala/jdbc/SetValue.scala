package com.github.czdm.scala.jdbc

import java.sql.{PreparedStatement, Date, Time, Timestamp}
import java.time.{LocalDate, LocalTime, LocalDateTime}

opaque type ValueSetter = (PreparedStatement, Int) => Unit

extension (setter: ValueSetter) {
  def apply(stmt: PreparedStatement, index: Int): Unit = setter.apply(stmt, index)
}

trait SetValue[T] extends Conversion[T, ValueSetter] {
  def set(stmt: PreparedStatement, index: Int, value: T): Unit

  final override def apply(x: T): ValueSetter =
    ((stmt: PreparedStatement, index: Int) => set(stmt, index, x)).asInstanceOf[ValueSetter]
}

object SetValue {
  
  given SetValue[Boolean] = (stmt: PreparedStatement, index: Int, value: Boolean) => stmt.setBoolean(index, value)

  given SetValue[Byte] = (stmt: PreparedStatement, index: Int, value: Byte) => stmt.setByte(index, value)

  given SetValue[Short] = (stmt: PreparedStatement, index: Int, value: Short) => stmt.setShort(index, value)

  given SetValue[Int] = (stmt: PreparedStatement, index: Int, value: Int) => stmt.setInt(index, value)

  given SetValue[Long] = (stmt: PreparedStatement, index: Int, value: Long) => stmt.setLong(index, value)

  given SetValue[Float] = (stmt: PreparedStatement, index: Int, value: Float) => stmt.setFloat(index, value)

  given SetValue[Double] = (stmt: PreparedStatement, index: Int, value: Double) => stmt.setDouble(index, value)

  given SetValue[BigDecimal] = (stmt: PreparedStatement, index: Int, value: BigDecimal) => stmt.setBigDecimal(index, value.bigDecimal)

  given SetValue[LocalDate] = (stmt: PreparedStatement, index: Int, value: LocalDate) => stmt.setDate(index, Date.valueOf(value))

  given SetValue[LocalTime] = (stmt: PreparedStatement, index: Int, value: LocalTime) => stmt.setTime(index, Time.valueOf(value))

  given SetValue[LocalDateTime] = (stmt: PreparedStatement, index: Int, value: LocalDateTime) => stmt.setTimestamp(index, Timestamp.valueOf(value))

  given SetValue[String] = (stmt: PreparedStatement, index: Int, value: String) => stmt.setString(index, value)
}