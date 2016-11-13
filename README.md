# TwitterImageDownloader

## How to run

```
export JAVA_OPTS="\
-Dtwitter4j.oauth.consumerKey=<CONSUMER KEY> \
-Dtwitter4j.oauth.consumerSecret=<CONSUMER SECRET> \
-Dtwitter4j.oauth.accessToken=<ACCESS TOKEN> \
-Dtwitter4j.oauth.accessTokenSecret=<ACCESS TOKEN SECRET> \
-Dapp.media.directory=<DOWNLOAD DIRECTORY> \
-Dapp.twitter.stream.list.owner=<OWNER OF TWITTER LIST> \
-Dapp.twitter.stream.list.name=<TWITTER LIST NAME>"
"

sbt run
```
