package www.coral.innovations.cyc.Storage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import www.coral.innovations.cyc.R;

/**
 * Created by Shreya Kotak on 12/05/16.
 */
public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    /*@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||  ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED|| ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED|| ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(false);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
*/
    public static void alertDialogShow(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String getFacebookUrl(Context applicationContext, String socailLink) {
        if (applicationContext == null) return null;

        PackageManager packageManager = applicationContext.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                Log.d("facebook api", "new");
                return "fb://facewebmodal/f?href=" + socailLink;
            } else { //older versions of fb app
                Log.d("facebook api", "old");
                return "fb://page/" + splitUrl(applicationContext, socailLink);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("facebook api", "exception");
            return socailLink; //normal web url
        }
    }
    public static String splitUrl(Context context, String url) {
        if (context == null) return null;
        Log.d("Split string: ", url + " ");
        try {
            String splittedUrl[] = url.split(".com/");
            Log.d("Split string: ", splittedUrl[1] + " ");
            return splittedUrl.length == 2 ? splittedUrl[1] : url;
        } catch (Exception ex) {
            return url;
        }
    }

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToast(Context applicationContext, String s) {
        Toast toast = Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String checkNull(String value) {
        if (value!=null || "null".equals(value) || "Null".equals(value) || value.length()==0)
        { value="N/A"; }
        return value;
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static String setTimeStamp() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa dd-MMM-yyyy");
        String datetime = dateformat.format(c.getTime());
        System.out.println(datetime);

        return  datetime ;
    }

    public static byte[] convertToByteArray(InputStream inputStream) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int next = inputStream.read();
            while (next > -1) {
                bos.write(next);
                next = inputStream.read();
            }

            bos.flush();

            return bos.toByteArray();
    }

    public static String getLocationAddress(Context context, double latitude, double longitude) {
        String final_address = "";
        List<Address> addresses = null;
        try{
            if (latitude!=0.0 && longitude!=0.0) {
                Geocoder geocoder;
                geocoder = new Geocoder(context, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                final_address =addresses.get(0).getAddressLine(0);
            }else {
                final_address = "" ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  final_address;
    }

    public static boolean isValidMobileNo(String tempContactNumber) {

        boolean FLG = false ;

        if(tempContactNumber.length()!=10){
            FLG = false ;
        }else if (tempContactNumber.charAt(0) == '6'
                || tempContactNumber.charAt(0) == '7'
                || tempContactNumber.charAt(0) == '8'
                || tempContactNumber.charAt(0) == '9') {
            FLG = true ;
        }else{
            FLG = false ;
        }
        return FLG ;
    }


    public static void ZoomImage(Context activity, ImageView capture_preview) {
        Bitmap bitmap = ((BitmapDrawable)capture_preview.getDrawable()).getBitmap();
        if (bitmap!=null) {
            Dialog d = new Dialog(activity);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.imagelayout);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            final ImageView imageView = (ImageView) d.findViewById(R.id.imageView1);
            Glide.with(activity)
                    .load(stream.toByteArray())
                    .into(imageView);
            d.show();

        }else{
            showToast(activity, activity.getString(R.string.NO_IMAGE));
        }
    }

    public static class DecimalUtils {

        public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
            BigDecimal bigDecimal = new BigDecimal(value);
            bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                    BigDecimal.ROUND_HALF_UP);
            return bigDecimal.doubleValue();
        }
    }

    public static boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

}
