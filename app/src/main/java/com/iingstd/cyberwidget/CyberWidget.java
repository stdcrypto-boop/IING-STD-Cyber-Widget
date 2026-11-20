package com.iingstd.cyberwidget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CyberWidget extends AppWidgetProvider {
 @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) { for(int id:ids) update(c,m,id); }
 private static void update(Context c,AppWidgetManager m,int id) {
  RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
  v.setTextViewText(R.id.date,new SimpleDateFormat("EEEE, dd MMM yyyy",new Locale("id","ID")).format(new Date()));
  Intent b=c.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
  if(b!=null) {
   int level=b.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=b.getIntExtra(BatteryManager.EXTRA_SCALE,100);
   int pct=scale>0?Math.round(level*100f/scale):0;
   int status=b.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
   boolean charging=status==BatteryManager.BATTERY_STATUS_CHARGING||status==BatteryManager.BATTERY_STATUS_FULL;
   v.setTextViewText(R.id.battery,"BAT "+pct+"%"+(charging?" ⚡":""));
   int temp=b.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);
   v.setTextViewText(R.id.temp,temp>=0?String.format(Locale.getDefault(),"%.1f°C",temp/10f):"--°C");
  }
  try {
   ActivityManager am=(ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
   ActivityManager.MemoryInfo info=new ActivityManager.MemoryInfo();am.getMemoryInfo(info);
   v.setTextViewText(R.id.ram,"RAM "+((info.totalMem-info.availMem)*100/info.totalMem)+"%");
  } catch(Exception e) {v.setTextViewText(R.id.ram,"RAM --");}
  updateSignal(c, v);
  v.setTextViewText(R.id.updated,"↻ "+new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date())+" · perbarui");
  Intent refresh=new Intent(c,CyberWidget.class);
  refresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
  refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,new int[]{id});
  PendingIntent pi=PendingIntent.getBroadcast(c,id,refresh,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  v.setOnClickPendingIntent(R.id.updated,pi);
  m.updateAppWidget(id,v);
 }
 private static void updateSignal(Context context, RemoteViews views) {
  String label = "Offline";
  int level = -1;
  try {
   ConnectivityManager connectivity = context.getSystemService(ConnectivityManager.class);
   NetworkCapabilities network = connectivity.getNetworkCapabilities(connectivity.getActiveNetwork());
   if (network != null && network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
    label = "Wi-Fi";
    WifiManager wifi = context.getApplicationContext().getSystemService(WifiManager.class);
    int rssi = wifi.getConnectionInfo().getRssi();
    if (rssi != -127 && rssi < 0) {
     level = WifiManager.calculateSignalLevel(rssi, 5);
    }
   } else if (network != null && network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
    label = "Seluler";
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
     TelephonyManager phone = context.getSystemService(TelephonyManager.class);
     int subscription = SubscriptionManager.getDefaultDataSubscriptionId();
     if (subscription >= 0) {
      phone = phone.createForSubscriptionId(subscription);
     }
     SignalStrength signal = phone.getSignalStrength();
     if (signal != null) {
      level = signal.getLevel();
     }
    }
   } else if (network != null) {
    label = "Online";
   }
  } catch (SecurityException | UnsupportedOperationException ignored) {
   // Keep the network label when the device cannot provide signal strength.
  }

  views.setTextViewText(R.id.signal, label);
  views.setViewVisibility(R.id.signal_bars, level >= 0 ? android.view.View.VISIBLE : android.view.View.GONE);
  if (level >= 0) {
   views.setImageViewBitmap(R.id.signal_bars, drawSignalBars(level));
   views.setContentDescription(R.id.signal_bars, "Sinyal " + level + " dari 4");
  }
 }

 private static Bitmap drawSignalBars(int level) {
  Bitmap bitmap = Bitmap.createBitmap(64, 48, Bitmap.Config.ARGB_8888);
  Canvas canvas = new Canvas(bitmap);
  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  for (int index = 0; index < 4; index++) {
   paint.setColor(index < level ? 0xFF49F9A5 : 0xFF456658);
   int left = index * 16;
   int height = (index + 1) * 12;
   canvas.drawRoundRect(left, 48 - height, left + 12, 48, 2, 2, paint);
  }
  return bitmap;
 }
}
