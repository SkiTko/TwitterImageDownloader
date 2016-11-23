package com.example.service

import java.io.FileOutputStream
import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.Logger

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by takao on 2016/11/06.
  */
trait HttpServiceComponent {

  val httpService: HttpService


  class HttpService {

    private[this] val logger = Logger[HttpService]


    def download(url: String, path: String)(implicit actorSystem: ActorSystem, materializer: Materializer) = {

      val downloadTask = Http().singleRequest(HttpRequest(uri = url))
      val response = Await.result(downloadTask, 5.minutes)
      response.status match {
        case StatusCodes.OK =>
          val channel = new FileOutputStream(path).getChannel
          val task = response.entity.getDataBytes().runWith(Sink.foreach(b => channel.write(b.asByteBuffer)), materializer)
          task.andThen { case _ =>
            channel.close()
          }
        case _ => // error 無視
          logger.warn(s"ダウンロードに失敗しちゃったの: ${response.status} ($url)")
      }
    }

  }

}
