package com.example.jake.caastme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.attr.scheme;

/**
 * Created by jake on 2016/10/3.
 */
public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;

    String server_url;
    //分享的地址
    String redirect_url;

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.test);

        //init dbmanager
        dbManager = new DBManager(this);
        //get shared url
        redirect_url = handleShare();

        //服务器的地址
        server_url = Constants.getProperty("url", this.getApplicationContext());
        QrScanner();
    }

    public void QrScanner() {
        mScannerView = new ZXingScannerView(this); // Programmatically initialize the scanner view
        mScannerView.startCamera(); // Start camera
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        setContentView(mScannerView);

        //确保startCamera这个线程完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera(); // Stop camera on pause
    }


    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here


        if (redirect_url != null && !redirect_url.isEmpty()) {
            try {
                redirect_url = URLEncoder.encode(redirect_url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            String code = rawResult.getText();
            Log.i("caastinfo1", code); // Prints scan results
            final String url_address = server_url + "redirect?code=" + code + "&redirect_url=" + redirect_url;

            // 开启新的线程
            new Thread() {
                public void run() {
                    try {
                        // 耗时操作
                        Log.i("xxxxd", url_address);
                        String result = NetUtil.getCall(url_address);
                        Log.i("caastinfo_result", result == null ? "null" : result); // Prints scan results
                        Message msg = Message.obtain();
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }.start();

        }

// show the scanner result into dialog box.
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();*/

// If you would like to resume scanning, call this method below:
// mScannerView.resumeCameraPreview(this);
    }


    public String handleShare() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String share_url = null;
        //判断他是否是分享过来的。不是的话，就是点击item列表过来的
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                //得到分享的内容（里面包含网址）
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

                if (sharedText != null && !sharedText.isEmpty()) {
                    //得到分享的标题
                    String sharedSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                    Log.i("shared_info", "分享的标题：" + sharedSubject);

                    Log.i("shared_info", "分享的内容：" + sharedText);
                    share_url = sharedText.substring(sharedText.indexOf("http"));

                    String favicon = null;
                    try {
                        URI uri = new URI(share_url);
                        String domain = uri.getHost();
                        //这个是zzd 就是uc浏览器的早知道，
                        if(domain.contains("uc") || domain.contains("zzd")){
                           //uc_favicon
                            favicon = Constants.getProperty("uc_favicon",this.getApplicationContext());
                        }else{

                            domain =  domain.startsWith("www.") ? domain.substring(4) : domain;
                            String scheme = uri.getScheme();
                            favicon = scheme+"://"+domain+"/favicon.ico";

                        }



                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    ShareEntity shareEntity = new ShareEntity();
                    shareEntity.setFavicon(favicon);
                    shareEntity.setTitle(sharedSubject == null ? share_url : sharedSubject);
                    shareEntity.setUrl(share_url);
                    dbManager.add(shareEntity);

                }

            }
        } else {
            share_url = intent.getStringExtra("redirect_url"); //if it's a string you stored.
        }

        return share_url;
    }


    //工作线程
    //http://blog.csdn.net/withiter/article/details/19908679
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // 更新UI
            super.handleMessage(msg);
            Log.i("caastinfo", "请求结果11:" + msg.obj.toString());
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg.obj.toString());
                Toast.makeText(ScannerActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    MyHandler mHandler = new MyHandler();


    public void goToListActivity() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //应用的最后一个Activity关闭时应释放DB
        dbManager.closeDB();
    }


}
