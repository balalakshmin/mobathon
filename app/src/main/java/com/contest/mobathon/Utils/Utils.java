package com.contest.mobathon.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.contest.mobathon.activity.AdminActivity;

import java.lang.ref.WeakReference;


/**
 * Created by SONY on 6/1/2015.
 */
public class Utils {
    private static ProgressDialog mPgDialog;
    private static Handler mPgBarHandler = new Handler();

    public static void showProgressBar(final Context context, final String message)
    {
        final WeakReference<AdminActivity> ActivityWeakRef = new WeakReference<AdminActivity>((AdminActivity)context);
        try
        {
            mPgBarHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mPgDialog != null && ActivityWeakRef.get() != null && !ActivityWeakRef.get().isFinishing())

                    {
                        mPgDialog.dismiss();
                    }
                    mPgDialog = new ProgressDialog(context);
                    mPgDialog.setIndeterminate(true);
                    mPgDialog.setMessage(message);
                    mPgDialog.setCanceledOnTouchOutside(false);
                    mPgDialog.setCancelable(false);
                    mPgDialog.show();
                }
            });
        }
        catch (Exception e) {
            Log.e("UI Utils", "Error showing progress bar " + e.getMessage());
        }
    }

    public static void dismissProgressBar(Context context) {
        final WeakReference<AdminActivity> ActivityWeakRef = new WeakReference<AdminActivity>((AdminActivity) context);
        if (null != mPgDialog && ActivityWeakRef.get() != null && !ActivityWeakRef.get().isFinishing()) {
            mPgBarHandler.post(new Runnable() {
                @Override
                public void run()
                {
                    mPgDialog.dismiss();
                }
            });
        }
    }

    /** check if any type internet connection is available */
    public static boolean isNetworkAvailable(final Context pContext) {
        ConnectivityManager connMgr = (ConnectivityManager)pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
