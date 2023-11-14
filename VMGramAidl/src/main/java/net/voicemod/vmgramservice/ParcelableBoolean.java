package net.voicemod.vmgramservice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ParcelableBoolean implements Parcelable {
    public boolean bool;

    public boolean value() {
        return bool;
    }

    public ParcelableBoolean() { bool = false; }

    public ParcelableBoolean(boolean b) {
        bool = b;
    }

    protected ParcelableBoolean(Parcel in) {
        bool = in.readInt() != 0;
    }

    public void readFromParcel(Parcel in) {
        bool = in.readInt() != 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(bool ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableBoolean> CREATOR = new Creator<ParcelableBoolean>() {
        @Override
        public ParcelableBoolean createFromParcel(Parcel in) {
            return new ParcelableBoolean(in);
        }

        @Override
        public ParcelableBoolean[] newArray(int size) {
            return new ParcelableBoolean[size];
        }
    };

}
