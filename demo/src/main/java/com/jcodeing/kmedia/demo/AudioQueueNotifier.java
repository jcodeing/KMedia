/*
 * Copyright (c) 2017 K Sun <jcodeing@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jcodeing.kmedia.demo;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.core.app.NotificationCompat.Builder;
import com.jcodeing.kmedia.definition.IMediaItem;
import com.jcodeing.kmedia.demo.assist.BitmapCache;
import com.jcodeing.kmedia.worker.ANotifier;

public class AudioQueueNotifier extends ANotifier {

  private Builder builder;

  @Override
  public void updateNotification(boolean mediaItemChanged) {
    //Update state(play/pause)
    addSimpleMediaAction(builder);
    if (mediaItemChanged) {
      //Update info(title/icon)
      setSimpleMediaInfo(builder, mediaItem);
      updateNotificationIcon(mediaItem.getIconUri().toString());
    }
    notification = builder.build();
  }

  @Override
  protected Notification createNotification(IMediaItem mediaItem) {
    builder = createSimpleMediaNotificationBuilder(mediaItem);
    //Update Icon
    updateNotificationIcon(mediaItem.getIconUri().toString());
    //Content Intent
    Intent intent = new Intent(playerService, AudioQueueActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    builder.setContentIntent(PendingIntent
        .getActivity(playerService, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT));
    return builder.build();
  }

  // ============================@Assist@============================
  public void updateNotificationIcon(String iconStr) {
    BitmapCache cache = BitmapCache.getInstance();
    if (cache.getBigImage(iconStr) != null) {
      builder.setLargeIcon(cache.getBigImage(iconStr));
      helper.notify(NOTIFICATION_ID, notification = builder.build());
    } else {
      cache.fetch(iconStr, new BitmapCache.FetchListener() {
        @Override
        public void onFetched(String url, Bitmap bitmap, Bitmap icon) {
          if (mediaItem != null && mediaItem.getIconUri().toString().equals(url)) {
            builder.setLargeIcon(bitmap);
            helper.notify(NOTIFICATION_ID, notification = builder.build());
          }
        }
      });
    }
  }
}
