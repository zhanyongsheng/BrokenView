# BrokenView
Glass-break effect for views.<br>

![brokenview](https://github.com/zhanyongsheng/raw/blob/master/BrokenView/image/demo.gif)

# Usage

Add Gradle dependency:

```gradle
dependencies {
   compile 'com.zys:brokenview:1.0.0'
}
```

An example of basic usage:

```Java
BrokenView brokenView = BrokenView.add2Window(context);
BrokenTouchListener listener = new BrokenTouchListener.Builder(brokenView).build();
view.setOnTouchListener(listener);
```

Or you can use `BrokenTouchListener.Builder` for more configurations, and set custom attributes as shown below:
```Java
new BrokenTouchListener.Builder(brokenView).
    setComplexity(...).       // default 12 
    setBreakDuration(...).    // in milliseconds, default 700
    setFallDuration(...).     // in milliseconds, default 2000
    setPaint(...).            // the paint to draw rifts
    build();
```

You can also set a callback if you want to do something during the anmiation, like this:
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

# License
`BrokenView` is available under the MIT license.
