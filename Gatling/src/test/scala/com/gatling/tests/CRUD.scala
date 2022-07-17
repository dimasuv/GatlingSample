package com.gatling.tests

import io.gatling.core.Predef.*
import io.gatling.http.Predef.*
import io.gatling.jdbc.Predef.*

import scala.concurrent.duration.*

class CRUD extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:3001")
    .acceptHeader("*/*")
    .userAgentHeader("PostmanRuntime/7.29.0")

  private val headers_default = Map(
    "Content-Type" -> "application/json",
    "User-Agent" -> "PostmanRuntime/7.29.0",
    "Accept" -> "*/*",
  )

  private val headers_token = Map(
    "Content-Type" -> "application/json",
    "User-Agent" -> "PostmanRuntime/7.29.0",
    "Accept" -> "*/*",
    "Cookie" -> "token=a7a37ee51d82a2b"
  )

  private val headers_4 = Map("Cookie" -> "token=a7a37ee51d82a2b")

  private val scn = scenario("CRUD")
    .exec(
      http("Booking - GenerateToken")
        .post("/auth")
        .headers(headers_default)
        .body(RawFileBody("0000_request.json"))
    )
    .pause(2)
    .exec(
      http("Booking - CreateBooking")
        .post("/booking/")
        .headers(headers_default)
        .body(RawFileBody("0001_request.json"))
        .check(status.is(200))
        .check(jsonPath("$.bookingid")
          .saveAs("bookingid")))
    .pause(3)
    .exec(
      http("Booking - GetBooking")
        .get("/booking/${bookingid}")
        .headers(headers_default)
        .check(status.is(200))
    )
    .pause(2)
    .exec(
      http("Booking - UpdateBooking")
        .put("/booking/${bookingid}")
        .headers(headers_token)
        .body(RawFileBody("0003_request.json"))
        .check(status.is(200))
    )
    .pause(2)
    .exec(
      http("Booking - DeleteBooking")
        .delete("/booking/${bookingid}")
        .headers(headers_token)
        .check(status.is(201))
    )
  setUp(scn.inject(atOnceUsers(5))).protocols(httpProtocol)
}
