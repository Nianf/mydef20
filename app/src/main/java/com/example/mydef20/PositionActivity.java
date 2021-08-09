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

public class PositionActivity extends AppCompatActivity {
    float stiffNess= SpringForce.STIFFNESS_MEDIUM;//硬度，可设
    float damp=SpringForce.DAMPING_RATIO_HIGH_BOUNCY;//阻尼比
    SpringAnimation xAnimation;
    SpringAnimation yAnimation;//坐标
    View mView;
    float dX=0f;
    float dY=0f;
    //创建一个动画对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        mView=findViewById(R.id.PView);
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                xAnimation=createSpringAnimation(mView,SpringAnimation.X,mView.getX(),stiffNess,damp);
                yAnimation=createSpringAnimation(mView,SpringAnimation.Y,mView.getY(),stiffNess,damp);
                mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });//确定初始位置，确定后移除监听器
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        dX=v.getX()-event.getRawX();//计算距离
                        dY=v.getY()-event.getRawY();
                        xAnimation.cancel();
                        yAnimation.cancel();//按住图片
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mView.animate()
                                .x(event.getRawX()+dX)
                                .y(event.getRawY()+dY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
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
                                          Float finalPosition,
                                          @FloatRange(from = 0.0) Float stiffness,
                                          @FloatRange(from = 0.0) Float dampingRatio) {
        //创建弹性动画类SpringAnimation
        SpringAnimation animation = new SpringAnimation(view, property);
        //SpringForce类，定义弹性特质
        SpringForce spring = new SpringForce(finalPosition);
        spring.setStiffness(stiffness);//
        spring.setDampingRatio(dampingRatio);
        //关联弹性特质
        animation.setSpring(spring);
        return animation;
    }
}