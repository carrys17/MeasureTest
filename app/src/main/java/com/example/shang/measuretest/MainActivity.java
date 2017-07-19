package com.example.shang.measuretest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tx ;
    int width,height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tx = (TextView) findViewById(R.id.tx);
        // 这种方式是得不到measure宽高的。无论是在onStart、onResume、onCreate中都不行。
        // 原因就是view的measure过程跟activity的生命周期不是同步进行的，无法保证measure是否完成了。
        width = tx.getMeasuredWidth();
        height = tx.getMeasuredHeight();
        Log.i("xyz","直接获取  width == "+ width+"  height == "+height);
    }


    // 有四种方式可以得到view的宽高

    // 一、在onWindowFocusChanged方法中，这个方法的含义是View已经初始化成功了，宽高已经准备好
    // 了，所以可以得到，需要注意的就是Activity的窗口得到/失去焦点时，该方法会被重复调用
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        width = tx.getMeasuredWidth();
        height = tx.getMeasuredHeight();
        Log.i("xyz","onWindowFocusChanged   width == "+ width+"  height == "+height);
    }



    @Override
    protected void onStart() {
        super.onStart();

        // 二、View.post(runnable),可以在onCreate、onStart、onResume中调用，效果一样。
        //通过post将一个runnable投递到消息队列的尾部，然后等待looper调用此runnable的时候，View也已经
        //初始化好了。
        tx.post(new Runnable() {
            @Override
            public void run() {
                width = tx.getMeasuredWidth();
                height = tx.getMeasuredHeight();
                Log.i("xyz","tx.post(new Runnable()    width == "+ width+"  height == "+height);
            }
        });

        // 三、ViewTreeObserver
        // 使用ViewTreeObserver的众多接口回调可以完成这个功能，如下面的addOnGlobalLayoutListener这个
        //接口，当View树的状态发生改变时或者View树内部的View的可见性发生变化时，onGlobalLayout方法将
        //被回调，这是获取View的宽高的一个好时机
        ViewTreeObserver observer = tx.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = tx.getMeasuredWidth();
                height = tx.getMeasuredHeight();
                Log.i("xyz","ViewTreeObserver    width == "+ width+"  height == "+height);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 四、measure(int widthMeasureSpec, int heightMeasureSpec)

        //这种比较复杂，根据View的LayoutParams分为三种情况进行处理
        // 1、match_parent  直接放弃，因为View的measure过程中，构造此种MeasureSpec需要知道parentSize，
        // 即父容器的剩余空间，而我们无法知道parentSize的大小。

        // 2、具体数值（dp/px）
        // 比如宽高都是100,这种有问题啊，得到的都是100
        // 在此时前面三种得到的值是根据你的xml布局中view是否设置了100dp，如果是，则为156，156
        //如果不是，则为117跟30（即文本大小）

        width = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
        height = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
        tx.measure(width,height);
        int w = tx.getMeasuredWidth();
        int x = tx.getMeasuredHeight();
        Log.i("xyz","measure 具体数值   width == "+ w+"  height == "+x);

        // 3、wrap_content，这里的(1<<30)-1就是2^30-1的意思，因为SpecSize是低20位上的
        width = View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
        height = View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
        tx.measure(width,height);
        int ww = tx.getMeasuredWidth();
        int xx = tx.getMeasuredHeight();
        Log.i("xyz","measure  wrap_content  width == "+ ww+"  height == "+xx);

        //关于View的measure过程，网上有两种错误的用法。说是违背了系统的内部实现规范（因为无法通过错误
        // 的MeasureSpec得到合法的SpecMode，从而导致Measure过程出错），其次不能保证一定能Measure出正确的结果


        // 纳闷的是我试了下，都可以，百度也没有细说，只好google了，也没有好的答案。唉，先记住吧
        // 1、
        width = View.MeasureSpec.makeMeasureSpec(-1, View.MeasureSpec.UNSPECIFIED);
        height = View.MeasureSpec.makeMeasureSpec(-1, View.MeasureSpec.UNSPECIFIED);
        tx.measure(width,height);
        int www = tx.getMeasuredWidth();
        int xxx = tx.getMeasuredHeight();
        Log.i("xyz","measure  -1  width == "+ www+"  height == "+xxx);

        //  2 、
        tx.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int wwww = tx.getMeasuredWidth();
        int xxxx = tx.getMeasuredHeight();
        Log.i("xyz","measure  LayoutParams.WRAP_CONTENT  width == "+ wwww+"  height == "+xxxx);
    }
}
