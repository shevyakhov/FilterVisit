package com.chelz.filtervisit

import android.graphics.Color
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.ColorFilterTransformation

class PremiumConfigurator : ImageFilterVisitor {

	override fun visit(filter: BasicImageFilter): RequestOptions {
		return RequestOptions.bitmapTransform(ColorFilterTransformation(Color.parseColor("#3AFF0000")))
	}

	override fun visit(filter: PremiumImageFilter): RequestOptions {
		return RequestOptions.bitmapTransform(ColorFilterTransformation(Color.parseColor("#26FFD700")))
	}
}