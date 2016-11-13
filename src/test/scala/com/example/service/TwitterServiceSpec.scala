package com.example.service

import com.example.service.TwitterServiceSpec.TestComponentRegistry
import org.scalatest._

class TwitterServiceSpec extends
  FlatSpec with
  Matchers with
  TestComponentRegistry {


  "TwitterService" should "getUserList" in {


    val list = twitterService.getUserListMembers("sakai_takao", "illustration")


    list.nonEmpty should ===(true)
  }



  "TwitterService" should "getStatus" in {


    // /i/web/797015368585777154
    val status = twitterService.getStatus(797015368585777154L)


    status.getId should ===(797015368585777154L)
  }
}


object TwitterServiceSpec {

  trait TestComponentRegistry extends
    TwitterServiceComponent {

    override val twitterService: TwitterService = new TwitterService
  }

}