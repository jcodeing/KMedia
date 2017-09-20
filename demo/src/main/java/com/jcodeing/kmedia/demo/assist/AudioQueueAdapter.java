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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jcodeing.kmedia.adapter.MediaQueueRecyclerAdapter;
import com.jcodeing.kmedia.definition.IMediaItem;
import com.jcodeing.kmedia.demo.R;
import com.jcodeing.kmedia.demo.assist.AudioQueueAdapter.LocalViewHolder;

public class AudioQueueAdapter extends MediaQueueRecyclerAdapter<LocalViewHolder> {

  private final LayoutInflater inflater;

  public AudioQueueAdapter(Context context) {
    inflater = LayoutInflater.from(context);
  }

  @Override
  public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new LocalViewHolder(
        inflater.inflate(R.layout.item_audio_queue, parent, false));
  }

  @Override
  public void onBindViewHolder(LocalViewHolder holder, int position) {
    holder.bindViewData(mediaQueue.getMediaItem(position), position);
  }

  class LocalViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    LocalViewHolder(View view) {
      super(view);
      view.setOnClickListener(this);
    }

    void bindViewData(IMediaItem mediaItem, int position) {
      ((TextView) itemView).setText(mediaItem.getTitle());
      itemView.setSelected(position == mediaQueue.getCurrentIndex());
    }

    @Override
    public void onClick(View v) {
      mediaQueue.skipToIndex(getLayoutPosition());
    }
  }
}