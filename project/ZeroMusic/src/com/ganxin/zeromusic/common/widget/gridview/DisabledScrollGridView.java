package com.ganxin.zeromusic.common.widget.gridview;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * 
 * @Description 重写的GridView,使其自身不带滚动
 * @author ganxin
 * @date Apr 8, 2015
 * @email ganxinvip@163.com
 */
public class DisabledScrollGridView extends GridView {     
    public DisabledScrollGridView(Context context, AttributeSet attrs) {     
        super(context, attrs);     
    }     
    
    public DisabledScrollGridView(Context context) {     
        super(context);     
    }     
    
    public DisabledScrollGridView(Context context, AttributeSet attrs, int defStyle) {     
        super(context, attrs, defStyle);     
    }     
    
    @Override     
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {     
    
        int expandSpec = MeasureSpec.makeMeasureSpec(     
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);     
        super.onMeasure(widthMeasureSpec, expandSpec);     
    }   
} 