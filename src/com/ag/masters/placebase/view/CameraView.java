package com.ag.masters.placebase.view;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Extends SurfaceView to preview live images from the camera of camera preview
 * perspective
 * 
 * http://itp.nyu.edu/~sve204/mobilemedia_spring10/androidCamera101.pdf
 * http://developer.android.com/guide/topics/media/camera.html
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

	SurfaceHolder mHolder;
	Camera mCamera;

	int width;
	int height;

	public CameraView(Context context) {
		super(context);
		holderCreation();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holderCreation();
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * Access the Camera object to make callbacks when the picture is taken
	 * @param shutter
	 * @param raw
	 * @param jpeg
	 */
	public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
		mCamera.takePicture(shutter, raw, jpeg);
	}

	public void holderCreation() {
		// Install a SurfaceHolder.Callback so we get 
		// notified when the underlying surface is created and destroyed
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		width = w;
		height = h;
		// now that the size is known, set up the camera and begin the preview
		Camera.Parameters parameters = mCamera.getParameters();
		//parameters.setPreviewSize(w, h);
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The surface has been created, acquire the camera and tell it where to draw
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
		
		Parameters params = mCamera.getParameters();
		
		if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			params.set("orientation", "portrait"); // "landscape"
			try{
				Method rotateSet = Camera.Parameters.class.getMethod(
						"setRotation", new Class[] {Integer.TYPE });
				Object arguments[] = new Object[] { new Integer(90) };
				rotateSet.invoke(params,arguments);
			} catch (NoSuchMethodException e) {
				Log.v("CAMERAVIEW", "No Set Rotation");
			} catch(IllegalAccessException e) {
				Log.v("CAMERAVIEW", "IllegalAccessException ");
			}catch (InvocationTargetException e) {
				Log.v("CAMERAVIEW", "InvocationTarget Exception");
			}
		}
		
		
		mCamera.setParameters(params);
		
		try{
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch(IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

}
