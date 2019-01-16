package com.project.itai.FindAPlaceVer2.beans;

import java.io.Serializable;
import java.util.Objects;

public class SearchPlace implements Serializable {

private Integer id;
private String name;
private String address;
private String urlImage;
private double lat;
private double lng;
private long timeStamp;

    public SearchPlace() {
    }

    public SearchPlace(Integer id, String name, String address, String urlImage, double lat, double lng, long timeStamp) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.urlImage = urlImage;
        this.lat = lat;
        this.lng = lng;
        this.timeStamp = timeStamp;
    }

    public SearchPlace(String name, String address, String urlImage, double lat, double lng, long timeStamp) {
        this.name = name;
        this.address = address;
        this.urlImage = urlImage;
        this.lat = lat;
        this.lng = lng;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchPlace)) return false;
        SearchPlace that = (SearchPlace) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lng, lng) == 0 &&
                timeStamp == that.timeStamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address) &&
                Objects.equals(urlImage, that.urlImage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, address, urlImage, lat, lng, timeStamp);
    }

    @Override
    public String toString() {
        return "SearchPlace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", timeStamp=" + timeStamp +
                '}';
    }

}



