package hmm.sensortest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends Activity {

    //线程是否停止标志位
    private boolean STOP = true;
    private boolean END  = false;
    
    private float acceValusX = 0f;
    private float acceValusY = 0f;
    private float acceValusZ = 0f;

    private mHandler handler;
    private mThread thread;
    private Button startButton;
    private Button stopButton;
    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;
    
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private OnSensorEventListener mOnSensorEventListener = new OnSensorEventListener();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new mHandler();
        thread = new mThread();

        xTextView = (TextView)findViewById(R.id.x_textView);
        yTextView = (TextView)findViewById(R.id.y_textView);
        zTextView = (TextView)findViewById(R.id.z_textView);
        startButton = (Button)findViewById(R.id.button_start);
        stopButton = (Button)findViewById(R.id.button_stop);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        //设置“开始测试”按钮事件处理
        startButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                
                if (END)
                	thread = new mThread();
                
                //设置标志位
                STOP = false;
                //开启新的线程
                thread.start();
            }
        });
        
        //设置“停止测试”按钮事件处理
        stopButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                
                //设置标志位
                STOP = true;
            }
        });

        //从系统服务中获得传感器管服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //注册传感器监听
        mSensorManager.registerListener(mOnSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    //自己定义的Handler类
    private class mHandler extends Handler
    {
        @Override
        public void handleMessage (Message msg)
        {
            switch(msg.what)
            {
                case 1:
                {
                    //显示
                    xTextView.setText(Float.toString(acceValusX));
                    yTextView.setText(Float.toString(acceValusY));
                    zTextView.setText(Float.toString(acceValusZ));
                    break;
                }
            }
        }
    }

    private class OnSensorEventListener implements SensorEventListener
    {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) 
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onSensorChanged(SensorEvent event) 
        {
            // TODO Auto-generated method stub
            
            //获得x轴的值
            acceValusX = event.values[0];
            //获得y轴的值
            acceValusY = event.values[1];
            //获得z轴的值
            acceValusZ = event.values[2];
            
        }
        
    }
    
    //自己定义的Thread类
    private class mThread extends Thread
    {

        @Override
        //线程启动时执行这个函数
        public void run()
        {
            //一直循环，直到标志位为“真”
            while(!STOP)
            {
                try {
                    //延时500ms
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = new Message();
                //消息标志
                msg.what = 1;
                //发送这个消息
                handler.sendMessage(msg);
            }
            
            END = true;
        }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //注销传感器监听
        mSensorManager.unregisterListener(mOnSensorEventListener, mAccelerometer);
    }
}
