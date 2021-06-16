package cloud.springnative.kotlinbbs.service

import cloud.springnative.kotlinbbs.domain.Message
import cloud.springnative.kotlinbbs.domain.MessageRepository
import cloud.springnative.kotlinbbs.domain.MessageSource
import cloud.springnative.kotlinbbs.domain.UserInfoRepository
import cloud.springnative.kotlinbbs.event.TopicReplyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MessageService(val messageRepository: MessageRepository,
                     val userInfoRepository: UserInfoRepository) {

    @EventListener
    @Async
    fun handleReply(event: TopicReplyEvent) {
        val topic = event.topic
        val reply = event.reply
        if (reply.userId == topic.userId) {
            return
        }
        val message = Message()
        message.source = MessageSource.USER
        message.readed = false

        message.topicId = topic.id
        message.userId = topic.userId

        message.fromUserId = reply.userId

        messageRepository.save(message)

        val user = userInfoRepository.findById(reply.userId).get()
        message.content = "<a href='/user/home?username=${user.name}' target='_blank'><cite>${user.name}</cite></a>" +
                "在<a href='/topic/${topic.id}?messageId=${message.id}' target='_blank'><cite>${topic.title}</cite></a>中回答：" +
                "<a target='_blank' href='/topic/${topic.id}?messageId=${message.id}#item-${reply.id}'><cite>${reply.content}</cite></a>"

        messageRepository.save(message)
    }

    fun read(id: Long) {
        messageRepository.findById(id).ifPresent {
            if (!it.readed) {
                it.readed = true
                messageRepository.save(it)
            }
        }
    }
}