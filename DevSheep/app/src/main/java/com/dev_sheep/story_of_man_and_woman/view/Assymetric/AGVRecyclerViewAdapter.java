package com.dev_sheep.story_of_man_and_woman.view.Assymetric;


import androidx.recyclerview.widget.RecyclerView;

public abstract class AGVRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
  public abstract AsymmetricItem getItem(int position);
}
