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
```
<com.sortagreg.graphview.GraphView
        android:id="@+id/graphView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/background_light"
        app:title="Graph Title"/>
```
FragmentGraph:
```
List<GraphViewDataModel> dataSetList = new ArrayList<>();
        int DATA_SET_LENGTH = 50;
        Paint paint;

        PointF[] exponentialCurve = new PointF[DATA_SET_LENGTH];
        for (int i = 0; i <= DATA_SET_LENGTH - 1; i++) {
            float x = i - 25;
            PointF point = new PointF(x, x * x * x);
            exponentialCurve[i] = point;
        }
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFFFF3355);
        GraphViewDataModel expCurve = new GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(expCurve);
```
