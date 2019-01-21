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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import com.jcodeing.kmedia.AndroidMediaPlayer;
import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.Player;
import com.jcodeing.kmedia.exo.ExoMediaPlayer;
import com.jcodeing.kmedia.video.PlayerView;

public class VideoSimpleActivity extends AppCompatActivity {

  private IPlayer player;
  private PlayerView playerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple);

    player = new Player(this).init(new ExoMediaPlayer(this));

    playerView = ((PlayerView) findViewById(R.id.k_player_view)).setPlayer(player);

    player.play(Uri.parse("asset:///" + "sample.mp4"));
  }

  @Override
  protected void onResume() {
    super.onResume();
    playerView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    playerView.onPause();
  }

  @Override
  public void finish() {
    super.finish();
    playerView.finish();
    player.shutdown();
  }

  // ============================@Menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.simple, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    item.setChecked(true);
    if (item.getItemId() == R.id.android) {
      // =========@AndroidMediaPlayer@=========
      player.init(new AndroidMediaPlayer());
      playerView.setPlayer(null);
      playerView.setSurfaceView(new SurfaceView(this));
      playerView.setPlayer(player);
      player.play(Uri.parse(
          "http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4"));
    } else {
      // =========@ExoMediaPlayer@=========
      player.init(new ExoMediaPlayer(this));
      playerView.setPlayer(null);
      playerView.setSurfaceView(new SurfaceView(this));
      playerView.setPlayer(player);
      player.play(Uri.parse("asset:///" + "sample.mp4"));
    }
    return true;
  }
}