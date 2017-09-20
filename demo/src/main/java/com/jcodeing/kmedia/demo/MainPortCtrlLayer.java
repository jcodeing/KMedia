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
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.jcodeing.kmedia.video.ControlLayerView;

public class MainPortCtrlLayer extends ControlLayerView implements OnClickListener {

  public MainPortCtrlLayer(Context context) {
    super(context);
  }

  public MainPortCtrlLayer(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MainPortCtrlLayer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected int getDefaultLayoutId() {
    return R.layout.ctrl_layer_port_main;
  }

  @Override
  protected void initView() {
    super.initView();
    //Increase play/pause touch area
    //ProgressCircle add click listener handle play(?)
    findViewById(R.id.k_progress_any).setOnClickListener(this);
  }

  @Override
  public boolean isVisibleByPlayController() {
    //Play controller eternity visible.
    //Assist mainActivity replace control layer view progress.
    return true;//progress_main needs to be updated in real time.
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.k_progress_any && getControlGroup() != null) {
      controlGroup.play(2);//Auto handle
    }
  }
}
