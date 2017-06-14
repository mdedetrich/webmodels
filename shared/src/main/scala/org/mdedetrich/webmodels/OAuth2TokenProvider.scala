package org.mdedetrich.webmodels

import scala.concurrent.Future

/**
  * Helper for services that need to retrieve an [[OAuth2Token]]
  * @param provider
  */
case class OAuth2TokenProvider(provider: () => Future[OAuth2Token]) extends AnyVal
