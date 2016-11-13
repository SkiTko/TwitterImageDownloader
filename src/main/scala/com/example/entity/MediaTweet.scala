package com.example.entity

import twitter4j.User

/**
  * Created by takao on 2016/11/04.
  */

/**
  *
  * @param statusId
  * @param user
  * @param uris
  */
case class MediaTweet(statusId: Long, user: User, uris: Iterable[String])
