package jfmacy.campuspaths;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import android.support.v7.widget.AppCompatImageView;

import hw7.Path;

/**Houses the map image and controls all the aspects of how the map is displayed
 *
 * Created by John Forrest Macy on 8/13/2017.
 */

public class DrawView extends AppCompatImageView {

    // this is not an ADT

    private Point sourceSpot = null;
    private Point destSpot = null;
    private float[] pathPoints = null;
    private float scaleFactor = 1;
    private int xShift = 0;
    private int yShift = 0;


    public DrawView (Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // paints for drawn elements
        Paint sourcePaint = new Paint();
        sourcePaint.setColor(Color.GREEN);

        Paint destPaint = new Paint();
        destPaint.setColor(Color.RED);

        Paint pathPaint = new Paint();
        pathPaint.setColor(Color.YELLOW);
        pathPaint.setStrokeWidth(10/scaleFactor);

        // draw path first so dots cover on ends
        if (pathPoints != null) {
            canvas.drawLines(pathPoints, pathPaint);
        }

        // draw source building dot
        if (sourceSpot != null) {
            canvas.drawCircle(sourceSpot.x, sourceSpot.y, 20/scaleFactor, sourcePaint);
        }

        // draw dest building dot
        if (destSpot != null) {
            canvas.drawCircle(destSpot.x, destSpot.y, 20/scaleFactor, destPaint);
        }

        // update position and zoom of map
        setScaleX(scaleFactor);
        setScaleY(scaleFactor);
        setScrollX(xShift);
        setScrollY(yShift);
    }

    /**
     * sets position of source building indicator
     * @modifies this
     * @effects location of source spot is updated
     * @param x horizontal location in pixels from left of image view
     * @param y vertical location in pixels from top of image view
     */
    public void markSource(int x, int y) {
        sourceSpot = new Point();
        sourceSpot.set(x, y);
        this.invalidate();
    }

    /**
     * sets position of dest building indicator
     * @modifies this
     * @effects location of dest spot is updated
     * @param x horizontal location in pixels from left of image view
     * @param y vertical location in pixels from top of image view
     */
    public void markDest(int x, int y) {
        destSpot = new Point();
        destSpot.set(x, y);
        this.invalidate();
    }

    /**
     * sets the list of points along path
     * @param points array must contains pixel coordinates of each line segment in the form:<br>
     *               x0, y0, x1, y1, x1, y1, x2, y2 ...
     */
    public void drawPath(float[] points) {
        pathPoints = points;
        this.invalidate();
    }

    /**set the scale factor of the map.  Based on original size.
     *
     * @param scale factor to scale by
     */
    public void setScaleFactor(float scale){
        scaleFactor = scale;
    }

    /**
     * set the amount to shift the image from default
     * @param x amount to shift right
     * @param y amount to shift up
     */
    public void setShift(int x, int y){
        xShift = x;
        yShift = y;
    }
}
