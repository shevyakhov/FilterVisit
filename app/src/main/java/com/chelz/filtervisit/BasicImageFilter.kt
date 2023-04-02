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

class BasicImageFilter : ImageFilter {

	var bitmap: Bitmap? = null

	override fun colorizeBlur(): RequestOptions {
		return RequestOptions.bitmapTransform(BlurTransformation(10, 2))
	}

	override fun colorizeKuhawara(): RequestOptions {
		return RequestOptions.bitmapTransform(KuwaharaFilterTransformation(25))
	}

	override fun colorizeSepia(): RequestOptions {
		return RequestOptions.bitmapTransform(SepiaFilterTransformation(10f))
	}

	override fun colorizeSketch(): RequestOptions {
		return RequestOptions.bitmapTransform(SketchFilterTransformation())
	}

	override fun colorizeSharpen(): RequestOptions {
		return RequestOptions.bitmapTransform(BrightnessFilterTransformation(10f))
	}

	override fun colorizePixel(): RequestOptions {
		return RequestOptions.bitmapTransform(PixelationFilterTransformation(20f))
	}

	override fun acceptToColorizeMain(v: ImageFilterVisitor?): RequestOptions? {
		return v?.visit(this)
	}

	override fun save() :Bitmap{
		return bitmap!!
	}

}
