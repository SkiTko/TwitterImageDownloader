package com.example.service

import com.typesafe.scalalogging.Logger
import twitter4j._

import scala.annotation.tailrec
import scala.collection.JavaConversions._


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
    def streamByList(ownerScreenName: String, listName: String, listener: StatusListener) = {

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
        val newList = listMembers ++ result
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

  }

}
