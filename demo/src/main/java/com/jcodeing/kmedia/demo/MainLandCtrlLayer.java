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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.assist.AnimationHelper;
import com.jcodeing.kmedia.video.ControlLayerView;
import com.jcodeing.kmedia.widget.DragTextView;

public class MainLandCtrlLayer extends ControlLayerView implements OnClickListener {

  public MainLandCtrlLayer(Context context) {
    super(context);
  }

  public MainLandCtrlLayer(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MainLandCtrlLayer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected boolean initAttrs(TypedArray a) {
    super.initAttrs(a);
    usePartTop = usePartLeft = usePartRight = true;
    return a != null;//top left right set default use
  }

  @Override
  protected int getDefaultLayoutId() {
    return R.layout.ctrl_layer_land_main;
  }//use custom layout

  private ImageView leftLock;
  private DragTextView subtitle;

  @Override
  protected void initView() {
    super.initView();
    leftLock = findViewById(R.id.part_lock_left);
    leftLock.setOnClickListener(this);
    subtitle = findViewById(R.id.part_subtitle);
    //override control group, public id click listener
    findViewById(R.id.k_rew).setOnClickListener(this);
    findViewById(R.id.k_ffwd).setOnClickListener(this);
  }

  @Override
  public void setVisibilityByInteractionArea(int visibility, boolean animation) {
    if (visibility == VISIBLE) {
      // =========@Show@=========
      //Lock
      if (leftLock.getVisibility() == View.GONE) {
        leftLock.setVisibility(VISIBLE);
        if (animation) {
          AnimationHelper.showLeft(leftLock, null);
        }
      }
      if (isLocked()) {
        return;
      }
    } else if (visibility == GONE || visibility == INVISIBLE) {
      // =========@Hide@=========
      //Lock
      if (leftLock.getVisibility() == View.VISIBLE) {
        if (animation) {
          AnimationHelper
              .hideLeft(leftLock, new AnimationHelper.AnimationActionListener() {
                public void onAnimationEnd() {
                  leftLock.setVisibility(GONE);
                }
              });
        } else {
          leftLock.setVisibility(GONE);
        }
      }
      if (isLocked()) {
        return;
      }
    }
    super.setVisibilityByInteractionArea(visibility, animation);
  }

  @Override
  public boolean isVisibleByInteractionArea() {
    if (isLocked()) {
      return leftLock.getVisibility() == VISIBLE;
    }
    return super.isVisibleByInteractionArea();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.k_ffwd:
      case R.id.k_rew:
        IPlayer player = getPlayer();
        if (player != null) {
          player.seekToPositionUnitIndex(
              v.getId() == R.id.k_ffwd ? player.getCurrentPositionUnitIndex() + 1
                  : player.getCurrentPositionUnitIndex() - 1);
        }//custom handle public id (k_ffwd,k_rew)
        break;
      case R.id.part_lock_left:
        if (getControlGroup() != null) {
          if (controlGroup.setLocked(!controlGroup.isLocked())) {
            leftLock.setImageResource(R.drawable.ic_player_locked);
            subtitle.setLocked(true);
          } else {
            leftLock.setImageResource(R.drawable.ic_player_unlock);
            subtitle.setLocked(false);
          }
        }
        break;
    }
  }

  public void setSubtitleText(CharSequence text) {
    if (subtitle.getVisibility() != VISIBLE) {
      subtitle.setVisibility(VISIBLE);
    }
    subtitle.setText(text);
  }

  public boolean isLocked() {
    return getControlGroup() != null && controlGroup.isLocked();
  }

  public IPlayer getPlayer() {
    if (getControlGroup() != null) {
      return controlGroup.getPlayer();
    }
    return null;
  }
}
