# GraphView (Beta)
A custom graph View Object for Android.

Still in development.

## Description
GraphView is a custom graphing library for Android.  GraphView can handle multiple styles of line graph, including data sets that are not organized in ascending order.  GraphView uses the basic Android Canvas methods to draw the graph and all of its associated pieces.

GraphView uses a custom data class to wrap a simple PointF[] that represents the data set to drawn, a Paint Object to tell GraphView how to style your data set when it is drawn, and an Integer flag, used to represent the style of graph you wish your data set to be drawn in.

## Graph Styles
GraphView currently supports four different styles of graphs:
1) Standard X, Y graph
2) Incremental graph
3) Constant lines
4) Binary state graphs

## Label Styles

## Usage
### Perquisites
In your project level gradle file, add the following:
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
and in your app level gradle file add the following:
```
implementation 'com.github.sortagreg:GraphView:0.4'
```
### Example
XML:
```xml
<com.sortagreg.graphview.GraphView
        android:id="@+id/graphView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/background_light"
        app:title="Graph Title"/>
```
FragmentGraph:
```java
// Using ButterKnife to bind the GraphView
@BindView(R.id.graphView)
GraphView graphView;

int DATA_SET_LENGTH = 50;

// Instantiate a PointF[] to hold a data set
PointF[] exponentialCurve = new PointF[DATA_SET_LENGTH];

// Populate the data set
for (int i = 0; i <= DATA_SET_LENGTH - 1; i++) {
    float x = i - 25;
    PointF point = new PointF(x, x * x * x);
    exponentialCurve[i] = point;
}

// Create a Paint object to style the line for the data set
Paint paint = new Paint();
paint.setStrokeWidth(5f);
paint.setColor(0xFFFF3355);

// GraphViewDataModel is a custom data wrapper class included in this library
GraphViewDataModel dataModel = new GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);

// Draw the data set
graphView.addToDataSetList(dataModel);
```

![Basic Demo Screenshot](https://github.com/sortagreg/GraphView/blob/Documentation/graphview/images/BasicDemoScreenshot.png)

## License
```
Copyright 2018 Marshall Lad

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
```
