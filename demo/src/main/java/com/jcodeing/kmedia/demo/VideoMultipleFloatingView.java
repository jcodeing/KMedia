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
import com.jcodeing.kmedia.window.FloatingWindow;
import com.jcodeing.kmedia.window.VideoFloatingWindowView;

public class VideoMultipleFloatingView extends VideoFloatingWindowView {

  private int minWidth = 111;
  private int minHeight = 111;

  public VideoMultipleFloatingView(Context context, int minWidth, int minHeight) {
    super(context);
    this.minWidth = minWidth;
    this.minHeight = minHeight;
  }

  public VideoMultipleFloatingView(Context context) {
    super(context);
  }

  public VideoMultipleFloatingView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoMultipleFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected int getDefaultLayoutId() {
    return R.layout.floating_video_view_multiple;
  }

  @Override
  protected void onSet(FloatingWindow floatingWindow) {
    this.floatingWindow = floatingWindow;
    floatingWindow
        .setMinWidthHeight(minWidth, minHeight);
    floatingWindow
        .setMaxWidthHeight(displayWidth, displayHeight);
  }
}
