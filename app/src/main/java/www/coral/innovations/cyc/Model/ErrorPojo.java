package www.coral.innovations.cyc.Model;

import com.google.gson.annotations.SerializedName;

public class ErrorPojo {

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDisplayMessage(){
        if (getError() != null && 4 < getError().length() && getError().length() < 100) {
            return getError();
        }
        return getMessage();
    }
}
