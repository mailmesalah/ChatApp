package chatapp.extras;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class SwissArmyKnife {
	public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 0, outputStream);		
		return outputStream.toByteArray();
	}
	
	public static Bitmap getBitmapFromByteArray(byte[] imgByte){		 
		return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
	}
	
	public static long getUTCTimeInMilliseconds(){
		Calendar curDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		System.out.println("Current Date in UTC "+curDate.getTime());
		return curDate.getTimeInMillis();		
	}

	public static String getCurrentLocalDateTime(String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				pattern, Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getLocalDateTime(String dateString,String pattern) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date=sdf.parse(dateString);
		sdf.setTimeZone(TimeZone.getDefault());		
		return sdf.format(date);
	}

	public static String getLocalDateTime(long datemillisec,String pattern) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar calendar = sdf.getCalendar();
		calendar.setTimeInMillis(datemillisec);
		Date date=calendar.getTime();
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(date);
	}
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static boolean isGPSOn(Context pContext){
		//Checks if the GPS device is available and enabled with the current context
		LocationManager locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public static void showGPSDisabledAlertToUser(final Context pContext){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(pContext);
		alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Goto Settings Page To Enable GPS",
						new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int id){
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								pContext.startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
						//Exits the app because there is no GPS Activated
						System.exit(0);
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

    /*
	 * Gets all the contacts in the device
	 */
    public static Map<String, String> getContacts(Context context){
        Map<String, String> contacts = new Hashtable<String, String>();
        ContentResolver cr =  context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.put(phoneNo, name);
                    }
                    pCur.close();
                }
            }
        }
        return contacts;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

	public static Bitmap getScaledBitMapBaseOnScreenSize(Bitmap bitmapOriginal, Activity activity){

		Bitmap scaledBitmap=null;
		try {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);


			int width = bitmapOriginal.getWidth();
			int height = bitmapOriginal.getHeight();

			float scaleWidth = metrics.scaledDensity;
			float scaleHeight = metrics.scaledDensity;

			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scaleWidth, scaleHeight);

			// recreate the new Bitmap
			scaledBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return scaledBitmap;
	}

	public static String encodeTobase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		return imageEncoded;
	}

	public static Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static int getIntegerValue(String stringV){
		byte[] bytes = stringV.getBytes();
        String sInt="";
		Log.d("int val","here"+stringV);
        for (byte b:bytes) {
			sInt=sInt.concat(Integer.toBinaryString(b)+"");
            Log.d("str v",sInt);
        }

        return Integer.parseInt(sInt,2);
    }

}
