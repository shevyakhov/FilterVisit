package com.chelz.filtervisit

import android.graphics.Bitmap
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation
import jp.wasabeef.glide.transformations.gpu.KuwaharaFilterTransformation
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation

class PremiumImageFilter : ImageFilter {

	var bitmap: Bitmap? = null

	override fun colorizeBlur(): RequestOptions {
		return RequestOptions.bitmapTransform(BlurTransformation(5, 1))
	}

	override fun colorizeKuhawara(): RequestOptions {
		return RequestOptions.bitmapTransform(KuwaharaFilterTransformation(10))
	}

	override fun colorizeSepia(): RequestOptions {
		return RequestOptions.bitmapTransform(SepiaFilterTransformation(5f))
	}

	override fun colorizeSketch(): RequestOptions {
		return RequestOptions.bitmapTransform(SketchFilterTransformation())
	}

	override fun colorizeSharpen(): RequestOptions {
		return RequestOptions.bitmapTransform(BrightnessFilterTransformation(3f))
	}

	override fun colorizePixel(): RequestOptions {
		return RequestOptions.bitmapTransform(PixelationFilterTransformation(5f))
	}

	override fun acceptToColorizeMain(v: ImageFilterVisitor?): RequestOptions? {
		return v?.visit(this)
	}

	override fun save() :Bitmap{
		return bitmap!!
	}
}
