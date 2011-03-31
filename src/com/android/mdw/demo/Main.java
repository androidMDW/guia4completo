package com.android.mdw.demo;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements SurfaceHolder.Callback{
	/**
	 * Variables globales para administrar la grabación y reproducción 
	 */
	private MediaRecorder mediaRecorder = null;
	private MediaPlayer mediaPlayer = null;

	/**
	 * Variable que define el nombre para el archivo donde escribiremos el video a grabar
	 */
	private String fileName = null;
	
	/**
	 * Variable que indica cuando se está grabado 
	 */	
	private boolean recording = false;
	    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /**
         * inicializamos la variable para el nombre del archivo
         */
        fileName = Environment.getExternalStorageDirectory() + "/test.mp4";

        /**
         * inicializamos la "superficie" donde se reproducirá la vista previa de la grabación
         * y luego el video ya grabado
         */        
        SurfaceView surface = (SurfaceView)findViewById(R.id.surface);
        SurfaceHolder holder = surface.getHolder(); 
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        /**
         * inicializamos los botones sobre los que vamos a trabajar su evento de click
         */
        final Button btnRec = (Button)findViewById(R.id.btnRec);
        final Button btnStop = (Button)findViewById(R.id.btnStop);        
        final Button btnPlay = (Button)findViewById(R.id.btnPlay);    
        
        /**
         * Botón para grabar
         */
        btnRec.setOnClickListener(new OnClickListener() {			
        	@Override
        	
        	public void onClick(View v) {
        			/**
        			 * Al iniciar grabación deshabilitamos los botones de grabar y reproducir y 
        			 * habilitamos el de detener 
        			 */
        			btnRec.setEnabled(false);
        			btnStop.setEnabled(true);
        			btnPlay.setEnabled(false);
        			
        			/**
        			 * Llamamos el método que configura el media recorder y le decimos el archivo de salida 
        			 */        			
        			prepareRecorder();
        			mediaRecorder.setOutputFile(fileName);
        			try {
	        			/**
	        			 * Una vez configurado todo llamamos al método prepare que deja todo listo
	        			 * para iniciar la grabación
	        			 */        				
        				mediaRecorder.prepare();
        			} catch (IllegalStateException e) {
        			} catch (IOException e) {
        			}				        			
        			/**
        			 * Iniciamos la grabación y actualizamos el estatus de la variable recording
        			 */        				        			
        			mediaRecorder.start();
        			recording = true;
        	}
        });
        
        /**
         * Botón para detener
         */        
        btnStop.setOnClickListener(new OnClickListener() {			
        	@Override
        	public void onClick(View v) {
    			/**
    			 * Al iniciar detener habilitamos los botones de grabar y reproducir y 
    			 * deshabilitamos el de detener 
    			 */        		
        		btnRec.setEnabled(true);
        		btnStop.setEnabled(false);
        		btnPlay.setEnabled(true);
        		
    			/**
    			 * Si se está grabando, detenemos la grabación y reiniciamos la configuración 
    			 * además de volver falsa la variable de estatus de grabación 
    			 */ 
        		if (recording) {
        			recording = false;					
        			mediaRecorder.stop();	
        			mediaRecorder.reset();        			
    			/**
    			 * Si se está reproduciendo, detenemos la reproducción y reiniciamos la configuración  
    			 */         			
        		} else if (mediaPlayer.isPlaying()) {
        			mediaPlayer.stop();
        			mediaPlayer.reset();
        		}
        	}
        });  
        
        /**
         * Botón para reproducir
         */            
        btnPlay.setOnClickListener(new OnClickListener() {			
        	@Override
        	public void onClick(View v) {	
    			/**
    			 * Al iniciar la reproducción deshabilitamos los botones de grabar y reproducir y 
    			 * habilitamos el de detener 
    			 */              		
        		btnRec.setEnabled(false);
        		btnStop.setEnabled(true);
        		btnPlay.setEnabled(false);

    			/**
    			 * Al concluir la reproducción habilitamos los botones de grabar y reproducir y 
    			 * deshabilitamos el de detener 
    			 */              		        		
        		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {				
        			@Override
        			public void onCompletion(MediaPlayer mp) {
        				
        				btnRec.setEnabled(true);
        				btnStop.setEnabled(false);
        				btnPlay.setEnabled(true);
        			}
        		});
        		
    			/**
    			 * Configuramos el archivo a partir del cual se reproducirá, preparamos el Media Player
    			 *  e iniciamos la reproducción 
    			 */              		
        		try {
        			mediaPlayer.setDataSource(fileName);
        			mediaPlayer.prepare();
        		} catch (IllegalStateException e) {
        		} catch (IOException e) {
        		}				
        		
        		mediaPlayer.start();
        		
        	}
        });        
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	/**
	 * Inicializamos los recursos asociados a las variables para administrar la grabación y reproducción.
	 * Se verifica si las variables son nulas (para ejecutar este código solo una vez) y luego de
	 * inicializarlas se coloca el SurfaceHolder como display para la vista previa de la grabación y
	 * para la vista de la reproducción
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setPreviewDisplay(holder.getSurface());
		}
		
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(holder);				
		}		
	}

	/**
	 * Liberamos los recursos asociados a las variables para administrar la grabación y reproducción
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mediaRecorder.release();
		mediaPlayer.release();
	}
	

	/**
	 * Método para preparar la grabación, configurando los atributos de la fuente para audio y video, 
	 * el formado y el codificador.
	 */
	public void prepareRecorder(){
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); 
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	}
}