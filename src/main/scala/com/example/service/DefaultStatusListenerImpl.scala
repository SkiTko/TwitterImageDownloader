package com.example.service

import com.typesafe.scalalogging.Logger
import twitter4j.{StallWarning, Status, StatusDeletionNotice, StatusListener}

/**
  * Created by takao on 2016/11/04.
  */


/**
  *
  * @param onStatusImpl
  * @param onExceptionImpl
  */
class DefaultStatusListenerImpl(onStatusImpl: Option[(Status => Unit)] = None,
                                onExceptionImpl: Option[(Exception => Unit)] = None) extends StatusListener {


  private[this] val logger = Logger[DefaultStatusListenerImpl]


  override def onStallWarning(warning: StallWarning): Unit = {
    logger.info(s"onStallWarning: ${warning.toString}")
  }

  override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
    logger.info(s"onDeletionNotice: ${statusDeletionNotice.toString}")
  }

  override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {
    logger.info(s"onScrubGeo: $userId, $upToStatusId")
  }

  override def onStatus(status: Status): Unit = {
    onStatusImpl match {
      case Some(impl) =>
        impl(status)

      case None =>
        val actualStatus = if (status.isRetweet) {
          status.getRetweetedStatus
        }
        else {
          status
        }

        val url = s"https://twitter.com/${actualStatus.getUser.getScreenName}/status/${actualStatus.getId}"
        val mediaUrls = actualStatus.getMediaEntities.map(e => s"${e.getMediaURLHttps}:orig")

        logger.info(s"onStatus: $url , ${mediaUrls.mkString(", ")}")
    }
  }

  override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
    logger.info(s"onTrackLimitationNotice: $numberOfLimitedStatuses")
  }

  override def onException(ex: Exception): Unit = {
    onExceptionImpl match {
      case Some(impl) => impl(ex)
      case None => logger.info(s"onException: $ex")
    }
  }
}
