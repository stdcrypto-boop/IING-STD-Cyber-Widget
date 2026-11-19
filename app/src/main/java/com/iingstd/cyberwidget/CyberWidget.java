package com.iingstd.cyberwidget;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class CyberWidget extends AppWidgetProvider {
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids) update(c,m,id);}
 private static void update(Context c,AppWidgetManager m,int id){
  RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
  Intent b=c.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
  if(b!=null){int level=b.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=b.getIntExtra(BatteryManager.EXTRA_SCALE,100);int pct=scale>0?Math.round(level*100f/scale):0;int status=b.getIntExtra(BatteryManager.EXTRA_STATUS,-1);boolean charging=status==BatteryManager.BATTERY_STATUS_CHARGING||status==BatteryManager.BATTERY_STATUS_FULL;
   v.setTextViewText(R.id.battery,"🔋 "+pct+"%"+(charging?" • Mengisi":""));int temp=b.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);v.setTextViewText(R.id.temp,"🌡 "+(temp>=0?String.format(Locale.getDefault(),"%.1f°C",temp/10f):"Tidak tersedia"));}
  try{StatFs fs=new StatFs(Environment.getDataDirectory().getPath());long total=fs.getTotalBytes(),free=fs.getAvailableBytes();v.setTextViewText(R.id.storage,"▣ "+((total-free)*100/total)+"% terpakai");}catch(Exception e){v.setTextViewText(R.id.storage,"▣ Penyimpanan: Tidak tersedia");}
  v.setTextViewText(R.id.network,"◉ "+network(c));
  v.setTextViewText(R.id.updated,"↻ "+new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date())+" · segarkan");
  Intent refresh=new Intent(c,CyberWidget.class);refresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,new int[]{id});PendingIntent pi=PendingIntent.getBroadcast(c,id,refresh,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);v.setOnClickPendingIntent(R.id.updated,pi);
  m.updateAppWidget(id,v);
 }
 private static String network(Context c){try{
  ConnectivityManager cm=(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);NetworkCapabilities cap=cm.getNetworkCapabilities(cm.getActiveNetwork());if(cap==null)return "Tidak terhubung";
  if(cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))return "Wi-Fi";
  if(!cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))return "Koneksi lain";
  if(c.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)return "Seluler (izin diperlukan)";
  TelephonyManager tm=(TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);int type=tm.getDataNetworkType();
  if(type==TelephonyManager.NETWORK_TYPE_NR)return "5G";
  if(type==TelephonyManager.NETWORK_TYPE_LTE)return "4G LTE";
  if(type==TelephonyManager.NETWORK_TYPE_HSPAP||type==TelephonyManager.NETWORK_TYPE_UMTS||type==TelephonyManager.NETWORK_TYPE_HSDPA||type==TelephonyManager.NETWORK_TYPE_HSUPA)return "3G";
  if(type==TelephonyManager.NETWORK_TYPE_EDGE||type==TelephonyManager.NETWORK_TYPE_GPRS)return "2G";
  return "Seluler (jenis tidak diketahui)";
 }catch(Exception e){return "Tidak tersedia";}}
}