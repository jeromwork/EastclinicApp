package com.eastclinic.core.push

/**
 * Interface for handling notifications.
 */
interface NotificationHandler {
    fun handleNotification(notification: Notification)
    fun handleNotificationClick(notificationId: String)
}

/**
 * Notification data model.
 */
data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap()
)


