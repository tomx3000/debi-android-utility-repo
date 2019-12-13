package com.example.debiread_library.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.debiread_library.R;

public class MagnaCashSliderView extends View {
    private Rect rectangle;
    private Paint rectangle_paint,circle_paint,oval_paint,text_paint,arc_paint;
    private int square_color;
    private int square_dimension;
    private int rectangle_paddinng = 20;
    private static final int SQUARE_SIDE = 100;
    private float cx,cy;
    private float rd =100f;
    private ValueAnimator animator;
    private int lastaction;
    private RadialGradient radialGradient;
    private LinearGradient linearGradient,white_gtadient_color;
    private MagnaCashSliderListener magnaCashSliderListener;
    private Bitmap button_image,actual_button_image;
    private boolean draw_slider = false;
    private boolean end_animation = true;
    private String TAG = "MagnaCashSliderView";
    private boolean touch_down = false;
    private boolean touch_up =false;
//    these are just a value added to make the slider stay at the rectangle ends properly (just to make things work)
    private int coeficient_of_top_placement = 20;
    private int coeficient_of_bottom_placement = 10;

    public MagnaCashSliderView(Context context) {
        super(context);
        init(null);
    }

    public MagnaCashSliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MagnaCashSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public MagnaCashSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public interface MagnaCashSliderListener{
        public void onTouchTop(boolean touch);
        public void onTouchBottom(boolean bottom);
    }

    public void setMagnaCashSliderListener(MagnaCashSliderListener magnaCashSliderListener){
        this.magnaCashSliderListener = magnaCashSliderListener;
    }

    public void swipeColor(){
        rectangle_paint.setColor(rectangle_paint.getColor()==square_color?Color.GREEN:square_color);
        circle_paint.setColor(rectangle_paint.getColor()==square_color?Color.parseColor("#2196f3"):square_color);
        postInvalidate();
    }

    public void init(@Nullable AttributeSet set ){
//        initalizing the button image
        button_image= BitmapFactory.decodeResource(getResources(), R.drawable.magna_share_slider_buttom_home);

//        initializning the actual image button
        actual_button_image = BitmapFactory.decodeResource(getResources(),R.drawable.magna_share_slider_button_image);

//        initializing the center rctangle for circle movement path
         rectangle = new Rect();
         rectangle_paint = new Paint(Paint.ANTI_ALIAS_FLAG);

         circle_paint = new Paint();
         circle_paint.setAntiAlias(true);

         text_paint = new Paint();
         text_paint.setAntiAlias(true);

         arc_paint = new Paint();
         arc_paint.setAntiAlias(true);
         arc_paint.setColor(Color.BLACK);


        circleWithGradientColor();

        if (set == null)return;

        TypedArray ta = getContext().obtainStyledAttributes(set,R.styleable.MagnaCashSliderView);

//        get attributes from the view
         square_color = ta.getColor(R.styleable.MagnaCashSliderView_square_color,Color.RED);
         square_dimension = ta.getDimensionPixelOffset(R.styleable.MagnaCashSliderView_square_size,SQUARE_SIDE);


//        this jepls to avoid messing up with the garbage collector
//        ta must be called after obtaining values from the set
        ta.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {

        if(draw_slider) {
// dealing with the recangle attributes
            rectangle.left = getLeft() + getWidth() / 2 - (int) (rd + rectangle_paddinng);
            rectangle.top = getTop() + (int) (rd);
            rectangle.right = rectangle.left + (int) (2 * (rd + rectangle_paddinng));
            rectangle.bottom = rectangle.top + getHeight() - (int) (2 * rd);
//            rectangle_paint.setShader(linearGradient);
            rectangle_paint.setColor(getResources().getColor(R.color.magna_slider_color_light_blue));

            canvas.drawRect(rectangle, rectangle_paint);

            //  top arc
            canvas.drawArc(rectangle.left, getTop(), rectangle.right, getTop() + (int) 2 * rd, 0, -180, true, rectangle_paint);

//  bottom arc
            canvas.drawArc(rectangle.left, rectangle.bottom - (rd), rectangle.right, rectangle.bottom + (rd), 0, 180, true, rectangle_paint);

            // dealing with the text  attributes
            text_paint.setColor(Color.BLACK);
            int manga_text_font = getResources().getDimensionPixelSize(R.dimen.magna_slider_font_size);
            text_paint.setTextSize(manga_text_font);


//        top arc text
            String text_deposit = "notification";
            Rect deposit_bounds = new Rect();
            text_paint.getTextBounds(text_deposit, 0, text_deposit.length(), deposit_bounds);
            canvas.drawText(text_deposit,rectangle.left+((rectangle.width()-deposit_bounds.width())/2),rectangle.top-(rd/2),text_paint);

//        bottom arc text
            String text_withdraw = "receipt";
            Rect withdraw_bounds = new Rect();
            text_paint.getTextBounds(text_withdraw, 0, text_withdraw.length(), withdraw_bounds);
            canvas.drawText(text_withdraw,rectangle.left+((rectangle.width()-withdraw_bounds.width())/2),rectangle.bottom+(rd/2),text_paint);

            circle_paint.setShader(linearGradient);

            //        drawing the circle
            if(cx == 0f || cy == 0f){
                cx = getWidth()/2;
                cy = getHeight()/2;
            }
            canvas.drawBitmap(actual_button_image,cx-(actual_button_image.getWidth()/2),cy-actual_button_image.getHeight()/2,null);
//            canvas.drawCircle(cx,cy,rd,circle_paint);

        }else {
            //        drawing the circle
            if(end_animation == false) {
                if (cx == 0f || cy == 0f) {
                    cx = getWidth() / 2;
                    cy = getHeight() / 2;
                }
                canvas.drawBitmap(actual_button_image,cx-(actual_button_image.getWidth()/2),cy-actual_button_image.getHeight()/2,null);
//                canvas.drawCircle(cx, cy, rd, circle_paint);
            }else{
                //            circle_paint.setShader(radialGradient);
                //        drawing the image on canvas
                canvas.drawBitmap(button_image,(getWidth()-button_image.getWidth())/2,(getHeight()-button_image.getHeight())/2,null);

            }

//            canvas.drawBitmap(actual_button_image,cx-(actual_button_image.getWidth()/2),cy-actual_button_image.getHeight()/2,null);

        }


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value= super.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                draw_slider = true;
                lastaction = MotionEvent.ACTION_DOWN;
                postInvalidate();
                return  true;

            }

            case MotionEvent.ACTION_UP: {

                if(lastaction == MotionEvent.ACTION_MOVE ){
                    auto_move_back((int)event.getY());
                    return true;
                }
                return value;
            }
            case MotionEvent.ACTION_MOVE:{
                float x = event.getX();
                float y = event.getY();

                if(y >= getHeight()){
                    y = getHeight()/2;
                }


                Log.d(TAG, "onTouchEvent: y: "+String.valueOf(y));

//                detecting if the click is within the circle .
//                using the equation of the circle
//                (x-cx)^2+(y-cy)*2 =rd&2

//                finding the respective dx and dy
                float dx = x-cx;
                float dy = y-cy;
//                from formulae
                if(Math.pow(dx,2)+Math.pow(dy,2)<=Math.pow(rd,2)){
                    Log.d(TAG, "onTouchEvent: insidecircle "+String.valueOf(y));
//                    inside of the circle touched
//                    cx = x;
                    lastaction = MotionEvent.ACTION_MOVE;
                    cy = y;
//                     cy = 0;

                    if(has_touch_circle(rectangle.left+rd,getTop(),cx,cy,rd)){
//                        magnaCashSliderListener.onTouchTop(true);
                        touch_up =true;
//                        limitting the circeles from hitting top  during finger sliding
//                        because during animation there is another limiter
                        cy = getTop()+(int)rd+coeficient_of_top_placement;

                    }else if(has_touch_circle(rectangle.left+rd,rectangle.bottom+rd,cx,cy,rd)){
//                        magnaCashSliderListener.onTouchBottom(true);
                        touch_down =true;

//                        limitting the circeles from hitting bottom   during finger sliding
//                        because during animation there is another limiter
                        cy = getHeight()-(int)rd -coeficient_of_bottom_placement;
                        Log.d(TAG, "onTouchEvent: bottom: "+String.valueOf(y));
                    }
                    postInvalidate();
                    return true;
                }
                return  value;
            }
        }

