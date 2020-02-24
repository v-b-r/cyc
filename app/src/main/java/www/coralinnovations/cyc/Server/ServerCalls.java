package www.coralinnovations.cyc.Server;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import www.coralinnovations.cyc.Model.DetailedChargesModel;
import www.coralinnovations.cyc.Model.ErrorPojo;
import www.coralinnovations.cyc.Model.GetChargesModel;
import www.coralinnovations.cyc.Storage.Constants;
import www.coralinnovations.cyc.Storage.Storage;
import www.coralinnovations.cyc.Storage.Utility;

import static www.coralinnovations.cyc.Utils.Common.showAlertDialog;


public class ServerCalls {
    private static int autoRetryCount = 0; // TODO: Fix this bad logic

    public static void errorHandler(Context ctx, Response response, boolean showAlert) {
        if (response.errorBody() != null) {
            try {
                ErrorPojo mError= new Gson().fromJson(response.errorBody().string(),ErrorPojo.class);
                if (showAlert) {
                    showAlertDialog(ctx, "Failed", mError.getDisplayMessage());
                } else {
                    Toast.makeText(ctx, mError.getDisplayMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Call<JsonObject> getDetailedCharges(final Context ctx, final GetChargesModel chargesReqModel, TextView calculated_units, ParseObject readingParseObj, Storage storage) {

        Retrofit retrofit = NetworkClient.getRetrofitClient(ctx);
        GetDetailedChargesAPI getChargesAPI = retrofit.create(GetDetailedChargesAPI.class);
//        GetChargesModel summaryModel = new GetChargesModel(serviceNo, serialNo, meterReading, startTime, endTime);

        final ProgressDialog mProgressDialog = new ProgressDialog(ctx);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        Call<JsonObject> call = getChargesAPI.fetchData(chargesReqModel.toMap());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.d("resp", response.toString());
                if (response.code() > 310) {
                    if (response.code() == 422) {
//                        DatabaseManager dbManager = new DatabaseManager(ctx);
//                        chargesReqModel.setSync(1);
//                        App.getDaoSession().getMeterSummaryModelDao().insertOrReplace(chargesReqModel);
//                        dbManager.onServerSaveMeterSummaryData(summaryModel.getEndTimeStamp());
                    }
                    errorHandler(ctx, response, true);
                } else if (response.body() != null && response.body().isJsonObject()){
                    JsonObject jsonResp = response.body().getAsJsonObject();
                    JsonObject readingData = jsonResp.getAsJsonObject("detailedCharges");

//                    chargesReqModel.setSync(1);
//                    App.getDaoSession().getMeterSummaryModelDao().insertOrReplace(chargesReqModel);
//                    DatabaseManager dbManager = new DatabaseManager(ctx);
//                    dbManager.onServerSaveMeterSummaryData(endTime);

                    if (readingData != null && readingData.has("energyCharges")) {

                        DetailedChargesModel detailedCharges = new Gson().fromJson(readingData.toString(), DetailedChargesModel.class);
//                        App.getDaoSession().getBillSummaryModelDao().save(billSummary);
//                        dbManager.saveBillSummaryData(billSummary);

                        Log.d("resp", "Energy Charges: "+ readingData.get("energyCharges").getAsString());
                        Log.d("resp", "Energy Charges: "+ detailedCharges.getEnergyCharges());

                        if (calculated_units.isShown()) {
                            calculated_units.setText(String.valueOf(detailedCharges.getEnergyCharges()));
                        }
                        final ProgressDialog dialog = new ProgressDialog(ctx);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setMessage("Please wait");
                        dialog.show();
                        readingParseObj.put("ExpectedBill", String.valueOf(detailedCharges.getEnergyCharges()));
                        readingParseObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i("ParseException", "==>" + e);
                                dialog.dismiss();
                                if (e == null) {
                                    storage.saveSecure(Constants.AVG_UNIT_PER_DAY, readingParseObj.getString("AverageUnit"));
                                } else {
                                    Utility.showToast(ctx, "Please try Again...!");
                                }
                            }
                        });

//                        showAlertDialog(ctx, "Success", "Bill Amount: "+ readingData.get("billAmount").getAsString());
                    } else {
                        Log.d("resp", "Bill Amount: Failed to calculate");
                        Toast.makeText(ctx, "Unable to calculate Bill!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("resp", "Non Json resp: " + new Gson().toJson(response.body()));
                    Toast.makeText(ctx, "Improper response from server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.d("resp", t.toString());
                Toast.makeText(ctx, "Error posting bill data!", Toast.LENGTH_SHORT).show();
//                if (autoRetryCount < 1) {
//                    autoRetryCount++;
//                    saveMeterReadingSummary(ctx, serviceNo, serialNo, meterReading, startTime, endTime);
//                }
            }
        });
        return call;
    }

}
