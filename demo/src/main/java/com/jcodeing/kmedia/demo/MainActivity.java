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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.jcodeing.kmedia.Player;
import com.jcodeing.kmedia.PlayerListener;
import com.jcodeing.kmedia.assist.AudioMgrHelper;
import com.jcodeing.kmedia.assist.BrightnessHelper;
import com.jcodeing.kmedia.assist.C;
import com.jcodeing.kmedia.assist.GestureDetectorHelper.SimpleGestureListenerExtendProxy;
import com.jcodeing.kmedia.assist.PowerMgrHelper;
import com.jcodeing.kmedia.demo.assist.Assist;
import com.jcodeing.kmedia.demo.assist.MediaItem;
import com.jcodeing.kmedia.exo.ExoMediaPlayer;
import com.jcodeing.kmedia.video.ControlGroupView;
import com.jcodeing.kmedia.video.PlayerView;
import com.jcodeing.kmedia.widget.ProgressCircle;
import com.jcodeing.kmedia.window.FloatingWindowController;
import com.jcodeing.kmedia.window.FloatingWindowController.Listener;
import com.jcodeing.kmedia.window.VideoFloatingWindowController;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    OnClickListener, OnLongClickListener {

  // =========@Player@=========
  private Player player;
  private PlayerView playerView;
  private ControlGroupView ctrlGroup;
  private MainPortCtrlLayer portCtrlLayer;
  private MainLandCtrlLayer landCtrlLayer;
  private TextView portCtrlLayerSpeed;
  private TextView portCtrlLayerSwitchSubtitle;

  // =========@Other@=========
  private View other;
  private View welcome;
  private View progressMain;
  private TextView textMain;
  // =========@AB
  private View abEnable;
  private ImageView abPlayPause;
  private ProgressCircle abProgressCircle;
  private EditText abStartEt;
  private EditText abEndEt;
  private EditText abLoopEt;
  private EditText abLoopIntervalEt;
  // =========@Floating
  private VideoFloatingWindowController vFloatingWinController;
  // =========@Switch Control Layer
  private RadioButton switchDefaultCtrlLayer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initAssist();//Toolbar Menu Drawer...

    // =========@Player@=========
    player = new Player(this);
    player.init(new ExoMediaPlayer(this))
        .setEnabledWifiLock(true)
        .setUpdatePlayProgressDelayMs(100)
        .addListener(playerListener);
    // =========@View
    playerView = findViewById(R.id.k_player_view);
    playerView.setPlayer(player);
    playerView.setOrientationHelper(this, 1);//enable sensor
    playerView.getShutterView().setOnClickListener(this);
    // =========@Control
    ctrlGroup = findViewById(R.id.k_ctrl_group);
    ctrlGroup.setListener((controlLayerId, switchState) -> {
      if (switchState != 0) {//!=failure
        updateSubtitle(C.PARAM_ORIGINAL);
      }
    });
    ctrlGroup.setGestureProxy(new SimpleGestureListenerExtendProxy() {
      boolean isAdjustsShowTipsView;

      @Override
      public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP
            && isAdjustsShowTipsView) {
          // =========@Adjusts End@=========
          // =========@Hide Tips View
          ctrlGroup.showTipsView(isAdjustsShowTipsView = false, null, -1);
        }
        return false;//Control group internal enable handle
      }

      @Override
      public boolean onScrollLongitudinalLeft(MotionEvent e1, MotionEvent e2, MotionEvent e3,
          float distanceX, float distanceY) {
        isAdjustsShowTipsView = true;
        // =========@Adjusts Brightness@=========
        //Below: (last Y - current Y) / height * max -> get brightness increment
        float bIncrement = (e3.getY() - e2.getY()) / ctrlGroup.getHeight();//omit(*1.0f)
        float bDeal = BrightnessHelper.setBrightness(getWindow(), bIncrement, true);
        //Show Tips
        if (bIncrement < 0) {//bDeal->percent(bDeal / max * 100) omit(/1.0f)
          ctrlGroup.showTipsView(true, Math.round(bDeal * 100) + "%",
              R.drawable.ic_brightness_down);
        } else if (bIncrement > 0) {
          ctrlGroup
              .showTipsView(true, Math.round(bDeal * 100) + "%",
                  R.drawable.ic_brightness_up);
        }
        return true;//Control group internal disable handle
      }

      private int vMax = C.INDEX_UNSET;
      private int lastValidIncrementalPartTag;

      @Override
      public boolean onScrollLongitudinalRight(MotionEvent e1, MotionEvent e2, MotionEvent e3,
          float distanceX, float distanceY) {
        isAdjustsShowTipsView = true;
        // =========@Adjusts Volume@=========
        if (vMax == C.INDEX_UNSET) {
          vMax = AudioMgrHelper.i().getMaxVolume();
        }
        //Below: (first Y - current Y) / height * max -> get relative to max volume [incremental part].
        //Due to the [incremental part] will accumulate constantly(moving), so not directly used.
        //We can create valid [incremental part] tag, when incremental part change once after operation.
        int vIncrementalPart = (int) ((e1.getY() - e2.getY()) / ctrlGroup.getHeight() * vMax);
        if (vIncrementalPart != lastValidIncrementalPartTag) {
          lastValidIncrementalPartTag = vIncrementalPart;
          // =========@Valid
          // According last Y, adjust relative direction
          if (e3.getY() - e2.getY() < 0 && vIncrementalPart > 0
              || e3.getY() - e2.getY() > 0 && vIncrementalPart < 0) {
            vIncrementalPart = -vIncrementalPart;
          }
          // According vIncrementalPart, determine actual increment
          int actualVIncrement = vIncrementalPart > 0 ? 1 : -1;
          int vDeal = AudioMgrHelper.i().setVolume(actualVIncrement, 0/*FLAG_NOTHING*/, true);
          //Show Tips
          if (vDeal == 0) {//vDeal->percent(vDeal / vMax * 100)
            ctrlGroup.showTipsView(true, Math.round(vDeal * 100 / vMax) + "%",
                R.drawable.k_ic_volume_mute);
          } else if (actualVIncrement < 0) {
            ctrlGroup.showTipsView(true, Math.round(vDeal * 100 / vMax) + "%",
                R.drawable.k_ic_volume_down);
          } else {//actualVIncrement > 0
            ctrlGroup.showTipsView(true, Math.round(vDeal * 100 / vMax) + "%",
                R.drawable.k_ic_volume_up);
          }
        }
        return true;//Control group internal disable handle
      }
    });
    // =====@Port
    portCtrlLayer = findViewById(R.id.k_ctrl_layer_port);
    portCtrlLayer.setFindSmartViewListener(id -> {
      if (id == R.id.k_progress_bar) {
        return progressMain = findViewById(R.id.progress_main);
      }//replace control layer view
      return portCtrlLayer.findViewById(id);
    });
    portCtrlLayerSpeed = findViewById(R.id.part_speed);
    portCtrlLayerSpeed.setOnClickListener(this);
    portCtrlLayerSwitchSubtitle = findViewById(R.id.part_switch_subtitle);
    portCtrlLayerSwitchSubtitle.setOnClickListener(this);
    // =====@Land
    landCtrlLayer = findViewById(R.id.k_ctrl_layer_land);

    // =========@Other@=========
    other = findViewById(R.id.other);
    (welcome = findViewById(R.id.welcome)).setOnClickListener(this);
    // =========@AB
    View ctrlBlock = findViewById(R.id.ab_ctrl_block);
    ctrlBlock.setOnClickListener(this);
    ctrlBlock.setOnLongClickListener(this);
    abEnable = findViewById(R.id.ab_enable);
    abEnable.setEnabled(true);//enable play ab
    abPlayPause = findViewById(R.id.ab_play_pause);
    abProgressCircle = findViewById(R.id.ab_progress_circle);
    abStartEt = findViewById(R.id.ab_start_et);
    abStartEt.setOnClickListener(this);
    abEndEt = findViewById(R.id.ab_end_et);
    abEndEt.setOnClickListener(this);
    abLoopEt = findViewById(R.id.ab_loop_et);
    abLoopIntervalEt = findViewById(R.id.ab_loop_interval_et);
    // =========@Floating
    findViewById(R.id.launch_floating_player_view_bt).setOnClickListener(this);
    // =========@Switch Control Layer
    switchDefaultCtrlLayer = findViewById(R.id.switch_ctrl_layer_1);
    switchDefaultCtrlLayer.setOnClickListener(this);
    findViewById(R.id.switch_ctrl_layer_2).setOnClickListener(this);
    findViewById(R.id.switch_ctrl_layer_3).setOnClickListener(this);
    // =========@Text
    textMain = (TextView) findViewById(R.id.text_main);
  }

  @Override
  protected void onResume() {
    super.onResume();
    playerView.onResume();
    powerMgrHelper().stayAwake(true);
  }//auto handle play in onResume

  @Override
  protected void onPause() {
    super.onPause();
    playerView.onPause();
    powerMgrHelper().stayAwake(false);
  }//auto handle pause in onPause

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    //Dispose view in control layer switch
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      barLayout.setVisibility(View.GONE);
      other.setVisibility(View.GONE);
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      barLayout.setVisibility(View.VISIBLE);
      other.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
  }//internal main handle KEYCODE_BACK in landCtrlLayer switch portCtrlLayer

  @Override
  public void finish() {
    super.finish();
    playerView.finish();
    player.shutdown();
    if (vFloatingWinController != null) {
      vFloatingWinController.hide();
    }//close video floating Window
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      // =========@Player@=========
      case R.id.welcome:
      case R.id.k_shutter:
        v.setOnClickListener(null);
        setTitle(getString(R.string.label_media_demo));
        welcome.setVisibility(View.GONE);
        if (mediaItem == null) {//First Launch
          textMain.setHint(getString(R.string.welcome));
          switchDefaultCtrlLayer.setChecked(true);
          SampleLoader sampleLoader = new SampleLoader();
          sampleLoader.execute();//Loader Sample...
        } else {//Again Launch
          playerView.getShutterView().setVisibility(View.GONE);
          player.play();
        }
        break;
      case R.id.part_speed:
        float playbackSpeed = player.getPlaybackSpeed();
        float speed = playbackSpeed + 0.2f;
        if (speed >= 2.0f) {
          speed = 0.2f;
        }//0.2f <= x < 2.0f
        player.setPlaybackSpeed(speed);
        portCtrlLayerSpeed.setText(String.format(Locale.US, "%.1fx", speed));
        break;
      case R.id.part_switch_subtitle:
        currentSubtitleType = currentSubtitleType == 0 ? 1 : 0;
        updateSubtitle(C.PARAM_ORIGINAL);
        portCtrlLayerSwitchSubtitle.setText(currentSubtitleType == 0 ? "EN" : "CN");
        break;
      // =========@AB@=========
      case R.id.ab_start_et:
        abStartEt.setText(String.valueOf(player.getCurrentPosition()));
        resetAB();
        showToast("abStartGot");
        break;
      case R.id.ab_end_et:
        abEndEt.setText(String.valueOf(player.getCurrentPosition()));
        resetAB();
        showToast("abEndGot");
        break;
      case R.id.ab_ctrl_block:
        // =========@Not play ab
        if (abEnable.isEnabled()) {
          int abStart;
          try {
            abStart = Integer.valueOf(abStartEt.getText().toString());
          } catch (NumberFormatException e) {
            abStart = C.POSITION_UNSET;//-1
          }
          int abEnd;
          try {
            abEnd = Integer.valueOf(abEndEt.getText().toString());
          } catch (NumberFormatException e) {
            abEnd = C.POSITION_UNSET;//-1
          }
          int abLoop;
          try {
            abLoop = Integer.valueOf(abLoopEt.getText().toString());
          } catch (NumberFormatException e) {
            abLoop = C.PARAM_UNSET;
          }
          int abLoopInterval;
          try {
            abLoopInterval = Integer.valueOf(abLoopIntervalEt.getText().toString());
          } catch (NumberFormatException e) {
            abLoopInterval = C.PARAM_UNSET;
          }
          if (abStart >= 0 && abStart < abEnd) {
            player.setAB(abStart, abEnd, abLoop, abLoopInterval).play();
            abPlayPause.setImageResource(R.drawable.ic_pause_primary);
            abEnable.setEnabled(false);//disable
          } else {
            showToast("illegality ab start|end");
          }
        }//can go play ab
        // =========@Played ab
        else if (player.isPlaying()) {
          player.pause();//pause ab play
          abPlayPause.setImageResource(R.drawable.ic_play_primary);
        } else if (player.isPlayable()) {
          player.start();//start ab play
          abPlayPause.setImageResource(R.drawable.ic_pause_primary);
        }
        break;
      // =========@Floating@=========
      case R.id.launch_floating_player_view_bt:
        if (vFloatingWinController == null) {
          vFloatingWinController = new VideoFloatingWindowController(getApplicationContext());
          vFloatingWinController//Custom VFloating View
              .setFloatingView(new MainVFloatingView(this));
          vFloatingWinController.setListener(new Listener() {
            @Override
            public void onHide(FloatingWindowController controller) {
              playerView.setPlayer(player);
            }
          });
        }
        playerView.setPlayer(null);
        vFloatingWinController.show(player);
        break;
      // =========@Switch Control Layer@=========
      case R.id.switch_ctrl_layer_1:
        //use progress main
        progressMain.setVisibility(View.VISIBLE);
        progressMain.setEnabled(true);
        //use public R.id.k_ctrl_layer_port
        ctrlGroup.switchControlLayer(R.id.k_ctrl_layer_port);
        break;
      case R.id.switch_ctrl_layer_2:
        progressMain.setEnabled(false);
        ctrlGroup.switchControlLayer(R.id.ctrl_layer_2);
        break;
      case R.id.switch_ctrl_layer_3:
        progressMain.setEnabled(false);
        ctrlGroup.switchControlLayer(R.id.ctrl_layer_3);
        break;
    }
  }

  @Override
  public boolean onLongClick(View v) {
    if (v.getId() == R.id.ab_ctrl_block) {
      resetAB();
      showToast("ab close");
      return true;
    }
    return false;
  }

  // ============================@Player@============================
  // ============================@AB
  private void resetAB() {
    player.clearAB();
    abEnable.setEnabled(true);//enable
    abPlayPause.setImageResource(R.drawable.ic_play_primary);
    abProgressCircle.setProgress(0);
  }

  // ============================@Other
  private PlayerListener playerListener = new PlayerListener() {
    @Override
    public void onABProgress(long position, long duration, int abState) {
      //update ab play progress
      if (position >= 0 && duration > position) {
        abProgressCircle.setProgress((int) (position * 100 / duration));
      }
      //update play ab state
      if (abState == C.STATE_PROGRESS_AB_END) {
        abPlayPause.setImageResource(R.drawable.ic_play_primary);
        abProgressCircle.setProgress(0);
      } else if (abState == C.STATE_PROGRESS_AB_FINISH) {
        resetAB();
        showToast("ab close");
      }
    }

    @Override
    public void onPositionUnitProgress(long position, long duration, int posUnitIndex,
        int posUnitState) {
      if (posUnitState == C.STATE_PROGRESS_POS_UNIT_START) {
        updateSubtitle(posUnitIndex);
      }
    }

    @Override
    public int onCompletion() {
      View shutterView = playerView.getShutterView();
      if (shutterView != null) {
        //Manual control shutter view
        shutterView.setVisibility(View.VISIBLE);
        shutterView.setOnClickListener(MainActivity.this);
        //Again welcome ...
        welcome.setOnClickListener(MainActivity.this);
        welcome.setVisibility(View.VISIBLE);
        textMain.setText(getString(R.string.copyright));
        setTitle(getString(R.string.app_name));
      }
      return super.onCompletion();
    }
  };

  // ============================@Subtitle
  /**
   * 0-EN 1-CN
   */
  int currentSubtitleType = 0;

  private void updateSubtitle(int posUnitIndex) {
    if (mediaItem == null) {
      return;
    }
    if (posUnitIndex == C.PARAM_ORIGINAL) {
      posUnitIndex = player.getCurrentPositionUnitIndex();
    }
    if (currentSubtitleType == 0) {
      if (ctrlGroup.getCurrentControlLayerId() == R.id.k_ctrl_layer_land) {
        landCtrlLayer.setSubtitleText(mediaItem.getSubtitleEn(posUnitIndex));
      } else {
        textMain.setText(mediaItem.getSubtitleEn(posUnitIndex));
      }
    } else {
      if (ctrlGroup.getCurrentControlLayerId() == R.id.k_ctrl_layer_land) {
        landCtrlLayer.setSubtitleText(mediaItem.getSubtitleCn(posUnitIndex));
      } else {
        textMain.setText(mediaItem.getSubtitleCn(posUnitIndex));
      }
    }
  }

  // ============================@Sample@============================
  MediaItem mediaItem;

  private final class SampleLoader extends AsyncTask<Void, Void, MediaItem> {

    @Override
    protected MediaItem doInBackground(Void... params) {
      try {
        String s = Assist.getStringFromInputStream(getAssets().open("sample.json"));
        return Assist.gson.fromJson(s, MediaItem.class);
      } catch (Exception e) {
        return null;
      }
    }

    @Override
    protected void onPostExecute(MediaItem result) {
      mediaItem = result;
      player.setPositionUnitList(mediaItem);
      player.play(mediaItem);
    }
  }

  // ============================@Assist@============================
  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }//Compat -> ctrlGroup.showTipsView(..,R.drawable.ic_brightness_down) ...

  private AppBarLayout barLayout;
  private DrawerLayout drawer;

  private void initAssist() {
    barLayout = findViewById(R.id.bar_layout);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  // ============================@Drawer
  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.nav_simple_video) {
      startActivity(new Intent(this, VideoSimpleActivity.class));
    } else if (id == R.id.nav_queue_video) {
      startActivity(new Intent(this, VideoQueueActivity.class));
    } else if (id == R.id.nav_multiple_video) {
      startActivity(new Intent(this, VideoMultipleActivity.class));
    } else if (id == R.id.nav_queue_audio) {
      startActivity(new Intent(this, AudioQueueActivity.class));
    }

    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  // ============================@Menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_about) {
      startActivity(new Intent(this, AboutActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  // ============================@Toast
  private Toast toast;

  private void showToast(String str) {
    if (toast != null) {
      toast.cancel();
      toast = null;
    }
    toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
    toast.show();
  }

  // ============================@PowerMgrHelper
  private PowerMgrHelper powerMgrHelper;

  private PowerMgrHelper powerMgrHelper() {
    if (powerMgrHelper == null) {
      powerMgrHelper = new PowerMgrHelper((PowerManager) getSystemService(Context.POWER_SERVICE));
      //noinspection deprecation
      powerMgrHelper//To prevent the screen dimmed
          .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "KMediaDemo");
    }
    return powerMgrHelper;
  }
}