package org.letgo.assignments.letshout.exceptions

final case class ClientAuthenticationException(
                                        private val message: String = "",
                                        private val cause: Throwable = None.orNull
                                      )
  extends Exception(message, cause)
