# BrokenView
Glass-break effect for views.<br>

![brokenview](https://raw.githubusercontent.com/zhanyongsheng/raw/master/BrokenView/image/demo.gif)

# Sample APK

[Download](https://raw.githubusercontent.com/zhanyongsheng/raw/master/BrokenView/apk/demo.apk)

# Usage

Add Gradle dependency:

```gradle
dependencies {
   compile 'com.zys:brokenview:1.0.1'
}
```

An example of basic usage:

```Java
BrokenView brokenView = BrokenView.add2Window(context);
BrokenTouchListener listener = new BrokenTouchListener.Builder(brokenView).build();
view.setOnTouchListener(listener);
```

Use `BrokenTouchListener.Builder` for more configurations, and set custom attributes as shown below:
```Java
new BrokenTouchListener.Builder(brokenView).
    setComplexity(...).       // default 12 
    setBreakDuration(...).    // in milliseconds, default 700
    setFallDuration(...).     // in milliseconds, default 2000
    setPaint(...).            // the paint to draw rifts
    build();
```

You can also set a callback to listen the animation status, like this:
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

#Compatibility
Support API LEVEL >= 14

# License
`BrokenView` is available under the MIT license.
