package hmm.flashlight;

/*import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}*/

import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 *  花花手电筒
 * @author huahua
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback{
	private static final String TAG = "huahua";      
	/**
	 * 进入APP时的背光亮度值
	 */
	int normal;
	/**
	 * 进入APP时，是否为自动调节亮度状态
	 */
	boolean AutoBrightnessOpen = false;
	
	private Camera camera; 
	
	private SurfaceView surfaceView;    
	private SurfaceHolder surfaceHolder;      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去除title   
       this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		         
        //去掉Activity上面的状态栏   
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
        WindowManager.LayoutParams.FLAG_FULLSCREEN); 

		setContentView(R.layout.activity_main);
		
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);      
		surfaceHolder = surfaceView.getHolder();      
		surfaceHolder.addCallback(this);      
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);     
		
		if(isAutoBrightness(getContentResolver()))
		{
			AutoBrightnessOpen = true;
		}
		
		normal = Settings.System.getInt(getContentResolver(),  
		        Settings.System.SCREEN_BRIGHTNESS, 255); 
		
		PackageManager pm= this.getPackageManager();
		 FeatureInfo[]  features=pm.getSystemAvailableFeatures();
		 for(FeatureInfo f : features)
		 {
		   if(PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))   //判断设备是否支持闪光灯
		   {
			   Log.d("huahua","支持闪光灯");
		   }
		 }
		 
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
        Closeshoudian();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Openshoudian();
	}
	
	/**
     * 判断是否开启了自动亮度调节
     * 
    * @param aContext
     * @return
     */
    public boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }
    
    /**
     * 停止自动亮度调节
     * 
    * @param activity
     */
    public void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }
    
    /**
     * 恢复自动亮度调节
     * 
    * @param activity
     */
    public void setAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
    
    /**
     * 打开手电筒
     */
    public void Openshoudian()
    {
    	//异常处理一定要加，否则Camera打开失败的话程序会崩溃
		try {
	        Log.d("huahua","camera打开");
			camera = Camera.open(); 
		} catch (Exception e) {
			Log.d("huahua","Camera打开有问题");
			Toast.makeText(MainActivity.this, "Camera被占用，请先关闭", Toast.LENGTH_SHORT).show();
		}
		
		if(camera != null)
		{
			//打开闪光灯
			 camera.startPreview();    
			 Camera.Parameters parameter = camera.getParameters();  
			 parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); 
			 camera.setParameters(parameter);
			 Log.d("huahua","闪光灯打开");
			 
			 //先关闭自动调节背光功能，才可以调节背光
			 if(AutoBrightnessOpen)
			 {
				stopAutoBrightness(MainActivity.this);
			 }
			 
			//将背光设置为最亮
	        WindowManager.LayoutParams lp = getWindow().getAttributes();
	        lp.screenBrightness = Float.valueOf(255) * (1f / 255f);
	        getWindow().setAttributes(lp);
		}
    }
    
    /**
     * 关闭手电筒
     */
    public void Closeshoudian()
    {
        if (camera != null)
        {
        	//关闭闪光灯
        	Log.d("huahua", "closeCamera()");
			camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF); 
			camera.setParameters(camera.getParameters());
	        camera.stopPreview();
	        camera.release();
	        camera = null;
	        
	        //恢复进入程序前的背光值
	        WindowManager.LayoutParams lp = getWindow().getAttributes();
	        lp.screenBrightness = Float.valueOf(normal) * (1f / 255f);
	        getWindow().setAttributes(lp);
	        
	        //如果进入APP时背光为自动调节，则退出时需要恢复为自动调节状态
			 if(AutoBrightnessOpen)
			 {
				 setAutoBrightness(MainActivity.this);
			 }
        }
    }
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {        
			if(camera != null)
			{
			camera.setPreviewDisplay(holder);      
			}
		} catch (IOException e) {        
			e.printStackTrace();      
		}  
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}


}

