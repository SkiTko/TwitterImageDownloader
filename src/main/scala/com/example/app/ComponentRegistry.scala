package com.example.app

import com.example.service.{HttpServiceComponent, TwitterServiceComponent}

/**
  * Created by takao on 2016/11/03.
  */
object ComponentRegistry extends
  TwitterImageAppComponent with
  ConfigComponent with
  TwitterServiceComponent with
  FlowsComponent with
  HttpServiceComponent {

  override val config = new Config()
  override val twitterImageApp = new TwitterImageApp
  override val twitterService = new TwitterService
  override val flows = new Flows
  override val httpService = new HttpService
}
