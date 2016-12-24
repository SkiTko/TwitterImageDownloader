package com.example.service

import com.typesafe.scalalogging.Logger
import twitter4j._

import scala.annotation.tailrec
import scala.collection.JavaConverters._


/**
  * Created by takao on 2016/11/04.
  */
trait TwitterServiceComponent {


  val twitterService: TwitterService


  class TwitterService {


    private[this] val logger = Logger[TwitterService]


    /**
      *
      * @param tracks
      */
    def streamByTrack(tracks: Seq[String], listener: StatusListener): Unit = {

      val twitterStream = new TwitterStreamFactory().getInstance()
      twitterStream.addListener(listener)

      val query = new FilterQuery()
        .track(tracks: _*)
      twitterStream.filter(query)
    }


    /**
      *
      * @param ownerScreenName
      * @param listName
      */
    def streamByList(ownerScreenName: String, listName: String, listener: StatusListener): Unit = {

      // list のメンバーを取得
      val userListMembers = getUserListMembers(ownerScreenName, listName)
      val userIds = userListMembers.map(u => u.getId)

      // list のメンバーをフォローする Stream API を呼ぶ
      val twitterStream = new TwitterStreamFactory().getInstance()
      twitterStream.addListener(listener)

      val query = new FilterQuery()
        .follow(userIds: _*)
      twitterStream.filter(query)
    }


    /**
      * リストのメンバーを取得
      *
      * @param ownerScreenName スクリーンネーム
      * @param listName        リスト名
      * @return リストのメンバー
      */
    def getUserListMembers(ownerScreenName: String, listName: String): Seq[User] = {

      // list のメンバーを取得
      val twitter = TwitterFactory.getSingleton

      @tailrec
      def $$(cursor: Long, listMembers: List[User]): List[User] = {

        val result = twitter.list().getUserListMembers(ownerScreenName, listName, 5000, cursor)
        val newList = listMembers ++ result.asScala
        if (result.hasNext) {
          $$(result.getNextCursor, newList)
        }
        else {
          newList
        }
      }

      $$(-1L, Nil)
    }


    def getStatus(id: Long): Status = {
      val twitter = TwitterFactory.getSingleton
      twitter.showStatus(id)
    }


    def getFavorites[U](screenName: Option[String], callback: (Status => U)): Unit = {

      val twitter = TwitterFactory.getSingleton

      def getFavoritesPaging(paging: Paging) = screenName match {
        case None => twitter.getFavorites(paging)
        case Some(scrnName) => twitter.getFavorites(scrnName, paging)
      }

      @tailrec def $$(maxId: Option[Long]): Unit = {
        val paging = new Paging(1, 200)
        val responseList = maxId match {
          case None =>
            getFavoritesPaging(paging)

          case Some(_maxId) =>
            logger.info(s"maxId = ${_maxId}.")
            paging.setSinceId(1L)
            paging.setMaxId(_maxId)
            getFavoritesPaging(paging)
        }

        if (responseList.size() > 0) {
          logger.info(s"Got: ${responseList.size()}")
          responseList.asScala.foreach { status =>
            logger.info(s"Status Id = ${status.getId}")
            callback(status)
          }
          val rateLimitStatus = responseList.getRateLimitStatus
          logger.info(s"RateLimit = ${rateLimitStatus.getLimit}, Remaining = ${rateLimitStatus.getRemaining}, SecondsUntilReset = ${rateLimitStatus.getSecondsUntilReset}")
          if (rateLimitStatus.getRemaining == 0) {
            logger.info(s"Sleeping... ${rateLimitStatus.getSecondsUntilReset} seconds.")
            Thread.sleep((rateLimitStatus.getSecondsUntilReset + 5) * 1000)
          }
          val minId = responseList.asScala.map(s => s.getId).min
          $$(Some(minId - 1))
        }
        else {
          logger.info(s"Finished.")
        }
      }

      $$(None)

    }

  }

}
