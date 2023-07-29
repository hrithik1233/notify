package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

@SuppressLint("RestrictedApi")
public class MessagesReceiver implements Parcelable  {
    long messageId;

    DateAndTime dateAndTime ;
    String requestResult="none";
    //none - if no response
    //accepted- if accepted
    //denied - if denied
    String requesterName="null";
    String resposeTime="";
    String requesterToken="";
    String receiverToken="";
    String requesterEmail="null";
    String requesterbatch="null";
    BatchPinConfigure configure ;
    String reqeustedBatchOFowner="null";
    String idOfRequester="null";
   String ownerName="";

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRequesterToken() {
        return requesterToken;
    }

    public void setRequesterToken(String requesterToken) {
        this.requesterToken = requesterToken;
    }

    public String getReceiverToken() {
        return receiverToken;
    }

    public void setReceiverToken(String receiverToken) {
        this.receiverToken = receiverToken;
    }

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
        this.reqeustedBatchOFowner = reqeustedBatchOFowner;


    }

    public MessagesReceiver() {

    }

    public String isRequestResult() {
        return requestResult;
    }

    public void setRequestResult(String requestResult) {
        this.requestResult = requestResult;
    }

    protected MessagesReceiver(Parcel in) {
        messageId = in.readLong();
        dateAndTime = in.readParcelable(DateAndTime.class.getClassLoader());
        requesterName = in.readString();
        requesterEmail = in.readString();
        requesterbatch = in.readString();
        configure = in.readParcelable(BatchPinConfigure.class.getClassLoader());
        reqeustedBatchOFowner = in.readString();
        idOfRequester = in.readString();
        requestResult=in.readString();
        resposeTime=in.readString();
        receiverToken=in.readString();
        requesterToken=in.readString();
        ownerName=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(messageId);
        dest.writeParcelable(dateAndTime, flags);
        dest.writeString(requesterName);
        dest.writeString(requesterEmail);
        dest.writeString(requesterbatch);
        dest.writeParcelable(configure, flags);
        dest.writeString(reqeustedBatchOFowner);
        dest.writeString(idOfRequester);
        dest.writeString(requestResult);
        dest.writeString(resposeTime);
        dest.writeString(receiverToken);
        dest.writeString(requesterToken);
        dest.writeString(ownerName);

    }

    @Override
    public int describeContents() {
        return 0;
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
        return reqeustedBatchOFowner;
    }



}
