package www.coralinnovations.cyc.Server;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import www.coralinnovations.cyc.Model.GetChargesModel;


interface FetchServiceNumberAPI {
    @GET("/fetchServiceNumber")
    Call<JsonObject> getData(@Query("serialNo") String serialNo);
}

interface GetDetailedChargesAPI {
//    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @GET("/getCharges")
    Call<JsonObject> fetchData(@QueryMap Map<String, String> requestParams);
}
