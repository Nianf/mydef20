package com.example.mydef20;

import androidx.annotation.FloatRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

public class ScaleActivity extends AppCompatActivity {
    private SeekBar damping;
    private SeekBar stiffness;//阻尼、硬度
    View sView;
    SpringAnimation xAnimation;
    SpringAnimation yAnimation;//坐标
    float dX=0f;
    float dY=0f;
    float sX;
    float sY;
    float vxDis;
    float vyDis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);
        //调整seekbar,设置硬度范围
        stiffness=(SeekBar) findViewById(R.id.stiffness);
        stiffness.setMax((int) SpringForce.STIFFNESS_HIGH);
        stiffness.setProgress((int) SpringForce.STIFFNESS_VERY_LOW);
        //设置阻尼范围,因为阻尼系数范围是0.0-1.0,所以乘以1000可视化为seekbar，转化为整数
        damping=(SeekBar) findViewById(R.id.damping);
        //damping.setMax((int) (SpringForce.DAMPING_RATIO_NO_BOUNCY*1000));
        damping.setProgress((int) (SpringForce.DAMPING_RATIO_HIGH_BOUNCY*1000));

        sView=findViewById(R.id.SView);
        sView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vxDis=sView.getRight()-sView.getLeft();
                vyDis=sView.getBottom()-sView.getTop();
                sView.setPivotX(vxDis/2);
                sView.setPivotY(vyDis/2);
                xAnimation=createSpringAnimation(sView,SpringAnimation.SCALE_X,sView.getScaleX(),getStiffness(),getDamp());
                yAnimation=createSpringAnimation(sView,SpringAnimation.SCALE_Y,sView.getScaleY(),getStiffness(),getDamp());
                sView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });//确定初始位置，确定后移除监听器
        sView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:

                        sX=vxDis/2-event.getRawX();//计算鼠标到当前视图中心的距离
                        sY=vyDis/2-event.getRawY();
                        xAnimation.getSpring().setStiffness(getStiffness());
                        yAnimation.getSpring().setStiffness(getStiffness());
                        xAnimation.getSpring().setDampingRatio(getDamp());
                        yAnimation.getSpring().setDampingRatio(getDamp());
//                        xAnimation.cancel();
//                        yAnimation.cancel();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if((vxDis/2-event.getRawX())/sX >= 0 && (vyDis/2-event.getRawY())/sY >= 0){
                            sView.animate()
                                    .scaleX((vxDis/2-event.getRawX())/sX)
                                    .scaleY((vyDis/2-event.getRawY())/sY)
                                    .setDuration(0)
                                    .start();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        xAnimation.getSpring().setFinalPosition(1);
                        yAnimation.getSpring().setFinalPosition(1);
                        xAnimation.start();
                        yAnimation.start();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("Range")
    SpringAnimation createSpringAnimation(View view,
                                          DynamicAnimation.ViewProperty property,
                                          Float scaleChange,
                                          @FloatRange(from = 0.0) Float stiffness,
                                          @FloatRange(from = 0.0) Float dampingRatio) {
        //创建弹性动画类SpringAnimation
        SpringAnimation animation = new SpringAnimation(view, property);
        //SpringForce类，定义弹性特质
        SpringForce spring = new SpringForce(scaleChange);
        spring.setStiffness(stiffness);//
        spring.setDampingRatio(dampingRatio);
        //关联弹性特质
        animation.setSpring(spring);
        return animation;
    }
    private float getStiffness(){
        return Math.max(stiffness.getProgress(),1f);
    }
    private float getDamp(){
        return damping.getProgress()/1000f;
    }
}