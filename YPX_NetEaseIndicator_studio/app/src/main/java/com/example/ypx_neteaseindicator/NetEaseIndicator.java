
package com.example.ypx_neteaseindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ������Բ���ƶ�viewpagerָʾ��
 *
 * ���͵�ַ:http://blog.csdn.net/qq_16674697/article/details/51954228
 *
 * @author yangpeixing
 */
public class NetEaseIndicator extends LinearLayout implements
        OnPageChangeListener {
    private int screenWidth = 0;
    /**
     * �ڲ���Բ����paint
     */
    private Paint indicatorPaint;
    /**
     * ��Χ����������ȣ�Ĭ��Ϊ4px
     */
    private int strokeWidth = 4;

    /**
     * ����ɫ,��Ĭ��Ϊ��ɫ#ff0000
     */
    private int backgroundColor = Color.RED;
    /**
     * ��������ɫ
     */
    private int backgroundLineColor = Color.WHITE;
    /**
     * ָʾ����ɫ
     */
    private int indicatorColor = Color.WHITE;

    /**
     * ������ָʾ���뾶��Ĭ��Ϊ40px
     */
    private int backgroundRadius = 40;
    /**
     * ��¼tab�������
     */
    private int[] tabLengthArray;

    /**
     * x��ƫ����
     */
    private int mTransitX = 0;
    /**
     * tabĬ�ϴ�СΪ12
     */
    private int tabTextSize = 12;
    /**
     * ����tabĬ����ɫ����δѡ��ʱ��ɫ
     */
    private int tabTextColor = Color.WHITE;
    /**
     * tabѡ�к���ɫ,Ĭ�Ϻͱ���ɫһ��
     */
    private int tabPressColor = backgroundColor;

    private int mTabWidth = 0;
    private String[] titles;
    private ViewPager viewPager;
    /**
     * Ĭ�ϸ߶ȣ��û����Լ����ø߶ȣ�Ĭ��50px
     */
    private int defaultHeight = 50;
    /**
     * viewpager����
     */
    private int mCurrentIndex = 0;
    /**
     * Ĭ��ѡ�еڼ���tab
     */
    private int mInitIndex = 0;
    /**
     * �ж��Ƿ���
     */
    private boolean isClick = false;

    /**
     * �Ƿ����ñ���
     */
    private boolean isShowBackground = true;
    /**
     * �Ƿ�����ָʾ��
     */
    private boolean isShowIndicator = true;
    /**
     * �Ƿ�����tab����任
     */
    private boolean isShowTabSizeChange = true;
    /**
     * �Ƿ�ƽ��tab�Ŀ�ȣ���false���򷵻�ÿ��tab����Ӧ�Ŀ�ȣ��ʺ��ڶ�����Ŀ��ʹ��
     */
    private boolean isDeuceTabWidth = true;
    /**
     * �ɼ�tab����
     */
    private int visiableCounts = -1;
    /**
     * ��������С��Ĭ��Ϊ14
     */
    private int maxTabTextSize = 14;
    /**
     * tab����
     */
    private int totalCount = 0;
    /**
     * �Ƿ��ֶ�������tab�Ŀ��
     */
    private boolean isSetTabWidth=false;
    /**
     * �Ƿ���HorizonScrollView����View
     */
    private boolean isChildOfHorizontalScrollView = false;

    private int tabPaddingLeft = 0, tabPaddingRight = 0, tabPaddingTop = 0,
            tabPaddingBottom = 0;

    public NetEaseIndicator(Context context) {
        this(context, null);
    }

    public NetEaseIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetEaseIndicator(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        initView();

    }

    private void initView() {
        mCurrentIndex = 0;
        mInitIndex = 0;
        mTabWidth = 0;
        mTransitX = 0;
        titles = new String[]{"tab1", "tab2", "tab3"};
        tabLengthArray = new int[titles.length];
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        setBackgroundShape();
        initPaints();
        setTabViews();
    }

    /**
     * ���ñ���
     */
    private void setBackgroundShape() {
        // ����drawable
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(backgroundColor);
        gd.setCornerRadius(backgroundRadius);
        gd.setStroke(strokeWidth, backgroundLineColor);
        if (isShowBackground) {
            setBackground(gd);
        } else {
            setBackgroundResource(0);
        }
    }

    /**
     * ��ʼ��ָʾ������
     */
    private void initPaints() {
        indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStyle(Style.FILL);
    }

    /**
     * ��ȡtab
     */
    private void setTabViews() {
        tabLengthArray = new int[titles.length];
        removeAllViews();
        for (int i = 0; i < titles.length; i++) {
            addView(creatDefaultTab(titles[i], i));
        }
        calculateSize();
        setItemClickEvent();
    }

    /**
     * ������textview�Ƿ�ƽ�ֿ���������ɼ���Ŀ
     */
    private void calculateSize() {
        totalCount = titles.length;
        if (isDeuceTabWidth) {// ���ƽ��tab��ȣ���ֱ������Ļ���tab��
            visiableCounts = screenWidth / mTabWidth;
        } else {// �����ƽ��
            visiableCounts = getDefaultVisiableCount();
        }
    }

    /**
     * ��ȡ��Ļ����ʾ��tab����
     *
     * @return
     */
    private int getDefaultVisiableCount() {
        int defaultNum = 0;
        for (int i = 0; i < tabLengthArray.length; i++) {
            defaultNum += tabLengthArray[i];
            if (defaultNum >= screenWidth) {
                return i;
            }
        }
        return screenWidth / mTabWidth;
    }

    /**
     * ��ȡpositionǰ����tab���֮��
     *
     * @return
     */
    private int getTransitXByPosition(int posotion) {
        int defaultNum = 0;
        for (int i = 0; i < posotion; i++) {
            defaultNum += tabLengthArray[i];
        }
        return defaultNum;
    }

    /**
     * ����Ĭ��tab��Textview��
     *
     * @param string Ҫ��ʾ���ı�
     * @param i  ����
     */
    private TextView creatDefaultTab(String string, int i) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(tabTextColor);
        textView.setTextSize(tabTextSize);
        textView.setText(string);
        textView.setPadding(tabPaddingLeft, tabPaddingTop, tabPaddingRight,
                tabPaddingBottom);
        TextPaint mTextPaint;
        if (isShowTabSizeChange) {//�����Ƿ�����任
            TextView dTextView = new TextView(getContext());
            dTextView.setTextSize(maxTabTextSize);
            mTextPaint = dTextView.getPaint();//�õ����ߴ�textview��Paint�����ڲ������
        } else {
            mTextPaint = textView.getPaint();
        }
        if(!isSetTabWidth) {
            mTabWidth = (int) mTextPaint
                    .measureText(isDeuceTabWidth ? getMaxLengthString(titles)
                            : string)
                    + tabPaddingLeft + tabPaddingRight;
        }
        tabLengthArray[i] = mTabWidth;
        textView.setLayoutParams(new LinearLayout.LayoutParams(mTabWidth,
                defaultHeight + tabPaddingBottom + tabPaddingTop));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            textView.setAllCaps(true);
        }
        return textView;

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        defaultHeight = getMeasuredHeight();
        if (mCurrentIndex == 0) {
            mTabWidth = tabLengthArray[0];
        }
        int left = mTransitX + mInitIndex * mTabWidth;// tab��߾���ԭ���λ��
        int right = mTabWidth + left;// ����tab��λ��
        int top = 0;// tab���붥�˵�λ��
        int bottom = defaultHeight;// ����tab�ĸ߶�
        if (isShowIndicator) {
            if (creator != null) {
                creator.drawIndicator(canvas, left, top, right, bottom,
                        indicatorPaint, backgroundRadius);
            } else {
                drawIndicatorWithTransitX(canvas, left, top, right, bottom,
                        indicatorPaint);
            }
        }
        if (mInitIndex != 0) {
            (getTab(mInitIndex)).setTextColor(backgroundColor);
            int centerX = getTransitXByPosition(mInitIndex)
                    - (screenWidth - tabLengthArray[mInitIndex]) / 2;
            parentScrollto(centerX, 0);
        }
        mInitIndex = 0;// �����һ��Ĭ��index
        super.dispatchDraw(canvas);
    }

    /**
     * Ĭ��ΪԲ�Ǿ���ָʾ�����û��ɼ̳���д�Զ���ָʾ����ʽ
     *
     * @param canvas
     * @param left   tab��߾���ԭ���λ��
     * @param top    ����tab��λ��
     * @param right  tab���붥�˵�λ��
     * @param bottom ����tab�ĸ߶�,�ȿؼ��߶�
     * @param paint  ָʾ������
     */
    public void drawIndicatorWithTransitX(Canvas canvas, int left, int top,
                                          int right, int bottom, Paint paint) {
        if (backgroundRadius < defaultHeight / 2) {
            // ������������ַ�ʽ��ģ����Բ�ǻ�ʧ��
            RectF oval = new RectF(left, top, right, bottom);// ���ø��µĳ����Σ�ɨ�����
            canvas.drawRoundRect(oval, backgroundRadius, backgroundRadius,
                    paint);
        } else {// �����δ���Բ�Ǿ��Σ���Բ�����Ρ�Բ
            RectF oval2 = new RectF(bottom / 2 + left, top, right - bottom / 2,
                    bottom);
            canvas.drawCircle(oval2.left, bottom / 2, bottom / 2,
                    indicatorPaint);
            canvas.drawRect(oval2, indicatorPaint);
            canvas.drawCircle(oval2.right, bottom / 2, bottom / 2, paint);
        }
    }

    public void setViewPager(ViewPager viewPager, int index) {
        this.viewPager = viewPager;
        this.mCurrentIndex = index;
        mInitIndex = index;
        setItemClickEvent();
        if (getParent() != null
                && (getParent() instanceof HorizontalScrollView)) {
            isChildOfHorizontalScrollView = true;
        }
        viewPager.setOnPageChangeListener(this);
        setmCurrentIndex(index);
    }

    /**
     * ������ؼ���HorizonScrollView������Ƹ��ؼ��ƶ�
     *
     * @param x
     * @param y
     */
    public void parentScrollto(int x, int y) {
        if (isChildOfHorizontalScrollView) {
            ((HorizontalScrollView) getParent()).smoothScrollTo(x, y);
        }
    }

    /**
     * ������ɫ�任
     *
     * @param position
     * @param positionOffset
     */
    protected void setTabColorChange(int position, float positionOffset) {
        getTab(position).setTextColor(
                blendColors(tabPressColor, tabTextColor, positionOffset));

    }

    /**
     * ���������С�任
     *
     * @param position
     * @param positionOffset
     */
    protected void setTabSizeChange(int position, float positionOffset) {
        getTab(position).setTextSize(
                blendSize(tabTextSize, maxTabTextSize, positionOffset));
    }

    /**
     * ���õ���¼�
     */
    public void setItemClickEvent() {
        for (int i = 0; i < totalCount; i++) {
            final int j = i;
            getTab(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClick = true;
                    if (viewPager != null && viewPager.getAdapter() != null)
                        viewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * ������С����
     *
     * @param minSize
     * @param maxSize
     * @param ratio
     * @return
     */
    private float blendSize(int minSize, int maxSize, float ratio) {
        return (minSize + (maxSize - minSize) * ratio * 1.0f);
    }

    /**
     * ������ɫ����ת��
     *
     * @param color1
     * @param color2
     * @param ratio
     * @return
     */
    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio)
                + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio)
                + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio)
                + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public String[] getTitles() {
        return titles;
    }

    /**
     * ����ָʾ������,Ĭ��tab1��tab2��tab3
     *
     * @param titles
     */
    public void setTitles(String[] titles) {
        if (titles != null && titles.length > 0) {
            this.titles = titles;
            setTabViews();
            setItemClickEvent();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    /**
     * ����tab��С��Ĭ�ϴ�СΪ12
     *
     * @param tabTextSize
     */
    public void setTabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
        resetTabSize();
    }

    /**
     * ����ָʾ���߶ȣ�Ĭ��50px
     *
     * @param defaultHeight
     */
    public void setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
        setTabViews();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * ��������ȣ�Ĭ��Ϊ4px
     *
     * @param strokeWidth
     */
    public void setBackgroundStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        setBackgroundShape();
    }

    /**
     * ��ȡtab
     *
     * @param i
     * @return
     */
    public TextView getTab(int i) {
        if (i < titles.length) {
            return (TextView) getChildAt(i);
        } else {
            return (TextView) getChildAt(titles.length - 1);
        }
    }

    /**
     * ����tab��ɫ
     */
    public void resetTabColor() {
        for (int i = 0; i < totalCount; i++) {
            getTab(i).setTextColor(tabTextColor);
        }
    }

    /**
     * ����tab��С
     */
    public void resetTabSize() {
        for (int i = 0; i < totalCount; i++) {
            getTab(i).setTextSize(tabTextSize);
        }
    }

    public int getTabTextColor() {
        return tabTextColor;
    }

    /**
     * ����tabĬ����ɫ����δѡ��ʱ��ɫ��Ĭ��Ϊ��ɫ
     *
     * @param tabTextColor
     */
    public void setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
        for (int i = 0; i < totalCount; i++) {
            getTab(i).setTextColor(tabTextColor);
        }
    }

    public int getmBackgroundColor() {
        return backgroundColor;
    }

    /**
     * ���ñ���ɫ,ͬʱҲ��ѡ��tab���ı���ɫ��Ĭ��Ϊ��ɫ
     *
     * @param backgroundColor
     */
    public void setmBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackgroundShape();
    }

    public int getBackgroundRadius() {
        return backgroundRadius;
    }

    /**
     * ���ñ���Բ�Ǵ�С��Ĭ��Ϊ40px
     *
     * @param backgroundRadius
     */
    public void setBackgroundRadius(int backgroundRadius) {
        this.backgroundRadius = backgroundRadius;
        setBackgroundShape();
    }

    /**
     * ���������ɫ,Ĭ��Ϊ��ɫ
     *
     * @param backgroundLineColor
     */
    public void setBackgroundLineColor(int backgroundLineColor) {
        this.backgroundLineColor = backgroundLineColor;
        setBackgroundShape();
    }

    /**
     * ��ȡ��ǰviewpagerѡ����
     *
     * @return
     */
    public int getmCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * ����Ĭ����
     *
     * @param mCurrentIndex
     */
    private void setmCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.setCurrentItem(mCurrentIndex, true);
        }

    }

    public boolean isShowBackground() {
        return isShowBackground;
    }

    /**
     * �Ƿ���ʾ����
     *
     * @param isShowBackground
     */
    public void setShowBackground(boolean isShowBackground) {
        this.isShowBackground = isShowBackground;
        setBackgroundShape();
    }

    public boolean isShowIndicator() {
        return isShowIndicator;
    }

    /**
     * �Ƿ���ʾָʾ��
     *
     * @param isShowIndicator
     */
    public void setShowIndicator(boolean isShowIndicator) {
        this.isShowIndicator = isShowIndicator;
        invalidate();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    /**
     * ����ָʾ����ɫ��Ĭ��Ϊ��ɫ
     *
     * @param indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        indicatorPaint.setColor(indicatorColor);
        invalidate();
    }

    /**
     * ������������С,Ĭ��Ϊ14
     *
     * @param maxTabTextSize
     */
    public void setTabMaxTextSize(int maxTabTextSize) {
        this.maxTabTextSize = maxTabTextSize;
        setTabViews();
    }

    public int getVisiableCounts() {
        return visiableCounts;
    }

    public int getTabWidth() {
        return mTabWidth;
    }

    /**
     * ����tab�Ŀ��
     *
     * @param mTabWidth
     */
    public void setTabWidth(int mTabWidth) {
        this.mTabWidth = mTabWidth;
        this.isSetTabWidth=true;
        setTabViews();
    }

    /**
     * �Ƿ�����tab����任Ч��
     *
     * @param isShowTabSizeChange
     */
    public void setShowTabSizeChange(boolean isShowTabSizeChange) {
        this.isShowTabSizeChange = isShowTabSizeChange;
    }

    public int getTabPressColor() {
        return tabPressColor;
    }

    /**
     * ����tabѡ����ɫ��Ĭ��Ϊ��ɫ#ff0000
     *
     * @param tabPressColor
     */
    public void setTabPressColor(int tabPressColor) {
        this.tabPressColor = tabPressColor;
    }

    /**
     * ����tab�ı߾�,Ĭ�϶�Ϊ0
     *
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setTabPadding(int l, int t, int r, int b) {
        this.tabPaddingLeft = l;
        this.tabPaddingRight = r;
        this.tabPaddingTop = t;
        this.tabPaddingBottom = b;
        setTabViews();
    }

    public boolean isDeuceTabWidth() {
        return isDeuceTabWidth;
    }

    /**
     * �Ƿ�ƽ��tab�Ŀ�ȣ���false���򷵻�ÿ��tab����Ӧ�Ŀ�ȣ��ʺ��ڶ�����Ŀ��ʹ��
     *
     * @param isDeuceTabWidth
     */
    public void setDeuceTabWidth(boolean isDeuceTabWidth) {
        this.isDeuceTabWidth = isDeuceTabWidth;
        setTabViews();
    }

    /**
     * ��ȡtab�����tab�ı���Ϊ��Сtab���
     *
     * @param arrStr
     * @return
     */
    private String getMaxLengthString(String[] arrStr) {
        String max = arrStr[0];
        for (int x = 1; x < arrStr.length; x++) {
            if (arrStr[x].length() > max.length())
                max = arrStr[x];
        }
        return max;
    }

    /**
     * ������ֵ
     */
    public void resetData() {
        initView();

    }

    /**
     * ָʾ��ʵ����
     *
     * @author yangpeixing
     */
    public interface DrawIndicatorCreator {
        /**
         * Ĭ��ΪԲ�Ǿ���ָʾ�����û��ɼ̳���д�Զ���ָʾ����ʽ
         *
         * @param canvas
         * @param left   tab��߾���ԭ���λ��
         * @param top    ����tab��λ��
         * @param right  tab���붥�˵�λ��
         * @param bottom ����tab�ĸ߶�,�ȿؼ��߶�
         * @param paint  ָʾ������
         * @param raduis ��ΧԲ�ǰ뾶
         */
        void drawIndicator(Canvas canvas, int left, int top, int right,
                           int bottom, Paint paint, int raduis);
    }

    DrawIndicatorCreator creator;

    /**
     * ����ָʾ����ʽ��Ĭ��ΪԲ����ʽ����������ʽ
     *
     * @param creator
     */
    public void setDrawIndicatorCreator(DrawIndicatorCreator creator) {
        this.creator = creator;
    }

    /**
     * �����ViewPager�Ļص��ӿ�
     *
     * @author yangpeixing
     */
    public interface PageChangeListener {
         void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

         void onPageSelected(int position);

         void onPageScrollStateChanged(int state);
    }

    /**
     * �����ViewPager�Ļص��ӿ�
     */
    private PageChangeListener onPageChangeListener;

    /**
     * �����ViewPager�Ļص��ӿڵ�����
     *
     * @param pageChangeListener
     */
    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            isClick = false;
            if (mCurrentIndex > visiableCounts / 2 - 1
                    && mCurrentIndex < titles.length) {
                int centerX = getTransitXByPosition(mCurrentIndex)
                        - (screenWidth - tabLengthArray[mCurrentIndex]) / 2;
                parentScrollto(centerX, 0);
            }
        }
        // �ص�
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        /*if (isDeuceTabWidth && mCurrentIndex > visiableCounts / 2 - 1
				&& mCurrentIndex < titles.length) {
			this.scrollTo((position - (visiableCounts - 2)) * mTabWidth
					+ (int) (mTabWidth * positionOffset), 0);
		}*/
        if (position + 1 != totalCount && !isClick && position < totalCount) {
            if (isShowTabSizeChange) {// �ж��Ƿ�任
                setTabSizeChange(position, 1 - positionOffset);
                setTabSizeChange(position + 1, positionOffset);
            }
            setTabColorChange(position, 1 - positionOffset);
            setTabColorChange(position + 1, positionOffset);
        }
        if (positionOffset != 0.0 && position < totalCount - 1) {
            mTransitX = (int) (tabLengthArray[position] * positionOffset + (getTransitXByPosition(position)));
            mTabWidth = (int) (tabLengthArray[position] + (tabLengthArray[position + 1] - tabLengthArray[position])
                    * positionOffset);
        }
        invalidate();
        // �ص�
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        mCurrentIndex = arg0;
        if (isClick) {
            resetTabColor();
            resetTabSize();
            getTab(arg0).setTextColor(tabPressColor);
            if (isShowTabSizeChange) {
                getTab(arg0).setTextSize(maxTabTextSize);
            } else {
                getTab(arg0).setTextSize(tabTextSize);
            }
        }
        if (arg0 == 0 && isChildOfHorizontalScrollView) {
            ((HorizontalScrollView) getParent()).scrollTo(0, 0);
        }
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(arg0);
        }
    }

}
