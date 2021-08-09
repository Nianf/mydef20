package com.example.mydef20;

import androidx.annotation.FloatRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import android.annotation.SuppressLint;
import android.icu.lang.UProperty;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RotationActivity extends AppCompatActivity {
    float stiffNess= SpringForce.STIFFNESS_MEDIUM;//硬度，可设
    float damp=SpringForce.DAMPING_RATIO_HIGH_BOUNCY;//阻尼比
    float spine=0f;//旋转角度，负表示逆时针，正代表顺时针
    TextView rTextView;
    ImageView rView;
    SpringAnimation rotationAnimation;
    float curRotation=0f,preRotation=0f;
    float x,y;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);
        rTextView= findViewById(R.id.rTextView);
        rView=findViewById(R.id.RView);
        updateRotationText();
        rotationAnimation=createSpringAnimation(rView,SpringAnimation.ROTATION,spine,stiffNess,damp);
        rotationAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                updateRotationText();
            }
        });
        final float cx=rView.getWidth()/2f;
        final float cy=rView.getHeight()/2f;
        rView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        x= event.getX();
                        y= event.getY();
                        //rotationAnimation.cancel();//
                        UCRotation(v,x,y,cx,cy);
                        rotationAnimation.cancel();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x=event.getX();
                        y= event.getY();//检测鼠标移动，注意，会相对当前image的位置进行旋转
                        preRotation=curRotation;
                        UCRotation(v,x,y,cx,cy);
                        float angle=curRotation-preRotation;
                        float tmp=angle+v.getRotation();
                        v.setRotation(tmp);
                        updateRotationText();
                        break;
                    case MotionEvent.ACTION_UP:
                        rotationAnimation.start();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    public void UCRotation(View v,float tx,float ty,float ex,float ey){
        curRotation=v.getRotation()+(float) (Math.toDegrees(Math.atan2(tx - ex, ey - ty)));
    }
    private void updateRotationText(){
        @SuppressLint("DefaultLocale")
        String context=String.format("%.3f", rView.getRotation());
        rTextView.setText(context);
    }
    @SuppressLint("Range")
    SpringAnimation createSpringAnimation(View view,
                                          DynamicAnimation.ViewProperty property,
                                          Float rangle,
                                          @FloatRange(from = 0.0) Float stiffness,
                                          @FloatRange(from = 0.0) Float dampingRatio) {
        //创建弹性动画类SpringAnimation
        SpringAnimation animation = new SpringAnimation(view, property);
        //SpringForce类，定义弹性特质
        SpringForce spring = new SpringForce(rangle);
        spring.setStiffness(stiffness);//
        spring.setDampingRatio(dampingRatio);
        //关联弹性特质
        animation.setSpring(spring);
        return animation;
    }
}