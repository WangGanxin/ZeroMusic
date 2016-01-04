package com.ganxin.zeromusic.common.widget.checkbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

public class IconCheckBox extends SvgPathView implements android.view.View.OnClickListener {

	private boolean checked, progressing;
	private CheckedChangedListener checkedChanged;

	private float mCheckedProgress = 0;
	private PathDataSet checkedPath,pathDataSet;
	private Matrix scaleMatrix;
	private boolean notifyChanged=true;

	public IconCheckBox(Context context) {
		super(context);
	}

	public IconCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setOnClickListener(this);
	}
	
	public void setOnCheckedChangedListener(CheckedChangedListener checkedChanged){
		this.checkedChanged=checkedChanged;
	}

	private void init() {
		scaleMatrix = new Matrix();
		scaleMatrix.setScale(1.0f, 1.0f);
		if (checked) {
			mCheckedProgress = 1.0f;
		}
		setUnchecked();
		setChecked();
	}
	public void setChecked(boolean isChecked) {
		setChecked(isChecked,true);
	}

	public void setChecked(boolean isChecked,boolean notifyChanged) {
		if(this.checked==isChecked)return;
		this.checked = isChecked;
		if (checked) {
			mCheckedProgress = 1.0f;
		}else{
			mCheckedProgress = 0.0f;
		}
		if(this.checkedChanged!=null&&notifyChanged){
			this.checkedChanged.checkedChanged(this,isChecked);
		}
		requestLayout();
		postInvalidate();
	}
	public void setChecking(boolean isChecked){
		setChecking(isChecked,true);
	}
	public void setChecking(boolean isChecked,boolean notifyChanged){
		this.notifyChanged=notifyChanged;
		if(this.checked!=isChecked){
			progressing=true;
			if(isChecked){
				goChecked();
			}else{
				goUnchecked();
			}
		}
	}
	public boolean isChecked(){
		return this.checked;
	}

	private void setUnchecked() {
		pathDataSet = new PathDataSet();
		pathDataSet. computeDatas( icon);
		pathDataSet.mPaint.setColor(this.iconColor.getColorForState(
				getDrawableState(), Color.BLACK));
	}

	private void setChecked() {
		checkedPath = new PathDataSet();
		checkedPath.mPaint.setColor(checkedColor);		
		checkedPath .computeDatas(iconChecked);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		pathDataSet.mPaint.setAlpha((int) (255 * (1 - mCheckedProgress)));
		canvas.drawPath(pathDataSet.mPath, pathDataSet.mPaint);
		checkedPath.mPaint.setAlpha((int) (255 * mCheckedProgress));
		canvas.save();
		scaleMatrix.setScale(mCheckedProgress, mCheckedProgress, pathDataSet.mWidth / 2,
				pathDataSet.mHeight / 2);
		canvas.concat(scaleMatrix);
		canvas.drawPath(checkedPath.mPath, checkedPath.mPaint);
		canvas.restore();
	}

	private void updateOnProgress(float delt) {
		this.mCheckedProgress += delt;
		if (this.mCheckedProgress < 0) {
			this.mCheckedProgress = 0;
		}
		if (this.mCheckedProgress > 1) {
			this.mCheckedProgress = 1;
		}
		if (this.mCheckedProgress == 1 || this.mCheckedProgress == 0) {
			progressing = false;
			checked = (mCheckedProgress == 1);
			if (this.checkedChanged != null&&notifyChanged) {
				this.checkedChanged.checkedChanged(this,checked);
			}
		}

	}

	private void goChecked() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressing) {
					updateOnProgress(0.1f);
					postInvalidate();
					goChecked();
				}
			}
		}, 15);
	}

	private void goUnchecked() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressing) {
					updateOnProgress(-0.1f);
					postInvalidate();
					goUnchecked();
				}
			}
		}, 15);
	}
	

	public interface CheckedChangedListener {
		void checkedChanged(IconCheckBox checkBox,boolean isChecked);
	}

	@Override
	protected void onMeasure(int wms, int hms) {
		setMeasuredDimension((int) pathDataSet.mWidth + 1, (int) pathDataSet.mHeight + 1);
	}

	@Override
	public void onClick(View v) {
		if (this.progressing) {
			return;
		}
		progressing = true;
		if (checked) {
			goUnchecked();
		} else {
			goChecked();
		}
		invalidate();
	}
}
