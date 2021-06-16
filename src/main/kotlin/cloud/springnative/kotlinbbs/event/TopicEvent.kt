package cloud.springnative.kotlinbbs.event

import cloud.springnative.kotlinbbs.domain.Topic
import cloud.springnative.kotlinbbs.domain.TopicReply

data class TopicCreateEvent(val id: Long, val userId: Long)

data class TopicUpdateEvent(val id: Long, val userId: Long)

data class TopicReplyEvent(val topic: Topic, val reply: TopicReply)