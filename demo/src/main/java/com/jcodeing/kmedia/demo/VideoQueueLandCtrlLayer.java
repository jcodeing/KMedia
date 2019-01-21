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
import android.widget.LinearLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jcodeing.kmedia.assist.AnimationHelper;
import com.jcodeing.kmedia.definition.IMediaQueue;
import com.jcodeing.kmedia.demo.assist.VideoQueueAdapter;
import com.jcodeing.kmedia.video.ControlLayerView;

public class VideoQueueLandCtrlLayer extends ControlLayerView implements OnClickListener {

  public VideoQueueLandCtrlLayer(Context context) {
    super(context);
  }

  public VideoQueueLandCtrlLayer(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoQueueLandCtrlLayer(Context context, AttributeSet attrs, int defStyleAttr) {
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
    return R.layout.ctrl_layer_land_queue;
  }//use custom layout


  private RecyclerView recyclerView;
  private View videoQueueMenu;

  @Override
  protected void initView() {
    super.initView();
    findViewById(R.id.v_queue_menu_open).setOnClickListener(this);
    findViewById(R.id.v_queue_menu_close).setOnClickListener(this);
    videoQueueMenu = findViewById(R.id.v_queue_menu);
    recyclerView = (RecyclerView) findViewById(R.id.part_v_recycler);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new VideoQueueAdapter(getContext(), true));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
  }

  @Override
  public void setVisibilityByInteractionArea(int visibility, boolean animation) {
    if (visibility == VISIBLE) {
      setVisibilityByListArea(GONE, true, false);
    }
    super.setVisibilityByInteractionArea(visibility, animation);
  }

  // ============================@Queue@============================
  public void setMediaQueue(IMediaQueue mediaQueue) {
    ((VideoQueueAdapter) recyclerView.getAdapter())
        .setMediaQueue(mediaQueue);
  }

  public void setVisibilityByListArea(int visibility, boolean animation, boolean isHandleIntera) {
    if (visibility == VISIBLE) {
      // =========@Show@=========
      if (videoQueueMenu.getVisibility() == View.GONE) {
        //gone interaction area
        if (isHandleIntera) {
          setVisibilityByInteractionArea(GONE, true);
        }
        //show video list
        videoQueueMenu.setVisibility(VISIBLE);
        if (animation) {
          AnimationHelper.showRight(videoQueueMenu, null);
        }
      }
    } else if (visibility == GONE || visibility == INVISIBLE) {
      // =========@Hide@=========
      if (videoQueueMenu.getVisibility() == View.VISIBLE) {
        //gone video list
        if (animation) {
          AnimationHelper
              .hideRight(videoQueueMenu, new AnimationHelper.AnimationActionListener() {
                public void onAnimationEnd() {
                  videoQueueMenu.setVisibility(GONE);
                }
              });
        } else {
          videoQueueMenu.setVisibility(GONE);
        }
        //show interaction area
        if (isHandleIntera && getControlGroup() != null) {
          controlGroup.show();
        }
      }
    }
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.v_queue_menu_open) {
      setVisibilityByListArea(VISIBLE, true, true);
      recyclerView.scrollToPosition(//Scroll to current play video index
          ((VideoQueueAdapter) recyclerView.getAdapter()).getMediaQueue().getCurrentIndex());
    } else if (v.getId() == R.id.v_queue_menu_close) {
      setVisibilityByListArea(GONE, true, true);
    }
  }
}
