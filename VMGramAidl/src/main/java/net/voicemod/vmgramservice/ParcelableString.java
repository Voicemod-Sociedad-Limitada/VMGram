package net.voicemod.vmgramservice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ParcelableString implements Parcelable {
    public String string;

    public ParcelableString() {}

    public ParcelableString(String string) {
        this.string = string;
    }

    protected ParcelableString(Parcel in) {
        string = in.readString();
    }

    public void readFromParcel(Parcel in) {
        string = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(string);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableString> CREATOR = new Creator<ParcelableString>() {
        @Override
        public ParcelableString createFromParcel(Parcel in) {
            return new ParcelableString(in);
        }

        @Override
        public ParcelableString[] newArray(int size) {
            return new ParcelableString[size];
        }
    };

    public String getString() {
        return string;
    }
}
