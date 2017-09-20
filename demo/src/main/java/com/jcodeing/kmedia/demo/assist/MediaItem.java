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

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import com.jcodeing.kmedia.definition.IMediaItem;
import com.jcodeing.kmedia.definition.IPositionUnit;
import com.jcodeing.kmedia.definition.IPositionUnitList;
import com.jcodeing.kmedia.utils.Assert;
import java.util.ArrayList;
import java.util.List;

public class MediaItem implements IMediaItem, IPositionUnitList, android.os.Parcelable {

  private String mediaId;
  private String mediaUri;

  private String title;
  private String description;
  private String iconUri;

  private List<PosUnit> posUnitList;

  private Bundle extras;

  @Override
  public String getMediaId() {
    return mediaId;
  }

  @Override
  public Uri getMediaUri() {
    return mediaUri != null ? Uri.parse(mediaUri) : null;
  }

  @Override
  public CharSequence getTitle() {
    return title;
  }

  @Override
  public CharSequence getDescription() {
    return description;
  }

  @Override
  public Uri getIconUri() {
    return iconUri != null ? Uri.parse(iconUri) : null;
  }

  @Override
  public Bundle getExtras() {
    return extras;
  }

  // ============================@IPositionUnitList
  @Override
  public int positionUnitSize() {
    return posUnitList != null ? posUnitList.size() : 0;
  }

  @Override
  public long getStartPosition(int posUnitIndex) {
    return posUnitList != null && Assert.checkIndex(posUnitIndex, positionUnitSize()) ?
        posUnitList.get(posUnitIndex).getStartPos() : 0;
  }

  @Override
  public long getEndPosition(int posUnitIndex) {
    return posUnitList != null && Assert.checkIndex(posUnitIndex, positionUnitSize()) ?
        posUnitList.get(posUnitIndex).getEndPos() : Integer.MAX_VALUE;
  }

  public String getSubtitleEn(int posUnitIndex) {
    return posUnitList != null && Assert.checkIndex(posUnitIndex, positionUnitSize()) ?
        posUnitList.get(posUnitIndex).getEn() : null;
  }

  public String getSubtitleCn(int posUnitIndex) {
    return posUnitList != null && Assert.checkIndex(posUnitIndex, positionUnitSize()) ?
        posUnitList.get(posUnitIndex).getCn() : null;
  }

  // ============================@PosUnit
  public class PosUnit implements IPositionUnit {

    long start;
    long end;

    String cn;
    String en;

    @Override
    public long getStartPos() {
      return start;
    }

    @Override
    public long getEndPos() {
      return end;
    }

    public String getCn() {
      return cn;
    }

    public String getEn() {
      return en;
    }
  }


  // ============================@Parcelable
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mediaId);
    dest.writeString(this.mediaUri);
    dest.writeString(this.title);
    dest.writeString(this.description);
    dest.writeString(this.iconUri);
    dest.writeList(this.posUnitList);
    dest.writeBundle(this.extras);
  }

  public MediaItem() {
  }

  protected MediaItem(Parcel in) {
    this.mediaId = in.readString();
    this.mediaUri = in.readString();
    this.title = in.readString();
    this.description = in.readString();
    this.iconUri = in.readString();
    this.posUnitList = new ArrayList<>();
    in.readList(this.posUnitList, PosUnit.class.getClassLoader());
    this.extras = in.readBundle(getClass().getClassLoader());
  }

  public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
    @Override
    public MediaItem createFromParcel(Parcel source) {
      return new MediaItem(source);
    }

    @Override
    public MediaItem[] newArray(int size) {
      return new MediaItem[size];
    }
  };
}
