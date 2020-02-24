package www.coralinnovations.cyc.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.TypedValue;

public class Common {
    public static void showAlertDialog(Context ctx, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String formatStringZero(String inp, int size) {
        inp = inp.replaceAll("\\s+","");
        while(inp.length() > 1 && inp.length() < size) {
            inp = "0".concat(inp);
        }
        return inp;
    }

    public static int getPxFromDp(Resources r, float dpValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                r.getDisplayMetrics()
        );
    }
}
