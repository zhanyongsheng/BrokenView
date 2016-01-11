package com.zys.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.zys.brokenview.BrokenCallback;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private BrokenView mBrokenView;

    private RelativeLayout parentLayout;
    private ImageView imageView;
    private ListView listView;
    private MyView myView;
    private Button button;
    private boolean hasAlpha;
    private int resids[];

    private SeekBar complexitySeekbar;
    private SeekBar breakSeekbar;
    private SeekBar fallSeekbar;
    private SeekBar radiusSeekbar;
    private Toolbar toolbar;

    private BrokenTouchListener colorfulListener;
    private BrokenTouchListener whiteListener;
    private Paint whitePaint;
    private boolean effectEnable = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mBrokenView = BrokenView.add2Window(this);

        whitePaint = new Paint();
        whitePaint.setColor(0xffffffff);

        colorfulListener = new BrokenTouchListener.Builder(mBrokenView).
                build();
        whiteListener = new BrokenTouchListener.Builder(mBrokenView).
                setPaint(whitePaint).
                build();

        setOnTouchListener();
    }

    private void initView(){
        parentLayout = (RelativeLayout) findViewById(R.id.demo_parent);
        imageView = (ImageView) findViewById(R.id.demo_image);
        listView = (ListView) findViewById(R.id.demo_list);
        myView = (MyView) findViewById(R.id.demo_myview);
        button = (Button) findViewById(R.id.demo_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Button onClick",Toast.LENGTH_SHORT).show();
            }
        });

        TypedArray ar = getResources().obtainTypedArray(R.array.imgArray);
        int len = ar.length();
        resids = new int[len];
        for (int i = 0; i < len; i++)
            resids[i] = ar.getResourceId(i, 0);

        initSeekBar();
        initToggleButton();
        initToolbar();
        initDrawerLayout();

        refreshDate();
    }
    private void initSeekBar(){
        complexitySeekbar = (SeekBar) findViewById(R.id.seekbar_complexity);
        breakSeekbar = (SeekBar) findViewById(R.id.seekbar_break);
        fallSeekbar = (SeekBar) findViewById(R.id.seekbar_fall);
        radiusSeekbar = (SeekBar) findViewById(R.id.seekbar_radius);
        final TextView complexityTv = (TextView) findViewById(R.id.complexity_value);
        final TextView breakTv = (TextView) findViewById(R.id.break_value);
        final TextView fallTv = (TextView) findViewById(R.id.fall_value);
        final TextView radiusTv = (TextView) findViewById(R.id.radius_value);
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case R.id.seekbar_complexity:
                        complexityTv.setText("" + (progress + 8));
                        break;
                    case R.id.seekbar_break:
                        breakTv.setText((progress + 500) + "ms");
                        break;
                    case R.id.seekbar_fall:
                        fallTv.setText((progress + 1000) + "ms");
                        break;
                    case R.id.seekbar_radius:
                        radiusTv.setText((progress + 20) + "dp");
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        complexitySeekbar.setOnSeekBarChangeListener(listener);
        breakSeekbar.setOnSeekBarChangeListener(listener);
        fallSeekbar.setOnSeekBarChangeListener(listener);
        radiusSeekbar.setOnSeekBarChangeListener(listener);
    }

    private void initToggleButton(){
        ToggleButton effectBtn = (ToggleButton) findViewById(R.id.toggle_effect);
        ToggleButton callbackBtn = (ToggleButton) findViewById(R.id.toggle_callback);
        final BrokenCallback callback = new MyCallBack();
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isOpen) {
                switch (buttonView.getId()) {
                    case R.id.toggle_effect:
                        effectEnable = isOpen;
                        break;
                    case R.id.toggle_callback:
                        mBrokenView.setCallback(isOpen ? callback : null);
                        break;
                }
            }
        };
        effectBtn.setOnCheckedChangeListener(listener);
        callbackBtn.setOnCheckedChangeListener(listener);
    }

    private void initToolbar(){
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        mBrokenView.reset();
                        refreshDate();
                        setOnTouchListener();
                        setViewVisible();
                        break;
                }
                return true;
            }
        });
    }

    private void initDrawerLayout(){

        DrawerLayout mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mBrokenView.setEnable(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                colorfulListener = new BrokenTouchListener.Builder(mBrokenView).
                        setComplexity(complexitySeekbar.getProgress() + 8).
                        setBreakDuration(breakSeekbar.getProgress() + 500).
                        setFallDuration(fallSeekbar.getProgress() + 1000).
                        setCircleRiftsRadius(radiusSeekbar.getProgress() + 20).
                        build();
                whiteListener = new BrokenTouchListener.Builder(mBrokenView).
                        setComplexity(complexitySeekbar.getProgress() + 8).
                        setBreakDuration(breakSeekbar.getProgress() + 500).
                        setFallDuration(fallSeekbar.getProgress() + 1000).
                        setCircleRiftsRadius(radiusSeekbar.getProgress() + 20).
                        setPaint(whitePaint).
                        build();

                setOnTouchListener();

                mBrokenView.setEnable(effectEnable);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setViewVisible(){
        parentLayout.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        myView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    public void refreshDate(){
        Random rand = new Random();

        List<ListItem> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int color = Color.argb(0xff, rand.nextInt(0x99), rand.nextInt(0x99), rand.nextInt(0x99));
            ListItem item = new ListItem(color, "list item " + i);
            items.add(item);
        }
        listView.setAdapter(new SampleAdapter(MainActivity.this, android.R.layout.simple_list_item_1, items));

        int pos = rand.nextInt(resids.length);
        imageView.setImageResource(resids[pos]);
        if(pos == 0 || pos == 1 || pos == 2)
            hasAlpha = true;
        else
            hasAlpha = false;
    }

    public void setOnTouchListener(){
    /*
        if you don't want the childView of parentLayout intercept touch event
        set like this:
        childView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        childView.setClickable(false);

        if you want only click the button can break the parentLayout,
        set like this:
        listener = new BrokenTouchListener.Builder(mBrokenView).
                setEnableArea(button).
                build();
        and set the button don't intercept touch event at the same time
    */
        parentLayout.setOnTouchListener(colorfulListener);
        button.setOnTouchListener(colorfulListener);
        myView.setOnTouchListener(whiteListener);
        listView.setOnTouchListener(colorfulListener);
        if(hasAlpha)
            imageView.setOnTouchListener(whiteListener);
        else
            imageView.setOnTouchListener(colorfulListener);
    }

    private class MyCallBack extends BrokenCallback{
        @Override
        public void onStart(View v) {
            showCallback(v,"onStart");
        }

        @Override
        public void onCancel(View v) {
            showCallback(v,"onCancel");
        }

        @Override
        public void onRestart(View v) {
            showCallback(v,"onRestart");
        }

        @Override
        public void onFalling(View v) {
            showCallback(v,"onFalling");
        }

        @Override
        public void onFallingEnd(View v) {
            showCallback(v,"onFallingEnd");
        }

        @Override
        public void onCancelEnd(View v) {
            showCallback(v,"onCancelEnd");
        }
    }

    public void showCallback(View v,String s){
        switch (v.getId()) {
            case R.id.demo_parent:
                Snackbar.make(parentLayout, "RelativeLayout---" + s, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.demo_image:
                Snackbar.make(parentLayout, "ImageView---" + s, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.demo_list:
                Snackbar.make(parentLayout, "ListView---" + s, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.demo_myview:
                Snackbar.make(parentLayout, "CustomView---" + s, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.demo_button:
                Snackbar.make(parentLayout, "Button---" + s, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private class SampleAdapter extends ArrayAdapter<ListItem> {
        private final LayoutInflater mInflater;
        private int mResource;
        private List<ListItem> items;
        public SampleAdapter(Context context, int layoutResourceId, List<ListItem> data) {
            super(context, layoutResourceId, data);
            mInflater = LayoutInflater.from(context);
            mResource = layoutResourceId;
            items = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, parent, false);
            }
            convertView.setBackgroundColor(items.get(position).color);
            ((TextView)convertView).setText(items.get(position).text);
            ((TextView)convertView).setTextColor(0xffffffff);

            return convertView;
        }
    }
    private class ListItem{
        int color;
        String text;
        public ListItem(int color,String text){
            this.color = color;
            this.text = text;
        }
    }
}
