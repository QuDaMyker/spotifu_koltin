package com.builtlab.spotifykotlin.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.exoplayer.ExoPlayer
import com.builtlab.spotifykotlin.R
import com.builtlab.spotifykotlin.other.Constants.NOTIFICATION_CHANNEL_ID
import com.builtlab.spotifykotlin.other.Constants.NOTIFICATION_ID
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/*
* New API
* Remove PlayerNotificationManager.NotificationListener -> Recommend: MediaLibraryService
* Document: https://developer.android.com/media/media3/exoplayer/migration-guide#PlayerNotificationManager
*/

class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallBack: () -> Unit
) {
    private val notificationManager: PlayerNotificationManager;

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,
        ).setChannelNameResourceId(R.string.notification_chanel_name)
            .setChannelDescriptionResourceId(R.string.notification_chanel_description)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .build().apply {
                setSmallIcon(R.drawable.ic_launcher_background)
                setMediaSessionToken(sessionToken)
            }
    }

    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(p0: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(p0: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(p0: Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            p0: Player,
            p1: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        p1.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                })
            return null
        }
    }
}

