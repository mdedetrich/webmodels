package org.mdedetrich.webmodels

import circe._
import io.circe._
import io.circe.syntax._

/** `ResponseContent` provides a convenient abstraction for working with REST'ful HTTP
  * API's that return RFC3986 Problem in error cases. `ResponseContent` makes the
  * assumption that the web services that you work with do mainly return RFC3986
  * Problem however `ResponseContent` also provides fallback data types
  * (`ResponseContent.JSON`/`ResponseContent.String`) that lets you easily handle
  * cases where the response of a request isn't a valid Problem JSON (such cases
  * are not uncommon when you have load balancer's/reverse proxies sitting infront of
  * webserver's).
  */
sealed abstract class ResponseContent extends Product with Serializable {

  /** Checks to see if the [[ResponseContent]] is JSON and contains a JSON field that satisfies a predicate.
    *
    * @param field The JSON field to check
    * @param predicate The predicate
    * @return Whether the predicate was satisfied. Always returns `false` if the this is a [[ResponseContent.String]]
    */
  def checkJsonField(field: String, predicate: Json => Boolean): Boolean =
    this match {
      case ResponseContent.Problem(problem) =>
        problem.asJson.findAllByKey(field).exists(predicate)
      case ResponseContent.Json(json) =>
        json
          .findAllByKey(field)
          .exists(predicate)
      case _: ResponseContent.String => false
    }

  /** A combination of [[checkJsonField]] that also checks if the resulting
    * JSON field is a String that satisfies a predicate.
    * @param field The JSON field to look for
    * @param predicate The predicate to satisfy
    */
  def checkJsonFieldAsString(field: String, predicate: String => Boolean): Boolean =
    checkJsonField(field, _.asString.exists(predicate))

  /** Checks to see if the [[ResponseContent]] contains a specific String, regardless
    * in what format its stored
    */
  def checkString(predicate: String => Boolean): Boolean =
    this match {
      case ResponseContent.Problem(problem) =>
        predicate(problem.asJson.noSpaces)
      case ResponseContent.Json(json) =>
        predicate(json.noSpaces)
      case ResponseContent.String(string) =>
        predicate(string)
    }
}

object ResponseContent {

  /** This case happens if the response of the Http request is a valid Problem according to
    * RFC7807. This means that the JSON response content is a JSON object that contains the field named `type`
    * and all other fields (if they exist) satisfy the RFC3986 specification (i.e. the `type` field is
    * valid URI)
    * @see https://tools.ietf.org/html/rfc7807
    */
  final case class Problem(problem: org.mdedetrich.webmodels.Problem) extends ResponseContent

  /** This case happens if the response of the HTTP request is JSON but it sn't a valid RFC3986 Problem.
    * This means that either the mandatory `type` field isn't in the JSON response and/or the other fields
    * specific to Problem don't follow all of the RFC3986 specification (i.e. the `type` field is
    * not a valid URI)
    * @see https://tools.ietf.org/html/rfc7159
    */
  final case class Json(json: io.circe.Json) extends ResponseContent

  /** This case happens if the body content is not valid JSON according to RFC7159
    */
  final case class String(string: java.lang.String) extends ResponseContent
}

final case class Header(name: String, value: String)

/** The purpose of this data type is to provide a common way of dealing
  * with errors from REST'ful HTTP APi's making it particularly useful
  * for strongly typed clients to web services.
  *
  * `HttpServiceError` makes no assumptions about what HTTP client you
  * happen to be using which makes it a great candidate for having a
  * common error type in projects that have to juggle with
  * multiple HTTP clients. Since `HttpServiceError` is a trait, it can easily be
  * extended with existing error types that your library/application may happen
  * to have.
  *
  * Due to the fact that `HttpServiceError` is meant abstract over different HTTP
  * clients, it exposes methods that provides the minimum necessary data commonly
  * needed to properly identify errors without exposing too much about the HTTP
  * client itself. Examples of such methods are `statusCode`, `responseContent`
  * and `responseHeaders`.
  */
trait HttpServiceError {

  /** Type Type of the HttpRequest object from the original Http Client
    */
  type HttpRequest

  /** The type of the HttpResponse object from the original Http Client
    */
  type HttpResponse

  /** The original request that gave this response
    */
  def request: HttpRequest

  /** The original response
    */
  def response: HttpResponse

  /** The content of the response represented as a convenient
    * data type
    */
  def responseContent: ResponseContent

  /** The status code of the response
    */
  def statusCode: Int

  /** Indicates whether this error is due to a missing resource, i.e. 404 case
    */
  def resourceMissingError: Boolean = statusCode.toString.startsWith("404")

  /** Indicates whether this error was caused due to a client error (i.e.
    * the client is somehow sending a bad request). Retrying such requests
    * are often pointless.
    */
  def clientError: Boolean = Platform.checkFirstDigitOfInt(4, statusCode)

  /** Indicates whether this error was caused due to a server problem.
    * Such requests are often safe to retry (ideally with an exponential delay)
    * as long as the request is idempotent.
    */
  def serverError: Boolean = Platform.checkFirstDigitOfInt(5, statusCode)

  /** The headers of the response without any alterations made
    * (i.e. any duplicate fields/ordering should remained untouched
    * from the original response).
    */
  def responseHeaders: IndexedSeq[Header]
}
