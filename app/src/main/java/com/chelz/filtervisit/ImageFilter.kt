package com.chelz.filtervisit

import android.graphics.Bitmap
import com.bumptech.glide.request.RequestOptions

interface ImageFilter {

	fun colorizeBlur(): RequestOptions
	fun colorizeSharpen(): RequestOptions
	fun colorizeSepia(): RequestOptions
	fun colorizeSketch(): RequestOptions
	fun colorizeKuhawara(): RequestOptions
	fun colorizePixel(): RequestOptions
	fun save() : Bitmap
}