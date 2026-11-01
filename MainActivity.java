package com.thanmoe.burmesevoiceledger;
import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.content.pm.PackageManager;
import android.webkit.*;
import android.widget.Toast;

public class MainActivity extends Activity {
    WebView webView;
    PermissionRequest pending;
    static final int MIC=71;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        webView=new WebView(this); setContentView(webView);
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true); s.setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(PermissionRequest r) {
                runOnUiThread(() -> {
                    boolean audio=false;
                    for(String x:r.getResources()) if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(x)) audio=true;
                    if(!audio){r.deny();return;}
                    if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
                        pending=r; requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},MIC);
                    } else r.grant(r.getResources());
                });
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }
    @Override public void onRequestPermissionsResult(int c,String[] p,int[] g){
        super.onRequestPermissionsResult(c,p,g);
        if(c==MIC && pending!=null){
            if(g.length>0 && g[0]==PackageManager.PERMISSION_GRANTED) pending.grant(pending.getResources());
            else {pending.deny();Toast.makeText(this,"အသံအသုံးပြုခွင့် မပေးထားပါ",Toast.LENGTH_LONG).show();}
            pending=null;
        }
    }
    @Override public void onBackPressed(){if(webView!=null&&webView.canGoBack())webView.goBack();else super.onBackPressed();}
}
