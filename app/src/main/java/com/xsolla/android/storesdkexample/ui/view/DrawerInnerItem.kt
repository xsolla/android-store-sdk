package com.xsolla.android.storesdkexample.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.use
import com.xsolla.android.storesdkexample.R
import kotlinx.android.synthetic.main.item_drawer.view.*

class DrawerInnerItem(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_inner_drawer, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.DrawerInnerItem, 0, 0).use {
            item_text.text = it.getString(R.styleable.DrawerInnerItem_text)
        }
    }

}