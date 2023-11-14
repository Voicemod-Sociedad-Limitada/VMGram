package net.voicemod.vmgramservice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ParcelableInteger implements Parcelable {
    public int integer;

    public ParcelableInteger() { integer = 0; }

    public ParcelableInteger(int i) {
        integer = i;
    }

    protected ParcelableInteger(Parcel in) {
        integer = in.readInt();
    }

    public void readFromParcel(Parcel in) {
        integer = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(integer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableInteger> CREATOR = new Creator<ParcelableInteger>() {
        @Override
        public ParcelableInteger createFromParcel(Parcel in) {
            return new ParcelableInteger(in);
        }

        @Override
        public ParcelableInteger[] newArray(int size) {
            return new ParcelableInteger[size];
        }
    };

}
