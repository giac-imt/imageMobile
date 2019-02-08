package imt.fr.frontimagemobile.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultModel implements Parcelable {
    String url;
    float score;

    public ResultModel(String url, float score) {
        this.url = url;
        this.score = score;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeFloat(this.score);
    }

    public static final Parcelable.Creator<ResultModel> CREATOR
            = new Parcelable.Creator<ResultModel>() {
        public ResultModel createFromParcel(Parcel in) {
            return new ResultModel(in);
        }

        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };

    private ResultModel(Parcel in) {
        this.url = in.readString();
        this.score = in.readFloat();
    }
}
