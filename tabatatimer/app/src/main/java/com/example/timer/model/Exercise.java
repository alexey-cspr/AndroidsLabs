package com.example.timer.model;

import android.os.Parcel;
import android.os.Parcelable;

import static androidx.room.ForeignKey.CASCADE;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Training.class, parentColumns = "uid", childColumns = "training_id", onDelete = CASCADE))
public class Exercise implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "training_id")
    public int training_id;

    @ColumnInfo(name = "time")
    public int time;

    public Exercise(){
    }

    protected Exercise(Parcel in){
        uid = in.readInt();
        training_id = in.readInt()    ;
        time = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(name);
        dest.writeInt(training_id);
        dest.writeInt(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}
