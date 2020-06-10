package cn.luern0313.removeindiaapps;

import androidx.appcompat.app.AppCompatActivity;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import android.animation.ObjectAnimator;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    Handler handler = new Handler();
    Runnable runnableNextApp, runnableLoadingFinish;

    ArrayList<PackageInfo> appList;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        appList = new ArrayList<PackageInfo>();
        for (PackageInfo packageInfo : list)
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                appList.add(packageInfo);

        runnableNextApp = new Runnable()
        {
            @Override
            public void run()
            {
                PackageInfo packageInfo = appList.get(i);
                ((TextView) findViewById(R.id.main_app)).setText(String.format(getResources().getString(R.string.main_app),
                                      packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(), packageInfo.packageName));
                i++;
                if(i < appList.size()) handler.postDelayed(this, 50);
                else handler.postDelayed(runnableLoadingFinish, 50);
            }
        };

        runnableLoadingFinish = new Runnable()
        {
            @Override
            public void run()
            {
                ((CircularProgressButton) findViewById(R.id.main_button)).revertAnimation();
                ObjectAnimator.ofFloat(findViewById(R.id.main_img_1), "scaleX", 1, 0).setDuration(800).start();
                ObjectAnimator.ofFloat(findViewById(R.id.main_img_1), "scaleY", 1, 0).setDuration(800).start();

                findViewById(R.id.main_img_2).setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(findViewById(R.id.main_img_2), "scaleX", 0, 0, 1).setDuration(1200).start();
                ObjectAnimator.ofFloat(findViewById(R.id.main_img_2), "scaleY", 0, 0, 1).setDuration(1200).start();
                ((TextView) findViewById(R.id.main_title)).setText("  恭喜！");
                ((TextView) findViewById(R.id.main_app)).setTextSize(18);
                ((TextView) findViewById(R.id.main_app)).setText("在您的手机上未发现任何印度应用\n(我也不知道印度应用有什么)");
            }
        };

        ((TextView) findViewById(R.id.main_title)).setText(Html.fromHtml("卸载<b>印度应用</b>"));

        findViewById(R.id.main_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                i = 0;
                ((TextView) findViewById(R.id.main_title)).setText(Html.fromHtml("卸载<b>印度应用</b>"));
                findViewById(R.id.main_img_2).setVisibility(View.GONE);
                ObjectAnimator.ofFloat(findViewById(R.id.main_img_1), "scaleX", 1, 1).setDuration(1).start();
                ObjectAnimator.ofFloat(findViewById(R.id.main_img_1), "scaleY", 1, 1).setDuration(1).start();
                ((CircularProgressButton) v).startAnimation();
                ((TextView) findViewById(R.id.main_app)).setTextSize(12);
                handler.post(runnableNextApp);
            }
        });
    }
}
