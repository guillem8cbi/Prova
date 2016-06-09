package net.iesmila.paint;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;


public class SurfacePersonalitzada extends SurfaceView
        implements SurfaceHolder.Callback {

    private FilSurface fil;
    private boolean mostraPunt = false;
    private Coord mInicial = null, mActual = null;
    //private ArrayList<Coord> mPunters = new ArrayList<Coord>();

    private Paint mPaint;
    private Bitmap mBuffer;
    private Canvas mCanvasBuffer;

    public  SurfacePersonalitzada(Context context, AttributeSet attributes) {
        super(context, attributes);
        // Registrem la mateixa classe com el SurfaceHolder.Callback
        getHolder().addCallback(this);

        mPaint = new Paint();
        mPaint.setARGB(255, 200, 200, 45);
        mPaint.setStrokeWidth(3);



    }

    public SurfacePersonalitzada(Context context) {

        this(context, null);
    }



    @Override
    public void onDraw(Canvas canvas) {

        if(canvas==null) {
            return;
        }
        canvas.drawColor(Color.BLACK);
        //canvas.drawBitmap(mBuffer,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        Rect origen = new Rect(0,0,        mBuffer.getWidth(),
                                           mBuffer.getHeight());
        Rect desti =  new Rect(0,0,
                                    (int)( mBuffer.getWidth() * mFactorAmpliacioBase * mFactorAmpliacioPerZoom),
                                    (int)( mBuffer.getHeight()* mFactorAmpliacioBase * mFactorAmpliacioPerZoom));
        canvas.drawBitmap(mBuffer, origen, desti,new Paint(Paint.FILTER_BITMAP_FLAG) );
        if(mostraPunt && !mModeZoom) {
            canvas.drawLine(
                    mInicial.getX(), mInicial.getY(),
                    mActual.getX(), mActual.getY(), mPaint);
        }
    }

    int mNumPuntersAnteriors = 0;
    double mDistanciaInicial =0;
    boolean mModeZoom = false;
    double mFactorAmpliacioPerZoom = 1.0;
    double mFactorAmpliacioBase = 1.0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int eventaction = event.getAction();
        if(event.getPointerCount()>=2) {
            mModeZoom = true;
            if (mNumPuntersAnteriors < 2) {
                mDistanciaInicial = distancia(event);
            } else {
                if(mDistanciaInicial!=0) {
                    mFactorAmpliacioPerZoom = distancia(event) / mDistanciaInicial;
                } else {
                    mFactorAmpliacioPerZoom = 1.0;
                }
            }
        } else { // Hi ha menys de dos punters
            if(mNumPuntersAnteriors >= 2) {
                mFactorAmpliacioBase = mFactorAmpliacioBase * mFactorAmpliacioPerZoom;
                mFactorAmpliacioPerZoom = 1;
                return true;
            } else {
                mModeZoom = false;
            }
        }
        mNumPuntersAnteriors = event.getPointerCount();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                mInicial = new Coord( (int)event.getX(), (int)event.getY());
            case MotionEvent.ACTION_MOVE:
                mActual = new Coord( (int)event.getX(), (int)event.getY());
                mostraPunt = true;
                break;

            case MotionEvent.ACTION_UP:
                mostraPunt = false;
                mCanvasBuffer.drawLine(
                        mInicial.getX(), mInicial.getY(),
                        mActual.getX(), mActual.getY(), mPaint);

                break;
        }

//        cx = event.getX();
//        cy = event.getY();
        return true;
    }

    private double distancia(MotionEvent event) {
        return Math.sqrt(// Powered by Pitagoras
                (event.getX(0)-event.getX(1))*
                (event.getX(0)-event.getX(1))+
                (event.getY(0)-event.getY(1))*
                (event.getY(0)-event.getY(1)));
    }


    /*
        @Override
        public void onDraw(Canvas canvas) {

            if(canvas==null) {
                return;
            }
            if(mostraPunt) {
                for(int i=0;i<mPunters.size();i++) {
                    Coord c = mPunters.get(i);
                    mCanvasBuffer.drawCircle(c.getX(), c.getY(), 80, mPaint);
                }
            }
            canvas.drawBitmap(mBuffer,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            int numeroPunters = event.getPointerCount();
            int pointerId;
            mPunters.clear();//<------------------------------------!!!
            for(int i=0;i<numeroPunters;i++) {
                //pointerId = event.getPointerId(i);
                //int pointerIndex = event.findPointerIndex(pointerId);
                //Log.d("XXX", "Multitouch:"+pointerIndex+":"+event.getAction());
                float x = event.getX(i);
                float y = event.getY(i);
                mPunters.add(new Coord((int)x,(int)y));
                Log.d("XXX", "=>("+i+")"+x+","+y);
            }

            int eventaction = event.getAction();

            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mostraPunt = true;
                    break;

                case MotionEvent.ACTION_UP:
                    mostraPunt = false;
                    break;
            }

    //        cx = event.getX();
    //        cy = event.getY();
            return true;
        }
    */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Creem el bitmap que actuarà com a buffer de pantalla
        mBuffer = Bitmap.createBitmap(getWidth(),
                getHeight(), Bitmap.Config.ARGB_8888);
        mCanvasBuffer = new Canvas(mBuffer);
        fil = new FilSurface(this.getHolder(), this);
        fil.start();

 }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(fil!=null){
            fil.setEnExecucio(false);
            while(true){
                try {
                    // ens esperem a que el fil acabi
                    fil.join();
                    break;
                } catch(InterruptedException ex){
                }
            }
        }
    }


}
