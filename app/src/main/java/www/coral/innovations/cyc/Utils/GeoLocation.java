package www.coral.innovations.cyc.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GeoLocation{
    private Context mContext;

        private String mLatitude;
        private String mLongtitude;
        private String mStreet;
        private String mHouseNumber;
        private String mPostalCode;
        private String mCity;

        private Location mMarkerLocation;

        public GeoLocation (Context context) {
            mContext = context;
        }

        public String getStreet () {
            return mStreet;
        }

        public String getHouseNumber () {
            return mHouseNumber;
        }

        public String getPostalCode () {
            return mPostalCode;
        }

        public String getCity () {
            return mCity;
        }

        public String getLatitude () {
            return mLatitude;
        }

        public String getLongtitude () {
            return mLongtitude;
        }

        // Lookup address via reverse geolocation
// Call this one
        public void lookUpAddress (Location markerLocation) {
            mMarkerLocation = markerLocation;
            if (Geocoder.isPresent()) {
                (new GetAddressTask(mContext)).execute();
            }
        }

public class GetAddressTask extends AsyncTask<Location, Void, String> {

    public GetAddressTask (Context context) {
        super();
        mContext = context;
    }

    @Override
    protected String doInBackground (android.location.Location... params) {
        Geocoder geocoder =
                new Geocoder(mContext, Locale.getDefault());
        android.location.Location location = params[0];

        List<Address> addresses = null;
        try {
            if (mMarkerLocation != null) {
                addresses = geocoder.getFromLocation(mMarkerLocation.getLatitude(),
                        mMarkerLocation.getLongitude(), 1);
            }
        } catch (IOException exception) {
            Log.e("ComplaintLocation",
                    "IO Exception in getFromLocation()", exception);

            return ("IO Exception trying to get address");
        } catch (IllegalArgumentException exception) {
            String errorString = "Illegal arguments " +
                    Double.toString(location.getLatitude()) + " , " +
                    Double.toString(location.getLongitude()) + " passed to address service";
            Log.e("LocationSampleActivity", errorString, exception);

            return errorString;
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            if (address.getMaxAddressLineIndex() > 0) {
                return String.format(
                        "%s/%s/%s/%s/%s/%s",
                        address.getLatitude(), // 0
                        address.getLongitude(), // 1
                        address.getThoroughfare(), // 2
                        address.getSubThoroughfare(), //3
                        address.getPostalCode(), // 4
                        address.getLocality()); // 5
            } else {
                return String.format(
                        "%s/%s/%s/%s",
                        address.getLatitude(), // 0
                        address.getLongitude(), // 1
                        address.getPostalCode(), // 2
                        address.getLocality()); // 3
            }
        } else return "No address found";
    }

    // Format address string after lookup
    @Override
    protected void onPostExecute (String address) {

        String[] addressFields = TextUtils.split(address, "/");
        Log.d("ADDRESS ARRAY", Arrays.toString(addressFields));

        // Workaround: doInBackground can only return Strings instead of, for example, an
        // Address instance or a String[] directly. To be able to use TextUtils.isEmpty()
        // on fields returned by this method, set each String that currently reads "null" to
        // a null reference
        for (int fieldcnt = 0; fieldcnt < addressFields.length; ++fieldcnt) {
            if (addressFields[fieldcnt].equals("null"))
                addressFields[fieldcnt] = null;
        }

        switch (addressFields.length) {
            case 4:
                mStreet = null;
                mHouseNumber = null;
                mLatitude = addressFields[0];
                mLongtitude = addressFields[1];
                mPostalCode = addressFields[2];
                mCity = addressFields[3];
                break;
            case 6:
                mLatitude = addressFields[0];
                mLongtitude = addressFields[1];
                mStreet = addressFields[2];
                mHouseNumber = addressFields[3];
                mPostalCode = addressFields[4];
                mCity = addressFields[5];
                break;
            default:
                mLatitude = null;
                mLongtitude = null;
                mStreet = null;
                mHouseNumber = null;
                mPostalCode = null;
                mCity = null;
                break;
        }

        Log.d("GeoLocation Street", mStreet);
        Log.d("GeoLocation No.", mHouseNumber);
        Log.d("GeoLocation Postalcode", mPostalCode);
        Log.d("GeoLocation Locality", mCity);
        Log.d("GeoLocation Lat/Lng", "[" + mLatitude + ", " + mLongtitude +
                "]");
    }
}
   }