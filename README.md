# BrokenView
Glass-break effect for views.<br>

![brokenview](https://raw.githubusercontent.com/zhanyongsheng/raw/master/BrokenView/image/demo.gif)

# Demo

[Download APK](https://raw.githubusercontent.com/zhanyongsheng/raw/master/BrokenView/apk/demo.apk)

# Usage
### Android Studio
```gradle
dependencies {
   compile 'com.zys:brokenview:1.0.3'
}
```
### Eclipse
Just put [brokenview.jar](https://raw.githubusercontent.com/zhanyongsheng/raw/master/BrokenView/jar/brokenview.jar) into the libs folder of your app.

### Basic Usage

```Java
BrokenView brokenView = BrokenView.add2Window(context);
BrokenTouchListener listener = new BrokenTouchListener.Builder(brokenView).build();
view.setOnTouchListener(listener);
```

# More Config

Use `BrokenTouchListener.Builder` for more configurations, and set custom attributes as shown below:
```Java
BrokenTouchListener.Builder(brokenView).
    setComplexity(...).          // default 12 
    setBreakDuration(...).       // in milliseconds, default 700ms
    setFallDuration(...).        // in milliseconds, default 2000ms
    setCircleRiftsRadius(...).   // in dp, default 66dp, you can disable circle-rifts effect by set it to 0
    setEnableArea(...).          // set the region or childview that can enable break effect,
                                 // be sure the childView or childView in region doesn't intercept any touch event
    setPaint(...).               // the paint to draw rifts
    build();
```

You can also set a callback to listen to the status of animations, like this:
```Java
brokenView.setCallback.(new BrokenCallback() {
   @Override
   public void onStart(View v) {...}
   @Override
   public void onCancel(View v) {...}
   @Override
   public void onCancelEnd(View v) {...}
   @Override
   public void onRestart(View v) {...}
   @Override
   public void onFalling(View v) {...}
   @Override
   public void onFallingEnd(View v) {...}
});
```

# Compatibility
* Support API LEVEL >= 14

# Changelog
### Version 1.0.0
* First release

### Version 1.0.1
* Optimize animation effects 
* Set minSdkVersion to 14

### Version 1.0.2
* Add many annotations 
* Add new features: setCircleRiftsRadius, setEnableArea

### Version 1.0.3
* Fix setCircleRiftsRadius(0) cause to ANR
* Better annotations 

# License
`BrokenView` is available under the MIT license.
