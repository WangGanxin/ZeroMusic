package com.ganxin.zeromusic.common.widget.lyric;

import java.util.List;

import com.ganxin.zeromusic.common.util.DensityHelper;
import com.ganxin.zeromusic.view.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LrcView extends View implements ILrcView {

	public final static String TAG = "LrcView";

	/** normal display mode */
	public final static int DISPLAY_MODE_NORMAL = 0;
	/** seek display mode */
	public final static int DISPLAY_MODE_SEEK = 1;
	/** scale display mode ,scale font size */
	public final static int DISPLAY_MODE_SCALE = 2;

	private List<LrcRow> mLrcRows;
	private int mMinSeekFiredOffset = 10; // min offset for fire seek action,
											// px;
	private int mHignlightRow = 0; // current singing row , should be
									// highlighted.
	private int mHignlightRowColor = getResources().getColor(R.color.green);
	private int mNormalRowColor = Color.BLACK;
	private int mSeekLineColor = getResources().getColor(R.color.green);
	private int mSeekLineTextColor = getResources().getColor(R.color.green);
	private int mSeekLineTextSize = 15;
	private int mMinSeekLineTextSize = 13;
	private int mMaxSeekLineTextSize = 18;
	private int mLrcFontSize = DensityHelper.sp2px(getContext(), 14); // font size of lrc
	private int mMinLrcFontSize = 10;
	private int mMaxLrcFontSize = 16;
	private int mPaddingY = 10; // padding of each row
	private int mSeekLinePaddingX = 0; // Seek line padding x
	private int mDisplayMode = DISPLAY_MODE_NORMAL;
	private LrcViewListener mLrcViewListener;

	private String mLoadingLrcTip = "暂无歌词";

	private Paint mPaint;

	public LrcView(Context context) {
		super(context);
	}

	public LrcView(Context context, AttributeSet attr) {
		super(context, attr);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mLrcFontSize);
	}

	public void setListener(LrcViewListener l) {
		mLrcViewListener = l;
	}

	// 对外暴漏默认显示的文本
	public void setLoadingTipText(String text) {
		mLoadingLrcTip = text;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final int height = getHeight(); // height of this view
		final int width = getWidth(); // width of this view

		// 默认显示
		if (mLrcRows == null || mLrcRows.size() == 0) {
			if (mLoadingLrcTip != null) {
				// draw tip when no lrc.
				mPaint.setColor(mHignlightRowColor);
				mPaint.setTextSize(mLrcFontSize);
				mPaint.setTextAlign(Align.CENTER);
				canvas.drawText(mLoadingLrcTip, width / 2, height / 2
						- mLrcFontSize, mPaint);
			}
			return;
		}

		int rowY = 0;
		final int rowX = width / 2;
		int rowNum = 0;

		// 高亮度
		String highlightText = mLrcRows.get(mHignlightRow).content;
		str = mLrcRows.get(mHignlightRow).strTime;
		int highlightRowY = height / 2 - mLrcFontSize;
		mPaint.setColor(mHignlightRowColor);
		mPaint.setTextSize(mLrcFontSize);
		mPaint.setTextAlign(Align.CENTER);
		canvas.drawText(highlightText, rowX, highlightRowY, mPaint);

		// 显示时间还有线
		if (mDisplayMode == DISPLAY_MODE_SEEK) {
			// draw Seek line and current time when moving.
			mPaint.setColor(mSeekLineColor);
			canvas.drawLine(mSeekLinePaddingX, highlightRowY, width
					- mSeekLinePaddingX, highlightRowY, mPaint);
			mPaint.setColor(mSeekLineTextColor);
			mPaint.setTextSize(mSeekLineTextSize);
			mPaint.setTextAlign(Align.LEFT);
			canvas.drawText(mLrcRows.get(mHignlightRow).strTime, 0,
					highlightRowY, mPaint);
		}

		mPaint.setColor(mNormalRowColor);
		mPaint.setTextSize(mLrcFontSize);
		mPaint.setTextAlign(Align.CENTER);
		rowNum = mHignlightRow - 1;
		rowY = highlightRowY - mPaddingY - mLrcFontSize;
		while (rowY > -mLrcFontSize && rowNum >= 0) {
			String text = mLrcRows.get(rowNum).content;
			canvas.drawText(text, rowX, rowY, mPaint);
			rowY -= (mPaddingY + mLrcFontSize);
			rowNum--;
		}

		rowNum = mHignlightRow + 1;
		rowY = highlightRowY + mPaddingY + mLrcFontSize;
		while (rowY < height && rowNum < mLrcRows.size()) {
			String text = mLrcRows.get(rowNum).content;
			canvas.drawText(text, rowX, rowY, mPaint);
			rowY += (mPaddingY + mLrcFontSize);
			rowNum++;
		}
	}

	public void seekLrc(int position) {
		if (mLrcRows == null || position < 0 || position > mLrcRows.size()) {
			return;
		}
		LrcRow lrcRow = mLrcRows.get(position);
		mHignlightRow = position;
		invalidate();
		if (mLrcViewListener != null) {
			mLrcViewListener.onLrcSeeked(position, lrcRow);
		}
	}

	private float mLastMotionY;
	private PointF mPointerOneLastMotion = new PointF();
	private PointF mPointerTwoLastMotion = new PointF();
	private boolean mIsFirstMove = false; // whether is first move , some events
											// can't not detected in touch down,
											// such as two pointer touch, so
											// it's good place to detect it in
											// first move

	@SuppressWarnings("unused")
	private String str;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mLrcRows == null || mLrcRows.size() == 0) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "down,mLastMotionY:" + mLastMotionY);
			mLastMotionY = event.getY();
			mIsFirstMove = true;
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:

			if (event.getPointerCount() == 2) {
				Log.d(TAG, "two move");
				doScale(event);
				return true;
			}
			Log.d(TAG, "one move");
			// single pointer mode ,seek
			if (mDisplayMode == DISPLAY_MODE_SCALE) {
				// if scaling but pointer become not two ,do nothing.
				return true;
			}

			doSeek(event);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mDisplayMode == DISPLAY_MODE_SEEK) {
				seekLrc(mHignlightRow);

				// 指定播放的位置（以毫秒为单位）
			//	PlayerService.player.seekToMusic(timeConvert(str));
			}
			mDisplayMode = DISPLAY_MODE_NORMAL;
			invalidate();
			break;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private static int timeConvert(String timeString) {
		timeString = timeString.replace('.', ':');
		String[] times = timeString.split(":");
		// mm:ss:SS
		return Integer.valueOf(times[0]) * 60 * 1000
				+ Integer.valueOf(times[1]) * 1000 + Integer.valueOf(times[2]);
	}

	private void doScale(MotionEvent event) {

		if (mDisplayMode == DISPLAY_MODE_SEEK) {
			// if Seeking but pointer become two, become to scale mode
			mDisplayMode = DISPLAY_MODE_SCALE;
			Log.d(TAG, "two move but teaking ...change mode");
			return;
		}
		// two pointer mode , scale font
		if (mIsFirstMove) {
			mDisplayMode = DISPLAY_MODE_SCALE;
			invalidate();
			mIsFirstMove = false;
			setTwoPointerLocation(event);
		}
		int scaleSize = getScale(event);
		Log.d(TAG, "scaleSize:" + scaleSize);
		if (scaleSize != 0) {
			setNewFontSize(scaleSize);
			invalidate();
		}
		setTwoPointerLocation(event);
	}

	private void doSeek(MotionEvent event) {
		float y = event.getY();
		float offsetY = y - mLastMotionY; // touch offset.
		if (Math.abs(offsetY) < mMinSeekFiredOffset) {
			// move to short ,do not fire seek action
			return;
		}
		mDisplayMode = DISPLAY_MODE_SEEK;
		int rowOffset = Math.abs((int) offsetY / mLrcFontSize); // highlight row
																// offset.
		Log.d(TAG, "move new hightlightrow : " + mHignlightRow + " offsetY: "
				+ offsetY + " rowOffset:" + rowOffset);
		if (offsetY < 0) {
			// finger move up
			mHignlightRow += rowOffset;
		} else if (offsetY > 0) {
			// finger move down
			mHignlightRow -= rowOffset;
		}
		mHignlightRow = Math.max(0, mHignlightRow);
		mHignlightRow = Math.min(mHignlightRow, mLrcRows.size() - 1);

		if (rowOffset > 0) {
			mLastMotionY = y;
			invalidate();
		}
	}

	private void setTwoPointerLocation(MotionEvent event) {
		mPointerOneLastMotion.x = event.getX(0);
		mPointerOneLastMotion.y = event.getY(0);
		mPointerTwoLastMotion.x = event.getX(1);
		mPointerTwoLastMotion.y = event.getY(1);
	}

	private void setNewFontSize(int scaleSize) {
		mLrcFontSize += scaleSize;
		mSeekLineTextSize += scaleSize;
		mLrcFontSize = Math.max(mLrcFontSize, mMinLrcFontSize);
		mLrcFontSize = Math.min(mLrcFontSize, mMaxLrcFontSize);
		mSeekLineTextSize = Math.max(mSeekLineTextSize, mMinSeekLineTextSize);
		mSeekLineTextSize = Math.min(mSeekLineTextSize, mMaxSeekLineTextSize);
	}

	// get font scale offset
	private int getScale(MotionEvent event) {
		Log.d(TAG, "scaleSize getScale");
		float x0 = event.getX(0);
		float y0 = event.getY(0);
		float x1 = event.getX(1);
		float y1 = event.getY(1);
		float maxOffset = 0; // max offset between x or y axis,used to decide
								// scale size

		boolean zoomin = false;

		float oldXOffset = Math.abs(mPointerOneLastMotion.x
				- mPointerTwoLastMotion.x);
		float newXoffset = Math.abs(x1 - x0);

		float oldYOffset = Math.abs(mPointerOneLastMotion.y
				- mPointerTwoLastMotion.y);
		float newYoffset = Math.abs(y1 - y0);

		maxOffset = Math.max(Math.abs(newXoffset - oldXOffset),
				Math.abs(newYoffset - oldYOffset));
		if (maxOffset == Math.abs(newXoffset - oldXOffset)) {
			zoomin = newXoffset > oldXOffset ? true : false;
		} else {
			zoomin = newYoffset > oldYOffset ? true : false;
		}

		Log.d(TAG, "scaleSize maxOffset:" + maxOffset);

		if (zoomin)
			return (int) (maxOffset / 10);
		else
			return -(int) (maxOffset / 10);
	}

	// 设置歌词行数
	public void setLrc(List<LrcRow> lrcRows) {
		mLrcRows = lrcRows;

		setTextSize(lrcRows);
		
		invalidate();
	}
	
	/**
	 * 根据歌词里面最长的那句来确定歌词字体的大小
	 */
	private void setTextSize(List<LrcRow> lrcRows){
		
		if(lrcRows!=null){
		    int max=lrcRows.get(0).content.length();	    
		    for(int i=1;i<lrcRows.size();i++){	    	
		    	LrcRow lrcStrLength=lrcRows.get(i);	        
		        if(max<lrcStrLength.content.length()){	        	
		            max=lrcStrLength.content.length();  
		        }  
		    }
	         	    	    
		    mLrcFontSize=max>=68?DensityHelper.sp2px(getContext(), 12):DensityHelper.sp2px(getContext(), 14);
		}
	}

	// 设置正在播放的时间段
	public void seekLrcToTime(long time) {
		if (mLrcRows == null || mLrcRows.size() == 0) {
			return;
		}

		if (mDisplayMode != DISPLAY_MODE_NORMAL) {
			// touching
			return;
		}

		Log.d(TAG, "seekLrcToTime:" + time);
		// find row
		for (int i = 0; i < mLrcRows.size(); i++) {
			LrcRow current = mLrcRows.get(i);
			LrcRow next = i + 1 == mLrcRows.size() ? null : mLrcRows.get(i + 1);

			if ((time >= current.time && next != null && time < next.time)
					|| (time > current.time && next == null)) {
				seekLrc(i);
				return;
			}
		}
	}
}
