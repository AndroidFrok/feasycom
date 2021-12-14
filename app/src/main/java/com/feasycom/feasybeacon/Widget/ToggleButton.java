package com.feasycom.feasybeacon.Widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;



import com.feasycom.feasybeacon.Spring.SimpleSpringListener;
import com.feasycom.feasybeacon.Spring.Spring;
import com.feasycom.feasybeacon.Spring.SpringConfig;
import com.feasycom.feasybeacon.Spring.SpringSystem;
import com.feasycom.feasybeacon.Spring.SpringUtil;
import com.feasycom.feasybeacon.R;
import com.feasycom.util.LogUtil;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
public class ToggleButton extends View {
	private SpringSystem springSystem;
	private Spring spring ;
	private float radius;
	/**
	 * the color of turn on state
	 */
	private int onColor = Color.parseColor("#4ebb7f");
	/**
	 * the color of turn off state
	 */
	private int offBorderColor = Color.parseColor("#dadbda");
	private int offColor = Color.parseColor("#ffffff");
	private int spotColor = Color.parseColor("#ffffff");

	private int borderColor = offBorderColor;
	private Paint paint ;
	private boolean toggleOn = false;
	private int borderWidth = 2;
	private float centerY;
	private float startX, endX;
	private float spotMinX, spotMaxX;
	private int spotSize ;
	private float spotX;
	private float offLineWidth;
	private RectF rect = new RectF();
	/**
	 *  default animation state
	 */
	private boolean defaultAnimate = true;

	/**
	 * whether the default is turned on
	 */
	private boolean isDefaultOn = true;

	private OnToggleChanged listener;
	
