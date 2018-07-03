// Copyright 2017, Yahoo Holdings Inc.
// Licensed under the terms of the Apache License 2.0. Please see LICENSE file in project root for terms.
package com.yahoo.maha.core

import java.nio.charset.StandardCharsets

import org.json4s.JValue
import org.json4s.jackson.JsonMethods.parse
import org.json4s.scalaz.JsonScalaz._
import scalaz.Validation._
/**
 * Created by hiral on 2/11/16.
 */
package object request {
  def fieldExtended[A: JSONR](name: String)(json: JValue): Result[A] = {
    //println("old: " + json)
    val result = field[A](name)(json)
    result.leftMap {
      nel =>
        nel.map {
          case UnexpectedJSONError(was, expected) =>
            UncategorizedError(name, s"unexpected value : $was expected : ${expected.getSimpleName}", List.empty)
          case a => a
        }
    }
  }

  def dynamicFieldExtended[A: JSONR](name: String)(json: JValue): Result[A] = {
    try {
      val result = field[String](name)(json)
      val leftMap = result.leftMap {
        nel =>
          nel.map {
            case UnexpectedJSONError(was, expected) =>
              UncategorizedError(name, s"unexpected value : $was expected : ${expected.getSimpleName}", List.empty)
            case a => a
          }
      }

      // TODO: Replace the following ugly code
      val defaultValue = result.toOption.get.replaceAll(".*,", "").replace(")", "").trim
      val tmpJson = parse(s"""{"key":$defaultValue}""")
      fieldExtended[A]("key")(tmpJson)
    } catch {
      case t: Throwable =>
        fieldExtended[A](name)(json)
    }
  }
}
