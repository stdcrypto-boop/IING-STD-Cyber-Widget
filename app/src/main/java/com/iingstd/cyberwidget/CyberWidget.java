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
  v.setTextViewText(R.id.signal,signal(c));
  v.setTextViewText(R.id.updated,"↻ "+new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date())+" · perbarui");
  Intent refresh=new Intent(c,CyberWidget.class);
  refresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
  refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,new int[]{id});
  PendingIntent pi=PendingIntent.getBroadcast(c,id,refresh,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  v.setOnClickPendingIntent(R.id.updated,pi);
  m.updateAppWidget(id,v);
 }
 private static String signal(Context c) {
  try {
   ConnectivityManager cm=(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
   NetworkCapabilities cap=cm.getNetworkCapabilities(cm.getActiveNetwork());
   if(cap==null)return "▁▁▁▁ OFF";
   if(cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
    WifiManager wifi=(WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    int rssi=wifi.getConnectionInfo().getRssi();
    if(rssi<=-100 || rssi>=0)return "▁▁▁▁ Wi-Fi";
    int bars=WifiManager.calculateSignalLevel(rssi,5);
    return (bars>=1?"▂":"▁")+(bars>=2?"▄":"▁")+(bars>=3?"▆":"▁")+(bars>=4?"█":"▁")+" Wi-Fi";
   }
   if(cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))return "▁▁▁▁ Seluler";
   return "▁▁▁▁ Online";
  }catch(Exception e){return "▁▁▁▁ --";}
 }
}