	private ToggleButton(Context context) {
		super(context);
	}
	public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setup(attrs);
	}
	public ToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(attrs);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		spring.removeListener(springListener);
	}
	
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		spring.addListener(springListener);
	}

	public void setup(AttributeSet attrs) {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL);
		paint.setStrokeCap(Cap.ROUND);
		
		springSystem = SpringSystem.create();
		spring = springSystem.createSpring();
		spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 7));
		
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				toggle(defaultAnimate);
			}
		});
		
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButton);
		offBorderColor = typedArray.getColor(R.styleable.ToggleButton_tbOffBorderColor, offBorderColor);
		onColor = typedArray.getColor(R.styleable.ToggleButton_tbOnColor, onColor);
		spotColor = typedArray.getColor(R.styleable.ToggleButton_tbSpotColor, spotColor);
		offColor = typedArray.getColor(R.styleable.ToggleButton_tbOffColor, offColor);
		borderWidth = typedArray.getDimensionPixelSize(R.styleable.ToggleButton_tbBorderWidth, borderWidth);
		defaultAnimate = typedArray.getBoolean(R.styleable.ToggleButton_tbAnimate, defaultAnimate);
		isDefaultOn = typedArray.getBoolean(R.styleable.ToggleButton_tbAsDefaultOn, isDefaultOn);
		typedArray.recycle();
		
		borderColor = offBorderColor;

		if (isDefaultOn) {
			toggleOn();
		}
	}
	
	public void toggle() {
		toggle(true);
	}
	
	public void toggle(boolean animate) {
		toggleOn = !toggleOn;
		takeEffect(animate);
		
		if(listener != null){
			listener.onToggle(toggleOn);
		}
	}
	
	public void toggleOn() {
		setToggleOn();
		if(listener != null){
			listener.onToggle(toggleOn);
		}
	}
	
	public void toggleOff() {
		setToggleOff();
		if(listener != null){
			listener.onToggle(toggleOn);
		}
	}
	/**
	 * set to open or close
	 */
	synchronized   public void setToggle(boolean enable){
		if(enable){
			setToggleOn();
		}else{
			setToggleOff();
		}
	}
	
	/**
	 * the settings display as open and do not trigger the toggle event
	 */
	public void setToggleOn() {
		setToggleOn(true);
	}

	/**
	 * determine if the switch is on
	 * @return
	 */
	public boolean isToggleOn(){
		return toggleOn;
	}
	/**
	 * @param animate    asd
	 */
	public void setToggleOn(boolean animate){
		LogUtil.i("button","on");
		toggleOn = true;
		takeEffect(animate);
	}
	
	/**
	 * the setting is displayed in a closed style and will not trigger a toggle event
	 */
	public void setToggleOff() {
		setToggleOff(true);
	}
	
	public void setToggleOff(boolean animate) {
		LogUtil.i("button","off");
		toggleOn = false;
		takeEffect(animate);
	}
	
	synchronized    private void takeEffect(boolean animate) {
		if(animate){
			spring.setEndValue(toggleOn ? 1 : 0);
		}else{
			/**
			 * spring is not called here, so the current value in the spring has not changed, here to set the current value of synchronization on both sides
			 */
			spring.setCurrentValue(toggleOn ? 1 : 0);
			calculateEffect(toggleOn ? 1 : 0);
		}
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		Resources r = Resources.getSystem();
		if(widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST){
			widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
		}
		
		if(heightMode == MeasureSpec.UNSPECIFIED || heightSize == MeasureSpec.AT_MOST){
			heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
		}
		
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		final int width = getWidth();
		final int height = getHeight();
		
		radius = Math.min(width, height) * 0.5f;
		centerY = radius;
		startX = radius;
		endX = width - radius;
		spotMinX = startX + borderWidth;
		spotMaxX = endX - borderWidth;
		spotSize = height - 4 * borderWidth;
		spotX = toggleOn ? spotMaxX : spotMinX;
		offLineWidth = 0;
	}
	
	
	SimpleSpringListener springListener = new SimpleSpringListener(){
		@Override
		public void onSpringUpdate(Spring spring) {
			final double value = spring.getCurrentValue();
			calculateEffect(value);
		}
	};

	private int clamp(int value, int low, int high) {
		return Math.min(Math.max(value, low), high);
	}

	
	@Override
	public void draw(Canvas canvas) {
		//
		rect.set(0, 0, getWidth(), getHeight());
		paint.setColor(borderColor);
		canvas.drawRoundRect(rect, radius, radius, paint);
		
		if(offLineWidth > 0){
			final float cy = offLineWidth * 0.5f;
			rect.set(spotX - cy, centerY - cy, endX + cy, centerY + cy);
			paint.setColor(offColor);
			canvas.drawRoundRect(rect, cy, cy, paint);
		}
		
		rect.set(spotX - 1 - radius, centerY - radius, spotX + 1.1f + radius, centerY + radius);
		paint.setColor(borderColor);
		canvas.drawRoundRect(rect, radius, radius, paint);
		
		final float spotR = spotSize * 0.5f;
		rect.set(spotX - spotR, centerY - spotR, spotX + spotR, centerY + spotR);
		paint.setColor(spotColor);
		canvas.drawRoundRect(rect, spotR, spotR, paint);
		
	}
	
	/**
	 * @param value
	 */
	private void calculateEffect(final double value) {
		final float mapToggleX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, spotMinX, spotMaxX);
		spotX = mapToggleX;
		
		float mapOffLineWidth = (float) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, 10, spotSize);
		
		offLineWidth = mapOffLineWidth;
		
		final int fb = Color.blue(onColor);
		final int fr = Color.red(onColor);
		final int fg = Color.green(onColor);
		
		final int tb = Color.blue(offBorderColor);
		final int tr = Color.red(offBorderColor);
		final int tg = Color.green(offBorderColor);
		
		int sb = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fb, tb);
		int sr = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fr, tr);
		int sg = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fg, tg);
		
		sb = clamp(sb, 0, 255);
		sr = clamp(sr, 0, 255);
		sg = clamp(sg, 0, 255);
		
		borderColor = Color.rgb(sr, sg, sb);
		
		postInvalidate();
	}
	
	/**
	 * @author ThinkPad
	 *
	 */
	public interface OnToggleChanged{
		/**
		 * @param on     = =
		 */
		public void onToggle(boolean on);
	}


	public void setOnToggleChanged(OnToggleChanged onToggleChanged) {
		listener = onToggleChanged;
	}
	
	public boolean isAnimate() {
		return defaultAnimate;
	}
	public void setAnimate(boolean animate) {
		this.defaultAnimate = animate;
	}
	
}
