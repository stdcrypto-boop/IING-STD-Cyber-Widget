package com.iingstd.cyberwidget;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.app.ActivityManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.telephony.TelephonyManager;
import android.telephony.SignalStrength;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class CyberWidget extends AppWidgetProvider {
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids) update(c,m,id);}
 @Override public void onReceive(Context c,Intent i){super.onReceive(c,i);if(Intent.ACTION_BATTERY_CHANGED.equals(i.getAction())||Intent.ACTION_POWER_CONNECTED.equals(i.getAction())||Intent.ACTION_POWER_DISCONNECTED.equals(i.getAction())){AppWidgetManager m=AppWidgetManager.getInstance(c);android.content.ComponentName n=new android.content.ComponentName(c,CyberWidget.class);for(int id:m.getAppWidgetIds(n))update(c,m,id);}}
 private static void update(Context c,AppWidgetManager m,int id){
 RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
 Intent b=c.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
 if(b!=null){int level=b.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=b.getIntExtra(BatteryManager.EXTRA_SCALE,100);int pct=scale>0&&level>=0?Math.round(level*100f/scale):-1;int status=b.getIntExtra(BatteryManager.EXTRA_STATUS,-1);boolean charging=status==BatteryManager.BATTERY_STATUS_CHARGING||status==BatteryManager.BATTERY_STATUS_FULL;v.setTextViewText(R.id.battery,"BAT "+(pct<0?"--":pct+"%")+(charging?" ⚡":""));int temp=b.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);v.setTextViewText(R.id.temp,temp>=0?String.format(Locale.getDefault(),"%.1f°C",temp/10f):"TEMP --");}
 try{ActivityManager am=(ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();am.getMemoryInfo(mi);long used=mi.totalMem-mi.availMem;v.setTextViewText(R.id.storage,"RAM "+(mi.totalMem>0?used*100/mi.totalMem+"%":"--"));}catch(Exception e){v.setTextViewText(R.id.storage,"RAM --");}
 try{ConnectivityManager cm=(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);NetworkCapabilities cap=cm.getNetworkCapabilities(cm.getActiveNetwork());if(cap==null){v.setTextViewText(R.id.network,"Offline");v.setTextViewText(R.id.signal,"Sinyal: --");}else if(cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){v.setTextViewText(R.id.network,"Wi-Fi");android.net.wifi.WifiManager wm=(android.net.wifi.WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);int rssi=wm.getConnectionInfo().getRssi();int bars=android.net.wifi.WifiManager.calculateSignalLevel(rssi,5);v.setTextViewText(R.id.signal,"Wi-Fi "+bars+"/4 bar");}else if(cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){TelephonyManager tm=(TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);if(c.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){int t=tm.getDataNetworkType();v.setTextViewText(R.id.network,t==TelephonyManager.NETWORK_TYPE_NR?"5G":t==TelephonyManager.NETWORK_TYPE_LTE?"4G LTE":"Seluler");}else v.setTextViewText(R.id.network,"Seluler");SignalStrength s=tm.getSignalStrength();v.setTextViewText(R.id.signal,s==null?"Sinyal: --":"Sinyal "+Math.max(0,Math.min(4,s.getLevel()))+"/4 bar");}else{v.setTextViewText(R.id.network,"Koneksi lain");v.setTextViewText(R.id.signal,"Sinyal: --");}}catch(Exception e){v.setTextViewText(R.id.network,"Jaringan tidak tersedia");v.setTextViewText(R.id.signal,"Sinyal: --");}
 int bars=-1;
 try { ConnectivityManager cm2=(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE); NetworkCapabilities nc=cm2.getNetworkCapabilities(cm2.getActiveNetwork());
 if(nc!=null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){android.net.wifi.WifiManager w=(android.net.wifi.WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);bars=android.net.wifi.WifiManager.calculateSignalLevel(w.getConnectionInfo().getRssi(),5);}
 else if(nc!=null && nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){TelephonyManager t=(TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);SignalStrength s=t.getSignalStrength();if(s!=null)bars=Math.max(0,Math.min(4,s.getLevel()));}
 }catch(Exception ignored){}
 int[] barIds={R.id.bar1,R.id.bar2,R.id.bar3,R.id.bar4};
 for(int j=0;j<4;j++)v.setInt(barIds[j],"setBackgroundColor",j<bars?android.graphics.Color.rgb(0,221,255):android.graphics.Color.rgb(40,65,65));
 v.setTextViewText(R.id.updated,"↻ "+new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date())+" · Perbarui");
 Intent refresh=new Intent(c,CyberWidget.class);refresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,new int[]{id});PendingIntent pi=PendingIntent.getBroadcast(c,id,refresh,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);v.setOnClickPendingIntent(R.id.updated,pi);m.updateAppWidget(id,v);
 }
}