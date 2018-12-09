package com.sortagreg.graphinglibrary.views;

import android.content.Context;
import android.graphics.Canvas;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class GraphViewDualVariableTest {

    private Canvas canvas;
    private Context context;
    private GraphViewDualVariable subject;

    @Before
    public void setUp() throws Exception {
        canvas = mock(Canvas.class);
        context = mock(Context.class);
        subject = new GraphViewDualVariable(context);
    }

    @Test
    public void setNumberOfVerticalMarkers() {
    }

    @Test
    public void setNumberOfHorizontalMarkers() {
    }

    @Test
    public void setNumberOfVerticalLabels() {
    }

    @Test
    public void setNumberOfHorizontalLabels() {
    }

    @Test
    public void setShouldDrawBox() {
    }

    @Test
    public void setTopAxisMargin() {
    }

    @Test
    public void setBottomAxisMargin() {
    }

    @Test
    public void setLeftAxisMargin() {
    }

    @Test
    public void setRightAxisMargin() {
    }

    @Test
    public void addToDataSetList() {
    }

    @Test
    public void addToDataSetListBulk() {
    }
}