package cloud.springnative.kotlinbbs.web

import cloud.springnative.kotlinbbs.domain.TopicReply
import cloud.springnative.kotlinbbs.domain.TopicReplyRepository
import cloud.springnative.kotlinbbs.domain.TopicRepository
import cloud.springnative.kotlinbbs.event.TopicReplyEvent
import cloud.springnative.kotlinbbs.util.AuthUtil
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ReplyController(val topicRepository: TopicRepository,
                      val topicReplyRepository: TopicReplyRepository,
                      val applicationContext: ApplicationContext) {

    @PostMapping("/reply")
    fun replyTopic(@RequestParam id: Long, @RequestParam content: String): ActionResult {
        val topic = topicRepository.findById(id).get()
        topic.replyNum += 1
        topicRepository.save(topic)

        val reply = TopicReply()
        reply.topicId = id
        reply.content = content
        reply.userId = AuthUtil.currentUser().id
        topicReplyRepository.save(reply)

        applicationContext.publishEvent(TopicReplyEvent(topic, reply))

        return ActionResult(0, "")
    }

    @PostMapping("/reply/{id}")
    fun getReply(@PathVariable id: Long): ReplyResult {
        val reply = topicReplyRepository.findById(id).get()
        return ReplyResult(0, reply.content)
    }

    @PostMapping("/reply/update")
    fun updateReply(@RequestParam id: Long, @RequestParam content: String): ActionResult {
        val reply = topicReplyRepository.findById(id).get()
        reply.content = content
        reply.lastModifyTime = Date()
        topicReplyRepository.save(reply)
        return ActionResult(0, "")
    }

    @PostMapping("/reply/delete")
    fun deleteReply(@RequestParam id: Long): ActionResult {
        val reply = topicReplyRepository.findById(id).get()
        if (reply.userId != AuthUtil.currentUser().id) {
            return ActionResult(-2, "???????????????????????????")
        }
        reply.status = 4
        topicReplyRepository.save(reply)

        val topic = topicRepository.findById(reply.topicId).get()
        topic.replyNum -= 1
        topicRepository.save(topic)

        return ActionResult(0, "")
    }
}

data class ReplyResult(val status: Int, val content: String)