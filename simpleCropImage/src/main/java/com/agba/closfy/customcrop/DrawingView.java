package com.agba.closfy.customcrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier.agudo on 18/08/2016.
 */
public class DrawingView extends View implements View.OnTouchListener {
    private Paint paint;
    public static List<Point> points;
    int DIST = 2;
    boolean flgPathDraw = true;

    Point mfirstpoint = null;
    boolean bfirstpoint = false;

    Point mlastpoint = null;

    public static Bitmap bitmap;
    Context mContext;

    public static float xInit = 100000;
    public static float yInit = 100000;

    public static float xFinal = 0;
    public static float yFinal = 0;

    private int width;
    private int height;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{30, 20}, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.YELLOW);

        this.setOnTouchListener(this);
        points = new ArrayList<Point>();

        bfirstpoint = false;
    }

    public void onDraw(Canvas canvas) {
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, 0, 0, null);

        Path path = new Path();
        boolean first = true;

        for (int i = 0; i < points.size(); i += 2) {
            Point point = points.get(i);
            if (first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else if (i < points.size() - 1) {
                Point next = points.get(i + 1);
                path.quadTo(point.x, point.y, next.x, next.y);
            } else {
                mlastpoint = points.get(i);
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {
        // if(event.getAction() != MotionEvent.ACTION_DOWN)
        // return super.onTouchEvent(event);

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        if (flgPathDraw) {

            if (bfirstpoint) {

                if (comparepoint(mfirstpoint, point)) {
                    // points.add(point);
                    points.add(mfirstpoint);
                    flgPathDraw = false;
                    //showcropdialog();
                } else {
                    points.add(point);
                }
            } else {
                points.add(point);
            }

            if (!(bfirstpoint)) {

                mfirstpoint = point;
                bfirstpoint = true;
            }
        }

        invalidate();
        Log.e("Hi  ==>", "Size: " + point.x + " " + point.y);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            resetView();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
           //Log.d("Action up*******~~~~~~~>>>>", "called");
            mlastpoint = point;
            if (flgPathDraw) {
                if (points.size() > 12) {
                    if (!comparepoint(mfirstpoint, mlastpoint)) {
                        flgPathDraw = false;
                        points.add(mfirstpoint);
                        //showcropdialog();
                    }
                }
            }

            xInit = 10000;
            yInit = 10000;
            xFinal = 0;
            yFinal = 0;
            for(int i=0; i< points.size();i++){
                if(points.get(i).x < xInit){
                    xInit = points.get(i).x;
                }

                if(points.get(i).x > xFinal){
                    xFinal = points.get(i).x;
                }

                if(points.get(i).y < yInit){
                    yInit = points.get(i).y;
                }

                if(points.get(i).y > yFinal){
                    yFinal = points.get(i).y;
                }
            }
        }

        return true;
    }

    private boolean comparepoint(Point first, Point current) {
        int left_range_x = (int) (current.x - 3);
        int left_range_y = (int) (current.y - 3);

        int right_range_x = (int) (current.x + 3);
        int right_range_y = (int) (current.y + 3);

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            if (points.size() < 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    public void fillinPartofPath() {
        Point point = new Point();
        point.x = points.get(0).x;
        point.y = points.get(0).y;

        points.add(point);
        invalidate();
    }

    public void resetView() {
        points.clear();
        bfirstpoint = false;
        flgPathDraw = true;
        mfirstpoint = null;
        mlastpoint = null;
        points = new ArrayList<Point>();
        invalidate();
    }

    public Bitmap recortarImagen (){
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, false);

        Bitmap resultingImage = Bitmap.createBitmap(width,
                height, bitmap2.getConfig());

        Canvas canvas = new Canvas(resultingImage);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.white));

        Path path = new Path();
        for (int i = 0; i < DrawingView.points.size(); i++) {
            path.lineTo(DrawingView.points.get(i).x, DrawingView.points.get(i).y);
        }

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap2, 0, 0, paint);


        int xInit = (int) DrawingView.xInit;
        int yInit = (int) DrawingView.yInit;

        int xFinal = (int) DrawingView.xFinal;
        int yFinal = (int) DrawingView.yFinal;

        int width = (xFinal - xInit);
        int height = (yFinal - yInit);

        Bitmap bitmapAux = Bitmap.createBitmap(resultingImage, xInit, yInit, width, height);
        bitmapAux = Bitmap.createScaledBitmap(bitmapAux, width, height, false);
        return bitmapAux;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
