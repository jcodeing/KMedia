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
package com.jcodeing.kmedia.demo.assist;

import android.view.KeyEvent;
import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.assist.KeyXClickHelper;
import com.jcodeing.kmedia.assist.KeyXClickHelper.OnXClickCallback;
import com.jcodeing.kmedia.assist.MediaButtonReceiverHelper;
import com.jcodeing.kmedia.assist.MediaButtonReceiverHelper.OnMediaButtonEventListener;
import com.jcodeing.kmedia.assist.MediaButtonReceiverHelper.PlayerAskFor;
import com.jcodeing.kmedia.service.PlayerService;

public class AudioQueuePlayerService extends PlayerService {

  @Override
  public void onCreate() {
    super.onCreate();
    // =========@MediaButtonReceiver@=========
    MediaButtonReceiverHelper.i()//To sole
        .setDefaultMediaButtonReceiverToSole(this, true);
    // =========@Handle Media Event(Default+Custom)
    // =====@Default
    // Assist media button event default handle setPlayerAskFor(.)
    MediaButtonReceiverHelper.i().setPlayerAskFor(new PlayerAskFor() {
      @Override
      public IPlayer player() {
        return mPlayer;//Internal use player handle media button event
      }//If you don't use the default handle, don't need to setPlayerAskFor(.)
    });
    // =====@Custom
    KeyXClickHelper.i().setOnXClickCallback(new OnXClickCallback() {
      @Override
      public void onXClick(int state, int count, KeyEvent keyEvent) {
        //Custom handle media button event [by click count]
        if (count == 1) {//click count : 1
          if (mPlayer.isPlaying()) {
            mPlayer.pause();
          } else if (mPlayer.isPlayable()) {
            mPlayer.start();
          }
        } else {//other click count
          //Do something ...
          mPlayer.getMediaQueue().skipToNext();
        }
      }
    });
    // Intercept [KEYCODE_HEADSETHOOK,KEYCODE_MEDIA_PLAY_PAUSE] -dispatch-> xClick
    MediaButtonReceiverHelper.i().setOnMediaButtonEventListener(new OnMediaButtonEventListener() {
      @Override
      public boolean onMediaButtonEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
          KeyXClickHelper.i().xClick(keyEvent);
          return true;//Custom handle media button event
        }
        return false;//Other media button event don't handle(super-> default handle)
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MediaButtonReceiverHelper.i()
        .setDefaultMediaButtonReceiverToSole(this, false);
  }
}