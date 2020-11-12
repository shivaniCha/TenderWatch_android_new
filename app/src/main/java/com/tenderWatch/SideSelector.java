package com.tenderWatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by lcom48 on 1/12/17.
 */

public class SideSelector extends View {
    private static String TAG = SideSelector.class.getCanonicalName();
    public static char[] ALPHABET2;
    public static final int BOTTOM_PADDING = 10;

    private SectionIndexer selectionIndexer = null;
    private ListView list;
    private Paint paint;
    private String[] sections;

    public SideSelector(Context context) {
        super(context);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SideSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(0x44FFFFFF);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        int size = SideSelector.this.getResources().getDimensionPixelSize(R.dimen.text_10);
        paint.setTextSize(size);
        paint.setFakeBoldText(true);
        //   paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setListView(ListView _list) {
        list = _list;
        selectionIndexer = (SectionIndexer) _list.getAdapter();

        if (selectionIndexer != null && selectionIndexer.getSections() != null) {
            Object[] sectionsArr = selectionIndexer.getSections();

            if (sectionsArr != null && sectionsArr.length > 0) {
                sections = new String[sectionsArr.length];
                for (int i = 0; i < sectionsArr.length; i++) {
                    sections[i] = sectionsArr[i].toString();
                }
            }
        }
        requestLayout();
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int y = (int) event.getY();
        int selectedIndex = (int) (((float) y / (float) getPaddedHeight()) * ALPHABET2.length);

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (selectionIndexer == null) {
                selectionIndexer = (SectionIndexer) list.getAdapter();
            }
            int position = selectionIndexer.getPositionForSection((int) selectedIndex);
            if (position == -1) {
                return true;
            }

            list.setSelection(position);
        }
        return true;
    }

    public void setAlphabet(char[] chars) {
        ALPHABET2 = chars;
        init();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sections != null && sections.length > 0) {
            int viewHeight = getPaddedHeight();
            float charHeight = ((float) viewHeight) / (float) sections.length;

            float widthCenter = getMeasuredWidth() / 2;
            for (int i = 0; i < sections.length; i++) {
                canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight + (i * charHeight), paint);
            }
        }
    }

    private int getPaddedHeight() {
        return getHeight() - BOTTOM_PADDING;
    }
}

