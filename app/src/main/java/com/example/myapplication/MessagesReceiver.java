package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

@SuppressLint("RestrictedApi")
public class MessagesReceiver implements Parcelable {
    long messageId;
    DateAndTime dateAndTime;
    String requesterName;
    String requesterEmail;
    String requesterbatch;
    BatchPinConfigure configure;
    String ReqeustedBatchOfOwner;
    String idOfRequester;

    public MessagesReceiver(long messageId, DateAndTime dt, String requesterName,
                            String requesterEmail, String requesterbatch, BatchPinConfigure configure,
                            String idOfRequester, String reqeustedBatchOFowner) {
        this.messageId = messageId;
        dateAndTime=dt;
        this.requesterName = requesterName;
        this.requesterEmail = requesterEmail;
        this.requesterbatch = requesterbatch;
        this.configure = configure;
        this.idOfRequester=idOfRequester;
        this.ReqeustedBatchOfOwner = reqeustedBatchOFowner;
    }

    public MessagesReceiver() {
    }

    protected MessagesReceiver(Parcel in) {
        messageId = in.readLong();
        requesterName = in.readString();
        requesterEmail = in.readString();
        requesterbatch = in.readString();
        ReqeustedBatchOfOwner = in.readString();
        idOfRequester = in.readString();
    }

    public static final Creator<MessagesReceiver> CREATOR = new Creator<MessagesReceiver>() {
        @Override
        public MessagesReceiver createFromParcel(Parcel in) {
            return new MessagesReceiver(in);
        }

        @Override
        public MessagesReceiver[] newArray(int size) {
            return new MessagesReceiver[size];
        }
    };

    public long getMessageId() {
        return messageId;
    }

    public DateAndTime getDateAndTime() {
        return dateAndTime;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public String getRequesterbatch() {
        return requesterbatch;
    }

    public BatchPinConfigure getConfigure() {
        return configure;
    }

    public String getidOfRequester() {
        return idOfRequester;
    }

    public String getReqeustedBatchOFowner() {
        return ReqeustedBatchOfOwner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(messageId);
        parcel.writeString(requesterName);
        parcel.writeString(requesterEmail);
        parcel.writeString(requesterbatch);
        parcel.writeString(ReqeustedBatchOfOwner);
        parcel.writeString(idOfRequester);
    }
}
