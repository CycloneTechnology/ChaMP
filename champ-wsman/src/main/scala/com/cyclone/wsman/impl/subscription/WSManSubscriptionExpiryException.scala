package com.cyclone.wsman.impl.subscription

import scala.util.control.NoStackTrace

/**
  * Thrown when a subscription expires (typically over push delivery) e.g. when a heart beat is missed.
  *
  * Will be sent to the onError method of observers.
  */
case object WSManSubscriptionExpiryException extends Exception with NoStackTrace