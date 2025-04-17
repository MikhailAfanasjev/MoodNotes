package com.example.ainotes.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.linguareader.R

fun showCustomToast(context: Context, message: String, isError: Boolean) {
    val toast = Toast(context)

    // Конвертация dp в пиксели для скругления и паддингов
    val cornerRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        16f,
        context.resources.displayMetrics
    )
    val verticalPadding = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        16f, // увеличенный вертикальный отступ
        context.resources.displayMetrics
    ).toInt()
    val horizontalPadding = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        16f,
        context.resources.displayMetrics
    ).toInt()

    // Выбор цвета фона: для ошибки — holo_red_dark, для обычного уведомления — blue
    val backgroundColor = if (isError)
        ContextCompat.getColor(context, R.color.holo_red_dark)
    else
        ContextCompat.getColor(context, R.color.blue)

    // Создание drawable с закруглёнными углами для каждого угла
    val backgroundDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        // Массив значений скругления для 4 углов: [top-left, top-right, bottom-right, bottom-left]
        // Каждая координата задаётся парами (x, y)
        cornerRadii = floatArrayOf(
            cornerRadius, cornerRadius,  // Top left
            cornerRadius, cornerRadius,  // Top right
            cornerRadius, cornerRadius,  // Bottom right
            cornerRadius, cornerRadius   // Bottom left
        )
        setColor(backgroundColor)
    }

    // Создание контейнера для Toast с увеличенным вертикальным отступом
    val container = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        background = backgroundDrawable
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
        gravity = Gravity.CENTER_VERTICAL
        // Можно дополнительно задать минимальную высоту
        minimumHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            48f, // минимальная высота в dp
            context.resources.displayMetrics
        ).toInt()
    }

    // Создание TextView для отображения сообщения
    val textView = TextView(context).apply {
        text = message
        setTextColor(Color.parseColor("#FFFFFFFF")) // Цвет текста white
        textSize = 14f
    }

    container.addView(textView)

    toast.apply {
        view = container
        duration = Toast.LENGTH_SHORT
        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
    }.show()
}