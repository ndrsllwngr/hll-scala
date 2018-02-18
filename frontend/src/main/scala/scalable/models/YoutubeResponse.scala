package scalable.models


case class YoutubeResponse(
                          kind: String,
                          etag: String,
                          nextPageToken: String,
                          regionCode: String,
                          pageInfo: PageInfo,
                          items: List[VideoResponse]
                        )

case class PageInfo(
                    totalResults: Int,
                    resultsPerPage: Int,
                   )

case class VideoResponse(
                     kind: String,
                     etag: String,
                     id: VideoId,
                     snippet: VideoSnippet
                   )


case class VideoId (
                     kind: String,
                     videoId: String,

                   )

case class VideoSnippet (
                          publishedAt: String,
                          channelId: String,
                          title: String,
                          description: String,
                          thumbnails: Map[String, VideoThumbnails],
                          channelTitle: String,
                          liveBroadcastContent: String
                   )

case class VideoThumbnails (
                          width: Int,
                          height: Int,
                          url: String
                        )
