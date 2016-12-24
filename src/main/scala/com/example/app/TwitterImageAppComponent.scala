package com.example.app

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.javadsl.SourceQueueWithComplete
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape, OverflowStrategy}
import com.example.entity.MediaTweet
import com.example.service.{DefaultStatusListenerImpl, TwitterServiceComponent}
import com.typesafe.scalalogging.Logger
import twitter4j._

import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by takao on 2016/11/03.
  */

trait TwitterImageAppComponent {
  this: ConfigComponent with
    TwitterServiceComponent with
    FlowsComponent =>


  val twitterImageApp: TwitterImageApp


  class TwitterImageApp {

    implicit val actorSystem = ActorSystem("TwitterImage")
    implicit val materializer = ActorMaterializer()
    private[this] val logger = Logger[TwitterImageApp]


    def execute(): Unit = {


      // Akka Stream の Source
      val src = Source.queue[Status](8, OverflowStrategy.backpressure)

      // Akka Stream の Sink
      val sink = Sink.foreach[MediaTweet] { mt =>
        logger.info(s"https://twitter.com/${mt.user.getScreenName}/status/${mt.statusId}, ${mt.uris}")
      }

      // Stream を実行
      val streamQueue = src
        .via(flows.resolveRT)
        .via(flows.filterNotHasMedia)
        .via(flows.extractMedia)
        .via(flows.downloadMedias(config.mediaDir))
        .to(sink)
        .run()


      def onStatus(status: Status): Unit = {
        streamQueue.offer(status)
      }

      config.mode match {
        case "hashtag" =>
          twitterService.streamByTrack(config.hashTag.get, new DefaultStatusListenerImpl(Some(onStatus)))

        case "list" =>
          twitterService.streamByList(config.listOwner.get, config.listName.get, new DefaultStatusListenerImpl(Some(onStatus)))

        case "favorites" =>
          twitterService.getFavorites(None, onStatus)

        case _ =>
          logger.error("設定が無いのです…")
          actorSystem.terminate()
      }
      Await.result(actorSystem.whenTerminated, Duration.Inf)
    }
  }

}
