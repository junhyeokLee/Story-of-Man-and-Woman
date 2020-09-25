package com.dev_sheep.story_of_man_and_woman.data.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ItemImage extends ItemPosition {

	@SerializedName("f_img_seq")
	private int F_img_seq;
	@SerializedName("feed_seq")
	private int Feed_seq;
	@SerializedName("path")
	private String Path;
	@SerializedName("thumb")
	private String Thumb;


	public ItemImage(int f_img_seq, String path, String thumb) {
		super();
		F_img_seq = f_img_seq;
		Path = path;
		Thumb = thumb;

	}

	protected ItemImage(Parcel in) {
		F_img_seq = in.readInt();
		Path = in.readString();
		Thumb = in.readString();

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(F_img_seq);
		dest.writeString(Path);
		dest.writeString(Thumb);

	}

	@Override
	public String toString() {
		return "ItemImage{" +
				"ItemImageId=" + F_img_seq +
				", ImagePath='" + Path + '\'' +
				", Thumb='" + Thumb + '\'' +
				'}';
	}

	public String getThumb() {
		return Thumb;
	}

	public void setThumb(String thumb) {
		Thumb = thumb;
	}

	public int getItemImageId() {
		return F_img_seq;
	}

	public void setItemImageId(int itemImageId) {
		F_img_seq = itemImageId;
	}

	public String getImagePath() {
		return Path;
	}

	public void setImagePath(String imagePath) {
		Path = imagePath;
	}



	public static final Parcelable.Creator<ItemImage> CREATOR = new Parcelable.Creator<ItemImage>() {
		@Override
		public ItemImage createFromParcel(Parcel in) {
			return new ItemImage(in);
		}

		@Override
		public ItemImage[] newArray(int size) {
			return new ItemImage[size];
		}
	};

}
