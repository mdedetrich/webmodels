package org.mdedetrich.webmodels

import scala.concurrent.Future

/**
  * Helper for services that need to retrieve an [[OAuth2Token]]
  * @param provider
  */
final case class OAuth2TokenProvider(value: () => Future[OAuth2Token]) extends AnyVal
