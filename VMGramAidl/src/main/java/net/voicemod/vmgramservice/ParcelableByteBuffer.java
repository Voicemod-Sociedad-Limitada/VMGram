package net.voicemod.vmgramservice;

import android.os.Parcel;
import android.os.Parcelable;

import java.nio.ByteBuffer;

public class ParcelableByteBuffer implements Parcelable {
    private ByteBuffer byteBuffer;

    public ParcelableByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    protected ParcelableByteBuffer(Parcel in) {
        byteBuffer = ByteBuffer.wrap(in.createByteArray());
    }

    public void readFromParcel(Parcel in) {
        byteBuffer = ByteBuffer.wrap(in.createByteArray());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(byteBuffer.array());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableByteBuffer> CREATOR = new Creator<ParcelableByteBuffer>() {
        @Override
        public ParcelableByteBuffer createFromParcel(Parcel in) {
            return new ParcelableByteBuffer(in);
        }

        @Override
        public ParcelableByteBuffer[] newArray(int size) {
            return new ParcelableByteBuffer[size];
        }
    };

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}
