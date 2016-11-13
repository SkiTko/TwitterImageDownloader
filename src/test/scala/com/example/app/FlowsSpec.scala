package com.example.app

import java.util.Date

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.testkit.{ImplicitSender, TestKit}
import com.example.app.FlowsSpec.TwitterStatusImpl
import com.example.service.HttpServiceComponent
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import twitter4j._
import twitter4j.{Status => TwitterStatus}

import scala.concurrent.Await
import scala.concurrent.duration._


class FlowsSpec extends
  TestKit(ActorSystem("FlowsSpec")) with
  ImplicitSender with
  WordSpecLike with
  Matchers with
  BeforeAndAfterAll with
  FlowsSpec.TestComponentRegistry {


  override val flows = new Flows
  override val httpService = new HttpService
  implicit val materializer = ActorMaterializer()


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  /**
    * resolveRT RTでない
    */
  "Flows" must {

    "resolveRT" in {

      // 準備
      val sinkUnderTest = flows.resolveRT.toMat(Sink.fold(List.empty[TwitterStatus])((u, t) => u :+ t))(Keep.right)
      val (ref, future) = Source.actorRef(8, OverflowStrategy.fail).toMat(sinkUnderTest)(Keep.both).run()
      val noRTStatus = new TwitterStatusImpl {
        override def isRetweet: Boolean = false

        override def getText: String = "はわわ～"

        override def getRetweetedStatus: twitter4j.Status = new TwitterStatusImpl {
          override def getText: String = "にゃしぃ"
        }
      }
      val rtStatus = new TwitterStatusImpl {
        override def isRetweet: Boolean = true

        override def getText: String = "はわわ～"

        override def getRetweetedStatus: twitter4j.Status = new TwitterStatusImpl {
          override def getText: String = "にゃしぃ"
        }
      }



      // 実行
      ref ! noRTStatus
      ref ! rtStatus
      ref ! akka.actor.Status.Success("おしまい")



      // 検証
      val actual = Await.result(future, 10.minutes)
      actual.head.getText should ===("はわわ～")
      actual.last.getText should ===("にゃしぃ")
    }
  }


}


object FlowsSpec {

  trait TestComponentRegistry extends
    FlowsComponent with
    HttpServiceComponent with
    MockitoSugar


  class TwitterStatusImpl extends twitter4j.Status {

    override def getQuotedStatus: twitter4j.Status = ???

    override def getPlace: Place = ???

    override def isRetweet: Boolean = ???

    override def isFavorited: Boolean = ???

    override def getFavoriteCount: Int = ???

    override def getCreatedAt: Date = ???

    override def getWithheldInCountries: Array[String] = ???

    override def getUser: User = ???

    override def getContributors: Array[Long] = ???

    override def getRetweetedStatus: twitter4j.Status = ???

    override def getInReplyToScreenName: String = ???

    override def getLang: String = ???

    override def isTruncated: Boolean = ???

    override def getId: Long = ???

    override def isRetweeted: Boolean = ???

    override def getCurrentUserRetweetId: Long = ???

    override def isPossiblySensitive: Boolean = ???

    override def getRetweetCount: Int = ???

    override def getGeoLocation: GeoLocation = ???

    override def getInReplyToUserId: Long = ???

    override def getSource: String = ???

    override def getText: String = ???

    override def getInReplyToStatusId: Long = ???

    override def getScopes: Scopes = ???

    override def isRetweetedByMe: Boolean = ???

    override def getQuotedStatusId: Long = ???

    override def getHashtagEntities: Array[HashtagEntity] = ???

    override def getURLEntities: Array[URLEntity] = ???

    override def getSymbolEntities: Array[SymbolEntity] = ???

    override def getMediaEntities: Array[MediaEntity] = ???

    override def getUserMentionEntities: Array[UserMentionEntity] = ???

    override def getExtendedMediaEntities: Array[ExtendedMediaEntity] = ???

    override def getAccessLevel: Int = ???

    override def getRateLimitStatus: RateLimitStatus = ???

    override def compareTo(o: twitter4j.Status): Int = ???
  }

}