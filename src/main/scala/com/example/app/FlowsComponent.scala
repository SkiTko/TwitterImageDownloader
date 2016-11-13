package com.example.app

import java.io.FileOutputStream
import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink}
import com.example.entity.MediaTweet
import com.example.service.HttpServiceComponent
import com.typesafe.scalalogging.Logger
import twitter4j.Status

import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by takao on 2016/11/04.
  */

trait FlowsComponent {
  this: HttpServiceComponent =>


  val flows: Flows


  class Flows {


    private[this] val logger = Logger[Flows]


    val resolveRT = Flow.fromFunction((s: Status) => {
      if (s.isRetweet) {
        s.getRetweetedStatus
      } else {
        s
      }
    })


    val filterNotHasMedia = Flow[Status].filter(s => s.getMediaEntities.nonEmpty)


    val extractMedia = Flow.fromFunction((s: Status) => {
      val uris = s.getExtendedMediaEntities.map { me => me.getMediaURLHttps }.toSeq
      MediaTweet(s.getId, s.getUser, uris)
    })


    def downloadMedias(directory: String)(implicit actorSystem: ActorSystem, materializer: Materializer) = {
      implicit val executionContext = actorSystem.dispatcher

      Flow.fromFunction((mt: MediaTweet) => {
        mt.uris.foreach { uri =>

          val pathName = s"$directory/${extractFileName(uri)}"
          val url = s"$uri:orig"
          httpService.download(url, pathName)
        }
        mt
      })
    }


    private def extractFileName(uri: String): String = {
      val _uri = new URI(uri)
      val paths = _uri.getPath.split("/")
      paths.last
    }

  }

}


