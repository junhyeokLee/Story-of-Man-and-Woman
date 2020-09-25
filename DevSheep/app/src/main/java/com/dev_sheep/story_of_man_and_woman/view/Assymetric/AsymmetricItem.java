package com.dev_sheep.story_of_man_and_woman.view.Assymetric;

import android.os.Parcelable;

public interface AsymmetricItem extends Parcelable {
  int getColumnSpan();
  int getRowSpan();
}
