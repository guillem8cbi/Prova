package net.iesmila.paint;

import android.graphics.Canvas;
import android.view.*;


public class FilSurface extends Thread{


    private SurfaceHolder holder;
    private SurfacePersonalitzada surface;
    private boolean enExecucio;

    public FilSurface(SurfaceHolder _holder, SurfacePersonalitzada _surface ){
        this.holder  = _holder;
        this.surface = _surface;
        enExecucio   = false;
    }

    public void setEnExecucio(boolean exec){
        this.enExecucio = exec;
    }

    @Override
    public void run() {
        enExecucio = true;
        Canvas canvas;
        // Iterem mentre no ens aturin
        while(enExecucio){
            canvas = null;
            try {
				/*
					Començar l'edició dels pixels al SurfaceView. El Canvas retornat pot ser usat
					per dibuixar al bitmpa de la superfície. En el cas bàsic cal repintar tota la
					superfície, si es vol preservar una part de la imatge, es pot usar una versió
					diferent....
					Retorna un null si no es pot crear la superfície o no es pot editar.
				 */
                canvas = holder.lockCanvas();
                // evitem que més d'un fil pugui dibuixar sobre la superfície.
                synchronized (holder) {
                    // dibuixem sobre la superfície.
                    surface.onDraw(canvas);
                }
            } finally {
                if(canvas!=null){
                    // Mostrar per pantalla el canvas.
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}
