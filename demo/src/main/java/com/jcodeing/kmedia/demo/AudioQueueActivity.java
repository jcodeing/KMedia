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

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.reflect.TypeToken;
import com.jcodeing.kmedia.APlayerBinding.BindPlayer;
import com.jcodeing.kmedia.APlayerBinding.BindingListener;
import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.IPlayerBase;
import com.jcodeing.kmedia.Player;
import com.jcodeing.kmedia.PlayerBinding;
import com.jcodeing.kmedia.PlayerListener;
import com.jcodeing.kmedia.definition.IMediaItem;
import com.jcodeing.kmedia.definition.IMediaQueue;
import com.jcodeing.kmedia.definition.IMediaQueue.Listener;
import com.jcodeing.kmedia.demo.assist.Assist;
import com.jcodeing.kmedia.demo.assist.AudioQueueAdapter;
import com.jcodeing.kmedia.demo.assist.AudioQueuePlayerService;
import com.jcodeing.kmedia.demo.assist.BitmapCache;
import com.jcodeing.kmedia.demo.assist.MediaItem;
import com.jcodeing.kmedia.exo.ExoMediaPlayer;
import com.jcodeing.kmedia.service.PlayerService;
import com.jcodeing.kmedia.utils.TimeProgress;
import com.jcodeing.kmedia.widget.ProgressCircle;
import java.util.ArrayList;
import java.util.List;

public class AudioQueueActivity extends AppCompatActivity implements Listener, OnClickListener {

  private RecyclerView recyclerView;
  // =========@Control Area@=========
  private ImageView iconIv;
  private TextView titleTv;
  private TextView descriptionTv;
  private ProgressCircle progressCircle;
  private ImageView playPauseIv;
  private View bufferView;

  // =========@Player@=========
  private IPlayer player;
  private IMediaQueue mediaQueue;
  private IMediaItem mediaItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_queue_audio);

    recyclerView = (RecyclerView) findViewById(R.id.a_recycler);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new AudioQueueAdapter(this));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    // =========@Control Area@=========
    iconIv = (ImageView) findViewById(R.id.icon);
    titleTv = (TextView) findViewById(R.id.title);
    descriptionTv = (TextView) findViewById(R.id.description);
    playPauseIv = (ImageView) findViewById(R.id.audio_play_pause);
    progressCircle = (ProgressCircle) findViewById(R.id.audio_progress_circle);
    progressCircle.setOnClickListener(this);
    bufferView = findViewById(R.id.buffer);

    // =========@Player@=========
    player = new PlayerBinding(this, AudioQueuePlayerService.class, new BindPlayer() {
      @Override
      public IPlayer onBindPlayer() {
        return new Player(getApplicationContext())
            .init(new ExoMediaPlayer(getApplicationContext()));
      }//Player service bind player, call back.(if is bound, not call back)
    }, new BindingListener() {
      @Override
      public void onFirstBinding(PlayerService service) {
        //do something player service init operation.
        service.setNotifier(new AudioQueueNotifier());
      }//First binding call back.(if first binding finish, not call back)

      @Override
      public void onBindingFinish() {
        // =========@Player@=========
        player.addListener(playerListener);
        // =========@MediaQueue
        mediaQueue = player.getMediaQueue();
        mediaQueue.setAutoSkipMode(IMediaQueue.AUTO_SKIP_MODE_RANDOM);
        mediaQueue.addListener(AudioQueueActivity.this);
        if (mediaQueue.isEmpty()) {
          //First loader
          SampleLoader sampleLoader = new SampleLoader();
          sampleLoader.execute();
        } else {
          //Use existing data set/update
          ((AudioQueueAdapter) recyclerView.getAdapter()).setMediaQueue(mediaQueue);
          onCurrentQueueIndexUpdated(mediaQueue.getCurrentIndex());
          playerListener.onStateChanged(player.getPlaybackState());
        }
      }//Binding finish. Can play.
    }).bind();
  }

  @Override
  protected void onResume() {
    super.onResume();
    player.addListener(playerListener);
    playerListener.onStateChanged(player.getPlaybackState());
  }

  @Override
  protected void onPause() {
    super.onPause();
    player.removeListener(playerListener);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.audio_progress_circle) {
      if (player.isPlaying()) {
        player.pause();
      } else if (player.isPlayable()) {
        player.start();
      } else {
        player.play(mediaItem);
      }
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
    mediaItem = mediaQueue.getMediaItem(index);
    if (mediaItem != null) {
      // =========@Update Info@=========
      titleTv.setText(mediaItem.getTitle());
      descriptionTv.setText(mediaItem.getDescription());
      //Icon
      String iconStr = mediaItem.getIconUri().toString();
      BitmapCache cache = BitmapCache.getInstance();
      if (cache.getBigImage(iconStr) != null) {
        iconIv.setImageBitmap(cache.getBigImage(iconStr));
      } else {
        iconIv.setImageResource(R.drawable.ic_rectangle_primary);
        cache.fetch(iconStr, new BitmapCache.FetchListener() {
          @Override
          public void onFetched(String url, Bitmap bitmap, Bitmap icon) {
            if (mediaItem != null && mediaItem.getIconUri().toString().equals(url)) {
              iconIv.setImageBitmap(bitmap);
            }
          }
        });
      }
      recyclerView.scrollToPosition(index);
    }
  }

  @Override
  public boolean onSkipQueueIndex(int index) {
    return false;//not handle(mediaQueue internal default handle)
  }

  // ============================@PlayerListener@============================
  private PlayerListener playerListener = new PlayerListener() {
    @Override
    public boolean onPlayProgress(long position, long duration) {
      // =========@Update Progress View@=========
      progressCircle.setProgress(
          TimeProgress.progressValue(position, duration, progressCircle.getMax()));
      return true;
    }

    @Override
    public void onStateChanged(int playbackState) {
      // =========@Update State/Control View@=========
      if (playbackState == IPlayerBase.STATE_READY) {
        if (player.isPlaying()) {
          playPauseIv.setImageResource(R.drawable.ic_pause_primary);
        } else if (player.isPlayable()) {
          playPauseIv.setImageResource(R.drawable.ic_play_primary);
        }
      }
      bufferView.setVisibility(playbackState == IPlayer.STATE_BUFFERING ? View.VISIBLE : View.GONE);
    }
  };

  // ============================@Sample@============================
  private final class SampleLoader extends AsyncTask<Void, Void, ArrayList<MediaItem>> {

    @Override
    protected ArrayList<MediaItem> doInBackground(Void... params) {
      try {
        String s = Assist.getStringFromInputStream(getAssets().open("samples.audio.json"));
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
      ((AudioQueueAdapter) recyclerView.getAdapter()).setMediaQueue(mediaQueue);
      //play default index audio
      mediaQueue.skipToIndex(0);
    }
  }

  // ============================@Menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.queue, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    item.setChecked(true);
    if (item.getItemId() == R.id.asm_list_loop) {
      mediaQueue.setAutoSkipMode(IMediaQueue.AUTO_SKIP_MODE_LIST_LOOP);
    } else if (item.getItemId() == R.id.asm_random) {
      mediaQueue.setAutoSkipMode(IMediaQueue.AUTO_SKIP_MODE_RANDOM);
    } else if (item.getItemId() == R.id.asm_single_loop) {
      mediaQueue.setAutoSkipMode(IMediaQueue.AUTO_SKIP_MODE_SINGLE_LOOP);
    } else if (item.getItemId() == R.id.asm_single_once) {
      mediaQueue.setAutoSkipMode(IMediaQueue.AUTO_SKIP_MODE_SINGLE_ONCE);
    }
    return true;
  }
}
