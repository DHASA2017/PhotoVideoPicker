package com.dl.mediapicker.model;

import java.io.Serializable;

public class Photo implements Serializable {
    private int id;
    private String path;//文件路径（图片或者视频）
    private String mimetype;
    private long duration;//视频时长
    private int width, height;
    private long size;
    private long adddate;
    private boolean fullImage;//是否使用原图，默认图片传送需要进过压缩。
    private String thumbnail;//视频缩略图
    private boolean isVideo;//是否是视频 true为视频 false为图片
    public Photo(int id, String path, String mimetype, int width, int height,long size,String thumbnail,boolean isVideo) {
        this.id = id;
        this.path = path;
        this.mimetype = mimetype;
        this.width = width;
        this.height = height;
        this.size = size;
        this.thumbnail = thumbnail;
        this.isVideo = isVideo;
    }

    public Photo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public long getAdddate() {
        return adddate;
    }

    public void setAdddate(long adddate) {
        this.adddate = adddate;
    }

    public boolean isFullImage() {
        return fullImage;
    }

    public void setFullImage(boolean fullImage) {
        this.fullImage = fullImage;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", mimetype='" + mimetype + '\'' +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", size=" + size +
                ", fullImage=" + fullImage +
                '}';
    }
}
