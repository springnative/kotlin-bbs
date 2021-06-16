package cloud.springnative.kotlinbbs.util

import cloud.springnative.kotlinbbs.domain.UserInfo
import org.springframework.security.core.context.SecurityContextHolder

object AuthUtil {

    fun currentUser() = SecurityContextHolder.getContext().authentication.principal as UserInfo

    fun isAdmin(): Boolean {
        val info = SecurityContextHolder.getContext().authentication.principal
        if (info is UserInfo) {
            return info.authorities.contains("ADMIN")
        }
        return false
    }
}
