package me.rerere.rikkahub.service

/**
 * 聊天服务参数错误。
 *
 * 这些异常属于原生聊天核心逻辑，不依赖外部 Web Server。
 */
class BadRequestException(message: String) : RuntimeException(message)

/**
 * 聊天服务资源不存在。
 *
 * 这些异常属于原生聊天核心逻辑，不依赖外部 Web Server。
 */
class NotFoundException(message: String) : RuntimeException(message)
