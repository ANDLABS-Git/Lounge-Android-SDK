package andlabs.lounge.game;

import java.util.ArrayList;

import andlabs.lounge.LoungeGameController;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class PointsView extends View {

    private float mDp;
    private float mRadius;
    private Paint mMyPaint;
    private Paint mOthersPaint;

    private ArrayList<Point> mMyPoints = new ArrayList<Point>();
    private ArrayList<Point> mOthersPoints = new ArrayList<Point>();
    private LoungeGameController mController;
    private String mMatchId;

    public PointsView(Context pContext, String pMatchId, LoungeGameController pController) {
        super(pContext);
        mDp = pContext.getResources().getDisplayMetrics().density;
        mMyPaint = new Paint();
        mMyPaint.setColor(Color.GREEN);
        mOthersPaint = new Paint();
        mOthersPaint.setColor(Color.RED);
        mRadius = 10 * mDp;
        mController = pController;
        mMatchId = pMatchId;
    }


    @Override
    protected void onDraw(Canvas pCanvas) {

        for (Point myPoint : mMyPoints) {
            pCanvas.drawCircle(myPoint.x - mRadius / 2, myPoint.y - mRadius / 2, mRadius, mMyPaint);
        }

        for (Point othersPoint : mOthersPoints) {
            pCanvas.drawCircle(othersPoint.x - mRadius / 2, othersPoint.y - mRadius / 2, mRadius, mMyPaint);
        }

        super.onDraw(pCanvas);
    }

    public void addMyPoint(Point point) {
        mMyPoints.add(point);
        Bundle move = new Bundle();
        move.putString("x", point.x / mDp +"");
        move.putString("y", point.y / mDp +"");
        
        mController.sendGameMove(mMatchId, move);
        invalidate();

    }

    public void addOthersPoint(Point point) {
        mOthersPoints.add(point);
        invalidate();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent pEvent) {
        final Point point = new Point();
        point.set((int) pEvent.getX(), (int) pEvent.getY());
        addMyPoint(point);
        return true;
    }
}
