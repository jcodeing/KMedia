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

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.Player;
import com.jcodeing.kmedia.exo.ExoMediaPlayer;
import com.jcodeing.kmedia.window.FloatingWindowController;
import com.jcodeing.kmedia.window.FloatingWindowController.Listener;
import com.jcodeing.kmedia.window.VideoFloatingWindowController;

public class VideoMultipleActivity extends AppCompatActivity implements OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_multiple_video);
    findViewById(R.id.video_1).setOnClickListener(this);
    findViewById(R.id.video_2).setOnClickListener(this);
    findViewById(R.id.video_3).setOnClickListener(this);
    findViewById(R.id.video_4).setOnClickListener(this);
    findViewById(R.id.video_5).setOnClickListener(this);
    findViewById(R.id.video_6).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    launchFloatingVideo(v);
  }

  private int[] locationOnScreen = new int[2];

  private void launchFloatingVideo(View v) {
    if (v != null) {
      // =========@FloatingWindowController@=========
      VideoFloatingWindowController controller =
          new VideoFloatingWindowController(getApplicationContext());
      controller.setListener(new Listener() {
        @Override
        public void onHide(FloatingWindowController controller) {
          VideoFloatingWindowController c = (VideoFloatingWindowController) controller;
          c.getPlayer().shutdown();//on hide, shutdown player. release resource.
        }
      });
      // =========@Location XY
      v.getLocationOnScreen(locationOnScreen);
      controller.setGravity(Gravity.TOP | Gravity.START)
          .setFloatingWindowXY(locationOnScreen[0], locationOnScreen[1]);
      // =========@Size
      int width = v.getWidth();
      int height = width * 3 / 4;
      if (height > v.getHeight()) {
        height = v.getHeight();
      }
      controller.setFloatingWindowSize(width, height);
      controller.setFloatingView(
          new VideoMultipleFloatingView(this, width / 2, height / 2));
      // =========@Player@=========
      IPlayer player = new Player(getApplicationContext())
          .init(new ExoMediaPlayer(getApplicationContext()));
      //multiple videos disable audio focus manage
      player.setEnabledAudioFocusManage(false);
      // =========@Play
      controller.show(player);
      player.play(Uri.parse("asset:///" + "sample.mp4"));
    }
  }
}
