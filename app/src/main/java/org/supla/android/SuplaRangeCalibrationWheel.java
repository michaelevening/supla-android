/*
 Copyright (C) AC SOFTWARE SP. Z O.O.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.supla.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class SuplaRangeCalibrationWheel extends View {

    private final int TOUCHED_NONE = 0;
    private final int TOUCHED_LEFT = 1;
    private final int TOUCHED_RIGHT = 2;

    private Paint paint;
    private RectF rectF;
    private float borderLineWidth;
    private float wheelCenterX;
    private float wheelCenterY;
    private float wheelRadius;
    private float wheelWidth;
    private int touched = TOUCHED_NONE;
    private PointF btnLeftCenter;
    private PointF btnRightCenter;
    private float halfBtnSize;
    private double btnRad;
    private double lastTouchedDegree;

    private int colorWheel = Color.parseColor("#c6d6ef");
    private int colorBorder = Color.parseColor("#4585e8");
    private int colorBtn = Color.parseColor("#4585e8");
    private int colorValue = Color.parseColor("#fee618");
    private int colorInsideBtn = Color.WHITE;

    private double maxRange = 1000;
    private double minRange = maxRange * 0.1;
    private double numerOfTurns = 5;
    private double minimum = 0;
    private double maximum = maxRange;
    private double leftEdge = 0;
    private double rightEdge = maxRange;
    private double driveLevel = 0;
    private boolean driveVisible = false;

    private OnChangeListener onChangeListener = null;


    public interface OnChangeListener {
        void onRangeChanged(SuplaRangeCalibrationWheel calibrationWheel, boolean minimum);
        void onDriveChanged(SuplaRangeCalibrationWheel calibrationWheel);
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        rectF = new RectF();
        btnLeftCenter = null;
        btnRightCenter = null;
        btnRad = 0;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        borderLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1.5F, metrics);
    }

    public SuplaRangeCalibrationWheel(Context context) {
        super(context);
        init();
    }

    public SuplaRangeCalibrationWheel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuplaRangeCalibrationWheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SuplaRangeCalibrationWheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setMaxRange(double maxRange) {
        if (leftEdge > maxRange) {
            maxRange = leftEdge;
        }

        if (rightEdge > maxRange) {
            maxRange = rightEdge;
        }
        this.maxRange = maxRange;
        invalidate();
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMinRange(double minRange) {
        if (minRange < 0) {
            minRange = 0;
        }
        if (minRange > maxRange) {
            minRange = maxRange;
        }
        this.minRange = minRange;
        invalidate();
    }

    public double getMinRange() {
        return this.minRange;
    }

    public void setNumerOfTurns(double numerOfTurns) {
        this.numerOfTurns = numerOfTurns;
        invalidate();
    }

    public double getNumerOfTurns() {
        return numerOfTurns;
    }

    private void setMinimum(double minimum, boolean inv) {
        if (minimum+minRange > maximum) {
            minimum = maximum - minRange;
        }

        if (minimum < leftEdge) {
            minimum = leftEdge;
        }

        if (minimum > driveLevel) {
            driveLevel = minimum;
        }

        this.minimum = minimum;
        if (inv) {
            invalidate();
        }
    }

    public void setMinimum(double minimum) {
        setMinimum(minimum, true);
    }

    public double getMinimum() {
        return minimum;
    }

    private void setMaximum(double maximum, boolean inv) {
        if (minimum+minRange > maximum) {
            maximum = minimum+minRange;
        }

        if (maximum > rightEdge) {
            maximum = rightEdge;
        }

        if (maximum < driveLevel) {
            driveLevel = maximum;
        }

        this.maximum = maximum;
        if (inv) {
            invalidate();
        }
    }

    public void setMaximum(double maximum) {
        setMaximum(maximum, true);
    }

    public double getMaximum() {
        return maximum;
    }

    public void setRightEdge(double rightEdge) {
        if (rightEdge < 0) {
            rightEdge = 0;
        }

        if (leftEdge > rightEdge) {
            rightEdge = leftEdge;
        }

        if (rightEdge > maxRange) {
            rightEdge = maxRange;
        }

        this.rightEdge = rightEdge;
        setMinimum(getMinimum());
        setMaximum(getMaximum());
    }

    public double getRightEdge() {
        return rightEdge;
    }

    public void setLeftEdge(double leftEdge) {
        if (leftEdge < 0) {
            leftEdge = 0;
        }

        if (leftEdge > rightEdge) {
            leftEdge = rightEdge;
        }

        if (leftEdge > maxRange) {
            leftEdge = maxRange;
        }

        this.leftEdge = leftEdge;
        setMinimum(getMinimum());
        setMaximum(getMaximum());
    }

    public void setDriveLevel(double driveLevel) {
        if (driveLevel < minimum) {
            driveLevel = minimum;
        }

        if (driveLevel > maximum) {
            driveLevel = maximum;
        }

        this.driveLevel = driveLevel;
        if (driveVisible) {
            invalidate();
        }
    }

    public double getDriveLevel() {
        return driveLevel;
    }

    public void setDriveVisible(boolean driveVisible) {
        this.driveVisible = driveVisible;
        invalidate();
    }

    public boolean isDriveVisible() {
        return driveVisible;
    }

    private void drawBtnLines(Canvas canvas, RectF rectF) {
        final int lc = 3;

        float hMargin = rectF.height() * 0.35F;
        float wMargin = rectF.width() * 0.2F;

        rectF = new RectF(rectF);
        rectF.left+=wMargin;
        rectF.right-=wMargin;
        rectF.top+=hMargin;
        rectF.bottom-=hMargin;

        float step = rectF.height() / (lc-1);
        float width = borderLineWidth*1F;

        for(int a=0;a<lc;a++) {
            RectF lineRectF = new RectF();
            lineRectF.set(rectF.left, rectF.top+step*a-width/2,
                    rectF.right, rectF.top+step*a+width/2);

            canvas.drawRoundRect(
                    lineRectF,
                    15,
                    15,
                    paint
            );
        }
    }

    private PointF drawButton(Canvas canvas, double rad, boolean visible) {

        float btnSize = wheelWidth+4*borderLineWidth;
        halfBtnSize = btnSize/2;
        float x = wheelCenterX+wheelRadius - borderLineWidth;

        PointF result = new PointF();
        result.x = (float)(Math.cos(rad)*wheelRadius)+wheelCenterX;
        result.y = (float)(Math.sin(rad)*wheelRadius)+wheelCenterY;

        if (!visible) {
            return result;
        }

        canvas.save();
        canvas.rotate((float)Math.toDegrees(rad), wheelCenterX, wheelCenterY);

        paint.setColor(colorBtn);
        paint.setStyle(Paint.Style.FILL);
        rectF.set(x-halfBtnSize, wheelCenterY-halfBtnSize,
                x+halfBtnSize, wheelCenterY+halfBtnSize);

        canvas.drawRoundRect(
                rectF,
                15,
                15,
                paint
        );

        paint.setColor(colorInsideBtn);

        drawBtnLines(canvas, rectF);

        canvas.restore();

        return result;
    }

    private PointF drawButton(Canvas canvas, double rad) {
        return drawButton(canvas, rad, true);
    }

    private void drawValue(Canvas canvas) {

        float distance = halfBtnSize + borderLineWidth * 2;
        float left = btnLeftCenter.x+distance;
        float top = btnLeftCenter.y-halfBtnSize;
        float right = btnRightCenter.x-distance;
        float bottom = btnRightCenter.y+halfBtnSize;

        float vleft = left + (float)((right-left) * minimum *100F/maxRange/100F);
        float vright = left + (float)((right-left) * maximum *100F/maxRange/100F);

        paint.setColor(colorValue);
        paint.setStyle(Paint.Style.FILL);
        rectF.set(vleft, top, vright, bottom);

        canvas.drawRoundRect(
                rectF,
                15,
                15,
                paint);

        paint.setColor(colorBorder);
        paint.setStrokeWidth(borderLineWidth);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set(left, top, right, bottom);

        canvas.drawRoundRect(
                rectF,
                15,
                15,
                paint
        );

        if (driveVisible) {
            vleft = left + (float)((right-left) * driveLevel *100F/maxRange/100F);
            if (driveLevel >= (maximum-minimum) / 2) {
                vleft-=borderLineWidth;
            } else {
                vleft+=borderLineWidth;
            }
            paint.setColor(Color.RED);
            canvas.drawLine(vleft, top-borderLineWidth,
                    vleft, bottom+borderLineWidth, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        wheelCenterX = this.getWidth() / 2;
        wheelCenterY = this.getHeight() / 2;

        wheelRadius = wheelCenterX > wheelCenterY ? wheelCenterY : wheelCenterX;
        wheelRadius *= 0.7;
        wheelWidth = wheelRadius * 0.25F;

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(colorBorder);
        paint.setStrokeWidth(wheelWidth);
        rectF.set(wheelCenterX-wheelRadius, wheelCenterY-wheelRadius,
                wheelCenterX+wheelRadius, wheelCenterY+wheelRadius);
        canvas.drawOval(rectF, paint);

        paint.setStrokeWidth(wheelWidth-borderLineWidth*2);
        paint.setColor(colorWheel);
        canvas.drawOval(rectF, paint);

        if (touched == TOUCHED_NONE) {
            btnRightCenter = drawButton(canvas, 0);
            btnLeftCenter = drawButton(canvas, (float)Math.toRadians(180), !driveVisible);
        } else {
            if (touched == TOUCHED_RIGHT) {
                drawButton(canvas, btnRad);
            } else if (touched == TOUCHED_LEFT) {
                drawButton(canvas, btnRad, !driveVisible);
            }
        }

        drawValue(canvas);

    }

    private boolean btnTouched(PointF btnCenter, PointF touchPoint) {
        if (btnCenter != null) {
            float touchRadius = (float)Math.sqrt(Math.pow(touchPoint.x - btnCenter.x, 2)
                    + Math.pow(touchPoint.y - btnCenter.y, 2));
            return touchRadius <= halfBtnSize*1.1;
        }
        return false;
    }

    private double touchPointToRadian(PointF touchPoint) {
        return Math.atan2(touchPoint.y-wheelCenterY,
                touchPoint.x-wheelCenterX);
    }

    private void onRangeChanged(boolean  minimum) {
        if (onChangeListener!=null) {
            onChangeListener.onRangeChanged(this, minimum);
        }
    }

    private void onDriveChanged() {
        if (onChangeListener!=null) {
            onChangeListener.onDriveChanged(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        PointF touchPoint = new PointF();

        touchPoint.x = event.getX();
        touchPoint.y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                touched = TOUCHED_NONE;
                btnRad = 0;
                invalidate();
                break;

            case MotionEvent.ACTION_DOWN:
                if (touched==TOUCHED_NONE) {
                    if (!driveVisible && btnTouched(btnLeftCenter, touchPoint)) {
                        touched = TOUCHED_LEFT;
                        onRangeChanged(true);
                    } else if (btnTouched(btnRightCenter, touchPoint)) {
                        touched = TOUCHED_RIGHT;
                        if (driveVisible) {
                            onDriveChanged();
                        } else {
                            onRangeChanged(false);
                        }
                    }

                    if (touched!=TOUCHED_NONE) {
                        lastTouchedDegree = Math.toDegrees(touchPointToRadian(touchPoint));
                        invalidate();
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touched!=TOUCHED_NONE) {
                    btnRad = (float) Math.atan2(touchPoint.y-wheelCenterY,
                            touchPoint.x-wheelCenterX);

                    btnRad = touchPointToRadian(touchPoint);
                    double touchedDegree = Math.toDegrees(btnRad);

                    double diff = touchedDegree-lastTouchedDegree;
                    if (Math.abs(diff) > 100) {
                        diff = 360 - Math.abs(lastTouchedDegree) - Math.abs(touchedDegree);
                        if (touchedDegree > 0) {
                            diff*=-1;
                        }
                    }

                    if (Math.abs(diff) <= 20) {
                        diff = (diff*100.0/360.0)*maxRange/100/numerOfTurns;
                        if (touched==TOUCHED_LEFT) {
                            setMinimum(getMinimum()-diff, false);
                            onRangeChanged(true);
                        } else {
                            if (driveVisible) {
                                setDriveLevel(getDriveLevel()+diff);
                                onDriveChanged();
                            } else {
                                setMaximum(getMaximum()+diff, false);
                                onRangeChanged(false);
                            }
                        }
                    }

                    lastTouchedDegree = touchedDegree;
                    invalidate();
                    return true;
                }
                break;
        }


        return super.onTouchEvent(event);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
}
