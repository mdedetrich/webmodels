package org.mdedetrich.webmodels

import io.circe._
import io.circe.syntax._

object circe {
  implicit val correlationIdDecoder: Decoder[CorrelationId] = Decoder[String].map(CorrelationId)
  implicit val correlationIdEncoder: Encoder[CorrelationId] = Encoder.instance[CorrelationId](_.id.asJson)

  implicit val flowIdDecoder: Decoder[FlowId] = Decoder[String].map(FlowId)
  implicit val flowIdEncoder: Encoder[FlowId] = Encoder.instance[FlowId](_.id.asJson)

  implicit val oAuth2TokenDecoder: Decoder[OAuth2Token] = Decoder[String].map(OAuth2Token)
  implicit val oAuth2TokenEncoder: Encoder[OAuth2Token] = Encoder.instance[OAuth2Token](_.token.asJson)

  implicit val problemDecoder: Decoder[Problem] = Decoder.instance[Problem] { c =>
    for {
      jsonObject      <- c.as[JsonObject]
      problemType     <- c.downField("type").as[Option[String]]
      problemTitle    <- c.downField("title").as[String]
      problemStatus   <- c.downField("status").as[Option[Int]]
      problemDetail   <- c.downField("detail").as[Option[String]]
      problemInstance <- c.downField("instance").as[Option[String]]
      asMap           = jsonObject.toMap
      extraFields = asMap.filterKeys {
        case "type" | "title" | "status" | "detail" | "instance" => false
        case _                                                   => true
      }.asJsonObject
    } yield
      Problem(
        problemType,
        problemTitle,
        problemStatus,
        problemDetail,
        problemInstance,
        extraFields
      )
  }

  implicit val problemEncoder: Encoder[Problem] = Encoder.instance[Problem] { x =>
    val base = Json.obj(
      "type"     -> x.`type`.asJson,
      "title"    -> x.title.asJson,
      "status"   -> x.status.asJson,
      "detail"   -> x.detail.asJson,
      "instance" -> x.instance.asJson
    )

    base deepMerge x.extraFields.asJson
  }

  implicit val requestIdDecoder: Decoder[RequestId] = Decoder[String].map(RequestId)
  implicit val requestIdEncoder: Encoder[RequestId] = Encoder.instance[RequestId](_.id.asJson)
}
