package com.example.app

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._


/**
  * Created by takao on 2016/11/03.
  */
trait ConfigComponent {

  val config: Config


  class Config(underlying: com.typesafe.config.Config = ConfigFactory.load()) {

    val mode = underlying.as[String]("app.twitter.stream.mode")
    val hashTag = underlying.getAs[List[String]]("app.twitter.stream.hashtags")
    val listOwner = underlying.getAs[String]("app.twitter.stream.list.owner")
    val listName = underlying.getAs[String]("app.twitter.stream.list.name")

    val mediaDir = underlying.getAs[String]("app.media.directory").getOrElse("./")
  }

}
