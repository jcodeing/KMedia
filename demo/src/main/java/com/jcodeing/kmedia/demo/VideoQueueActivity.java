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

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.reflect.TypeToken;
import com.jcodeing.kmedia.Player;
import com.jcodeing.kmedia.definition.IMediaItem;
import com.jcodeing.kmedia.definition.IMediaQueue;
import com.jcodeing.kmedia.definition.IMediaQueue.Listener;
import com.jcodeing.kmedia.demo.assist.Assist;
import com.jcodeing.kmedia.demo.assist.MediaItem;
import com.jcodeing.kmedia.demo.assist.VideoQueueAdapter;
import com.jcodeing.kmedia.exo.ExoMediaPlayer;
import com.jcodeing.kmedia.utils.Metrics;
import com.jcodeing.kmedia.video.ControlLayerView;
import com.jcodeing.kmedia.video.PlayerView;
import java.util.ArrayList;
import java.util.List;

public class VideoQueueActivity extends AppCompatActivity implements OnClickListener, Listener {

  private RecyclerView recyclerView;
  // =========@Player@=========
  private Player player;
  private PlayerView playerView;
  private IMediaQueue mediaQueue;
  private VideoQueueLandCtrlLayer landCtrlLayer;
  private TextView landTitle;
  private TextView portTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_queue_video);

    // =========@Player@=========
    player = new Player(this);
    player.init(new ExoMediaPlayer(this));
    mediaQueue = player.getMediaQueue();
    // =========@View
    playerView = findViewById(R.id.k_player_view);
    playerView.setPlayer(player);
    playerView.setOrientationHelper(this, 1);
    // =========@Control
    // =====@Port
    ControlLayerView portCtrlLayer = findViewById(R.id.k_ctrl_layer_port);
    portTitle = (TextView) portCtrlLayer.initPart(R.id.part_top_tv);
    //Custom top left iv
    portCtrlLayer.initPartIb(R.id.part_top_left_ib,
        R.drawable.ic_go_back, "goBack").setOnClickListener(this);
    //Custom bottom right iv
    portCtrlLayer.initPartIb(R.id.part_bottom_right_ib,
        R.drawable.ic_go_full_screen, "goFullScreen").setOnClickListener(this);
    //Custom remove position
    portCtrlLayer.removePart(R.id.k_position_tv);
    //Custom remove duration
    portCtrlLayer.removePart(R.id.k_duration_tv);
    //Custom change top background
    portCtrlLayer.findPart(R.id.k_ctrl_layer_part_top)
        .setBackgroundResource(android.R.color.transparent);
    //Custom change bottom background
    portCtrlLayer.findPart(R.id.k_ctrl_layer_part_bottom)
        .setBackgroundResource(android.R.color.transparent);
    //Custom change middle background
    portCtrlLayer.findPart(R.id.k_ctrl_layer_part_middle)
        .setBackgroundResource(android.R.color.transparent);
    //Custom change middle play/pause/buffer size
    ((ImageButton) portCtrlLayer.findPart(R.id.k_play,
        Metrics.dp2px(this, 31f), Metrics.dp2px(this, 31f))).setScaleType(ScaleType.FIT_CENTER);
    ((ImageButton) portCtrlLayer.findPart(R.id.k_pause,
        Metrics.dp2px(this, 31f), Metrics.dp2px(this, 31f))).setScaleType(ScaleType.FIT_CENTER);
    portCtrlLayer.findPart(R.id.part_buffer,
        Metrics.dp2px(this, 51f), Metrics.dp2px(this, 51f));
    //Custom change tips background
    portCtrlLayer.findPart(R.id.part_tips_tv)
        .setBackgroundResource(R.color.bg_video_queue_common);
    portCtrlLayer.updateSmartView();
    // =====@Land
    landCtrlLayer = findViewById(R.id.k_ctrl_layer_land);
    //Custom change bottom background
    landCtrlLayer.findPart(R.id.k_ctrl_layer_part_bottom)
        .setBackgroundResource(R.color.bg_video_queue_common);
    //Custom change middle background
    landCtrlLayer.findPart(R.id.k_ctrl_layer_part_middle)
        .setBackgroundResource(R.drawable.bg_video_queue_activity_oval);
    landTitle = (TextView) landCtrlLayer.findPart(R.id.part_top_tv);

    // =========@RecyclerView@=========
    recyclerView = findViewById(R.id.v_recycler);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new VideoQueueAdapter(this));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

    // =========@Sample@=========
    SampleLoader sampleLoader = new SampleLoader();
    sampleLoader.execute();
  }

  @Override
  protected void onResume() {
    super.onResume();
    playerView.onResume();
    mediaQueue.addListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    playerView.onPause();
    mediaQueue.removeListener(this);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      recyclerView.setVisibility(View.GONE);
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      recyclerView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
  }

  @Override
  public void finish() {
    super.finish();
    playerView.finish();
    player.shutdown();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.part_top_left_ib:
        finish();//goBack
        break;
      case R.id.part_bottom_right_ib://fullScreen
        playerView.getOrientationHelper().goLandscape();
        break;
    }
  }

  // ============================@MediaQueue@============================
  @Override
  public void onQueueUpdated(List<? extends IMediaItem> newQueue) {
    //Do nothing
  }

  @Override
  public void onItemRemoved(int index) {
    //Do nothing
  }

  @Override
  public void onCurrentQueueIndexUpdated(int index) {
    IMediaItem mediaItem = mediaQueue.getMediaItem(index);
    if (mediaItem != null) {
      // =========@Update Info@=========
      portTitle.setText(mediaItem.getTitle());
      landTitle.setText(mediaItem.getTitle());
    }
  }

  @Override
  public boolean onSkipQueueIndex(int index) {
    return false;//not handle(mediaQueue internal default handle)
  }

  // ============================@Sample@============================
  private final class SampleLoader extends AsyncTask<Void, Void, ArrayList<MediaItem>> {

    @Override
    protected ArrayList<MediaItem> doInBackground(Void... params) {
      try {
        String s = Assist.getStringFromInputStream(getAssets().open("samples.video.json"));
        return Assist.gson.fromJson(s,
            new TypeToken<ArrayList<MediaItem>>() {
            }.getType());
      } catch (Exception e) {
        return null;
      }
    }

    @Override
    protected void onPostExecute(ArrayList<MediaItem> result) {
      mediaQueue.update(result);
      ((VideoQueueAdapter) recyclerView.getAdapter())
          .setMediaQueue(mediaQueue);
      landCtrlLayer.setMediaQueue(mediaQueue);
      mediaQueue.skipToIndex(0);
    }
  }
}