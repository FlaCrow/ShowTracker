package com.flacrow.showtracker

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.roundToInt

private const val DEFAULT_TEXT = "0%"
private const val DEFAULT_PERCENTAGE_SIZE = 10f
private const val DEFAULT_INNER_PADDING = 10f
private const val CIRCLE_DEGREES = 360f

class PieChartView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {


    private val maxPercentage = 100f
    private var angle: Float = 0f
    private val rect = RectF()
    private val rectCenter = RectF()
    private val rectText = RectF()
    private val innerPaint = Paint()
    private val primaryCirclePaint = Paint()
    private val secondaryCirclePaint = Paint()
    private val textPaint = Paint()
    private var percentageSize = DEFAULT_PERCENTAGE_SIZE
    private var innerCirclePadding = DEFAULT_INNER_PADDING
    private var percentTextString = DEFAULT_TEXT
    private var animator: ValueAnimator? = null
    var percentage = 0f
        set(value) {
            field = value / maxPercentage
            angle = (CIRCLE_DEGREES * field)
            setPercentageText()
            animator = ValueAnimator.ofFloat(0f, angle).apply {
                duration = 650
                interpolator = LinearInterpolator()
                addUpdateListener { valueAnimator ->
                    angle = valueAnimator.animatedValue as Float
                    invalidate()
                }
            }
            animator?.start()
        }

    private fun setPercentageText() {
        percentTextString = if (percentage != 0f) {
            (percentage * maxPercentage).roundToInt().toString() + "%"
        } else DEFAULT_TEXT
    }

    init {

        setupAttributeParameters(context, attributeSet)
        textPaint.textSize = percentageSize * resources.displayMetrics.density
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = context.resources.getColor(R.color.white, null)
        secondaryCirclePaint.color =
            context.resources.getColor(R.color.black, null)
        innerPaint.color = context.resources.getColor(R.color.pie_chart_grey, null)


        primaryCirclePaint.isAntiAlias = true
        innerPaint.isAntiAlias = true
        secondaryCirclePaint.isAntiAlias = true
        textPaint.isAntiAlias = true

        innerPaint.style = Paint.Style.FILL
        secondaryCirclePaint.style = Paint.Style.FILL
        innerPaint.style = Paint.Style.FILL


    }


    private fun setupAttributeParameters(context: Context, attributeSet: AttributeSet) {
        val attrArray =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.PieChartView, 0, 0)
        try {

            percentageSize =
                attrArray.getFloat(
                    R.styleable.PieChartView_percentage_size,
                    DEFAULT_PERCENTAGE_SIZE
                )
            innerCirclePadding =
                attrArray.getFloat(
                    R.styleable.PieChartView_inner_pie_padding,
                    DEFAULT_INNER_PADDING
                )
            percentage = attrArray.getFloat(R.styleable.PieChartView_percentage, 0f)
        } finally {
            attrArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val top = 0f
        val left = 0f
        rect.set(left, top, left + width, top + width)
        rectCenter.set(
            left + innerCirclePadding,
            top + innerCirclePadding,
            left - innerCirclePadding + width,
            top - innerCirclePadding + width
        )
        rectText.set(rectCenter)
        rectText.offset(0f, -((textPaint.descent() + textPaint.ascent()) / 2))

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        primaryCirclePaint.color = when (percentage) {
            in 0f..0.4f -> context.getColor(R.color.pie_chart_red)
            in 0.4f..0.8f -> context.getColor(R.color.pie_chart_yellow)
            else -> context.getColor(R.color.pie_chart_green)
        }
        canvas?.drawArc(rect, -90f, CIRCLE_DEGREES, true, secondaryCirclePaint)

        if (percentage != 0f) {
            canvas?.drawArc(rect, -90f, angle, true, primaryCirclePaint)
        }
        canvas?.drawArc(rectCenter, -90f, CIRCLE_DEGREES, true, innerPaint)

        canvas?.drawText(
            percentTextString,
            rectText.centerX(),
            rectText.centerY(),
            textPaint
        )
    }

    override fun onDetachedFromWindow() {
        animator?.removeAllUpdateListeners()
        super.onDetachedFromWindow()
    }
}