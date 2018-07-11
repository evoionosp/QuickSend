package com.devshubhpatel.quicksend;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.devshubhpatel.quicksend.MainActivity.realm;


/**
 * Created by patel on 15-07-2017.
 */

 public class Reminder extends RealmObject {
    @PrimaryKey
    private long _id;
    private String rTitle;
    private String rCountry;
    private long rTime;
    private String rMobile;
    private String rMessage;
    private int rType; // 0=Silent 1=Normal 2=Long

    public Reminder() {
    }

    public Reminder(long _id, String rTitle, String rCountry, long rTime, String rMobile, String rMessage, int rType) {
        this._id = _id;
        this.rTitle = rTitle;
        this.rCountry = rCountry;
        this.rTime = rTime;
        this.rMobile = rMobile;
        this.rMessage = rMessage;
        this.rType = rType;
    }

    public Reminder(long _id, String rTitle, String rCountry, long rTime, String rMobile, String rMessage) {
        this._id = _id;
        this.rTitle = rTitle;
        this.rCountry = rCountry;
        this.rTime = rTime;
        this.rMobile = rMobile;
        this.rMessage = rMessage;
        this.rType = R.id.choice_normal;
    }

    public String getrCountry() {
        return rCountry;
    }

    public void setrCountry(String rCountry) {
        this.rCountry = rCountry;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getrTitle() {
        return rTitle;
    }

    public void setrTitle(String rTitle) {
        this.rTitle = rTitle;
    }

    public long getrTime() {
        return rTime;
    }

    public void setrTime(long rTime) {
        this.rTime = rTime;
    }

    public String getrMobile() {
        return rMobile;
    }

    public void setrMobile(String rMobile) {
        this.rMobile = rMobile;
    }

    public String getrMessage() {
        return rMessage;
    }

    public void setrMessage(String rMessage) {
        this.rMessage = rMessage;
    }

    public int getrType() {
        return rType;
    }

    public void setrType(int rType) {
        this.rType = rType;
    }


}
