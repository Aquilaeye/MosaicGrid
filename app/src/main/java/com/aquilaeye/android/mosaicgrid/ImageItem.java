package com.aquilaeye.android.mosaicgrid;

public class ImageItem {
    public static final byte HIGH = 3;
    public static final byte NORMAL = 0;
    public static final byte lOW = -3;

    private int photo;
    private byte ImgPref;

    public ImageItem(int photo){
       this(photo,(byte) 0);
    }
    /**
     * @param photo           Current context, will be used to access resources.
     * @param ImagePreference Layout orientation. Should be {@link #HIGH} or {@link
     *                        #NORMAL} or {@link #lOW}.
     */
    public ImageItem(int photo, byte ImagePreference) {
        this.photo = photo;
        this.ImgPref = ImagePreference;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getImgPref() {
        return ImgPref;
    }

    public void setImgPref(byte ImagePreference) {
        this.ImgPref = ImagePreference;
    }

}