        return value;
    }

    public boolean has_touch_circle(float x,float y,float cx, float cy, float rd){
        float dx = x -cx;
        float dy = y -cy;
        if(Math.pow(dx,2)+Math.pow(dy,2)<=Math.pow(rd,2))
            return true;
        else return false;

    }

    public void text_for_width(){

    }

    public void auto_move_back(int current_position){
//        stopping the slider from going of the canvas
        if(current_position>getHeight() || touch_down){
//            stoping the slider from going through the bottom end of the canvas
            current_position=getHeight()-(int)rd-coeficient_of_bottom_placement;
            magnaCashSliderListener.onTouchBottom(true);
            touch_down =false;
//            stopping the slider from going through the top end of the canvas
        }else if(current_position < 0 || touch_up){
            current_position = getTop()+(int)rd+coeficient_of_top_placement;
            magnaCashSliderListener.onTouchTop(true);
            touch_up=false;
        }

         animator = ValueAnimator.ofInt(current_position,getHeight()/2);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                end_animation = false;
                cy = (int) animation.getAnimatedValue();
                draw_slider = false;
                if(cy == (getHeight()/2)){
                    end_animation = true;
                }
                postInvalidate();
            }

        });
        animator.setDuration(500);
        animator.start();
    }

    public void circleWithGradientColor(){

        float[] stopradient = new float[]{0,0.5f,1};
        int[] colorGradient = new int[] {getResources().getColor(R.color.magna_slider_gradient_three),getResources().getColor(R.color.magna_slider_gradient_one),getResources().getColor(R.color.magna_slider_gradient_two)};
        int [] whiteGradient = new int[]{Color.WHITE,Color.WHITE};

         radialGradient = new RadialGradient(cx,cy,rd,colorGradient,null, Shader.TileMode.MIRROR);

//          linearGradient = new LinearGradient(cx-rd,cy-rd,cx+rd,cy+rd,colorGradient,null, Shader.TileMode.REPEAT);
        linearGradient = new LinearGradient(rectangle.left,rectangle.top,rectangle.right,rectangle.bottom,colorGradient,stopradient, Shader.TileMode.CLAMP);

        white_gtadient_color = new LinearGradient(rectangle.left,rectangle.top,rectangle.right,rectangle.bottom,whiteGradient,null, Shader.TileMode.REPEAT);

    }



}
