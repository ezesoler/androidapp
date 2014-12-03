package com.rodrigo.lock.app.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.scrollActionbar.AlphaForegroundColorSpan;
import com.rodrigo.lock.app.presentation.UI.scrollActionbar.NotifyingScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "NoBoringActionBarActivity";
    private int mActionBarTitleColor;
    private int mActionBarHeight;
    private int mMinHeaderTranslation;

    @InjectView(R.id.scrollView) NotifyingScrollView mListView;
    @InjectView(R.id.header_picture) ImageView mHeaderPicture;
    @InjectView(R.id.header_logo) ImageView mHeaderLogo;
    @InjectView(R.id.toolbar) android.support.v7.widget.Toolbar toolbar;
    @InjectView(R.id.header) FrameLayout header;

    @InjectView(R.id.version) TextView version;
    @InjectView(R.id.r0) TextView r0;

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    private TypedValue mTypedValue = new TypedValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initToolbar();
        //setSupportActionBar(toolbar);

        int mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_bar);
        mMinHeaderTranslation = - mHeaderHeight + getActionBarHeight();

        if (isFirstTime()){
            tutorial();
        }

        //inicializa el titulo del action bar
        mActionBarTitleColor = getResources().getColor(R.color.white);
        mSpannableString = new SpannableString(getString(R.string.app_name));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
        setTitleAlpha(0F);

        mListView.setOnScrollChangedListener(mOnScrollChangedListener);
        String versionName="";
        try {
            versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText(versionName);

        //respuesta 0
        r0.setMovementMethod(LinkMovementMethod.getInstance());
        r0.setText(Html.fromHtml(getString(R.string.r0)));
    }

    @OnClick(R.id.tutorial)
    public void tutorial(){
        startActivity(new Intent(this, InstructionsActivity.class));
    }

    @OnClick(R.id.donar)
    public void donarl() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7R9PXAXWHZ8HU"));
        startActivity(browserIntent);
    }


    @OnClick(R.id.contacto)
    public void Contacto() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"lock.app.android@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "from android");
        i.putExtra(Intent.EXTRA_TEXT   , "");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            int headerHeight = findViewById(R.id.header).getHeight() - toolbar.getHeight();
            float ratio = (float) clamp(t, 0, headerHeight) / headerHeight;
            ratio=Math.min(ratio, 1.0F);

            int posicionActionBar = Math.max(-t, mMinHeaderTranslation);
            //sticky actionbar
            header.setTranslationY(posicionActionBar);


            float subeAparece = clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F);
            setTitleAlpha(subeAparece);
            float bajaAparece = clamp(1.0F - (5.0F * ratio - 4.0F), 0.0F, 1.0F);
            mHeaderPicture.setAlpha(bajaAparece);
            mHeaderLogo.setAlpha(bajaAparece);
        }
    };



    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       toolbar.setTitle(mSpannableString);

    }


    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }


public void initToolbar(){
    // Set an OnMenuItemClickListener to handle menu item clicks
    toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                startActivity (new Intent(MainActivity.this, ConfigActivity.class));
            }
            return true;
        }
    });

    // Inflate a menu to be displayed in the toolbar
    toolbar.inflateMenu(R.menu.main);
}

/*


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity (new Intent(this, ConfigActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    /***
     * Checks that application runs first time and write flag at SharedPreferences
     * @return true if 1st time
     */
    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

}
