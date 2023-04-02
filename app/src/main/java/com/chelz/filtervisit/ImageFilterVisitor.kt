package com.chelz.filtervisit

import android.graphics.Bitmap
import com.bumptech.glide.request.RequestOptions

interface ImageFilterVisitor {

	fun visit(filter: BasicImageFilter): RequestOptions
	fun visit(filter: PremiumImageFilter):RequestOptions
}
