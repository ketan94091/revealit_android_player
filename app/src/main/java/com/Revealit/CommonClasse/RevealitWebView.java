package com.Revealit.CommonClasse;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RevealitWebView extends WebView {

    public RevealitWebView(Context context) {
        super(context);
    }

    public void enablecrossdomain() {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field.setAccessible(true);
            Object webviewcore = field.get(this);
            Method method = webviewcore.getClass().getDeclaredMethod("nativeRegisterURLSchemeAsLocal", String.class);
            method.setAccessible(true);
            method.invoke(webviewcore, "http");
            method.invoke(webviewcore, "https");
        } catch (Exception e) {
            Log.d("wokao", "enablecrossdomain error");
            e.printStackTrace();
        }
    }

    //for android 4.1+
    public void enablecrossdomain41() {
        try {
            Field webviewclassic_field = WebView.class.getDeclaredField("mProvider");
            webviewclassic_field.setAccessible(true);
            Object webviewclassic = webviewclassic_field.get(this);
            Field webviewcore_field = webviewclassic.getClass().getDeclaredField("mWebViewCore");
            webviewcore_field.setAccessible(true);
            Object mWebViewCore = webviewcore_field.get(webviewclassic);
            Field nativeclass_field = webviewclassic.getClass().getDeclaredField("mNativeClass");
            nativeclass_field.setAccessible(true);
            Object mNativeClass = nativeclass_field.get(webviewclassic);

            Method method = mWebViewCore.getClass().getDeclaredMethod("nativeRegisterURLSchemeAsLocal", new Class[]{int.class, String.class});
            method.setAccessible(true);
            method.invoke(mWebViewCore, mNativeClass, "http");
            method.invoke(mWebViewCore, mNativeClass, "https");
        } catch (Exception e) {
            Log.d("wokao", "enablecrossdomain error");
            e.printStackTrace();
        }
    }

}