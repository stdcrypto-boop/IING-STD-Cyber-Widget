package com.iingstd.cyberwidget;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b) { super.onCreate(b);
  LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(35,65,35,35);l.setBackgroundColor(Color.rgb(8,20,15));
  TextView t=new TextView(this);t.setText("IING STD CYBER WIDGET\n\nTambahkan widget ukuran 4×4 dari menu Widget di layar utama.\n\nIzin telepon hanya untuk membaca jenis jaringan seluler, bukan untuk menelepon.");t.setTextColor(Color.rgb(68,245,160));t.setTextSize(19);l.addView(t);
  Button btn=new Button(this);btn.setText("IZINKAN INFORMASI JARINGAN");l.addView(btn);btn.setOnClickListener(v->{if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},1);else refresh();});
  Button refresh=new Button(this);refresh.setText("PERBARUI WIDGET");l.addView(refresh);refresh.setOnClickListener(v->refresh());setContentView(l);
 }
 private void refresh(){Intent i=new Intent(this,CyberWidget.class);i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);int[] ids=AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this,CyberWidget.class));i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);sendBroadcast(i);}
 @Override public void onRequestPermissionsResult(int req,String[] perms,int[] grants){super.onRequestPermissionsResult(req,perms,grants);refresh();}
}