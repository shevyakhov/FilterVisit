package com.chelz.filtervisit

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chelz.filtervisit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private var isPremium = false
	private lateinit var bitmap: Bitmap
	var premiumImageFilter = PremiumImageFilter()
	var basicImageFilter = BasicImageFilter()
	var premiumConfigurator = PremiumConfigurator()

	private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
		if (uri != null) {
			val byteArray = this.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
			basicImageFilter.bitmap = bitmap
			binding.mainImage.setImageBitmap(bitmap)
			premiumImageFilter.bitmap = bitmap
		}
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		bitmap = getDrawable(R.drawable.none)?.toBitmap()!!
		basicImageFilter.bitmap = getDrawable(R.drawable.none)?.toBitmap()!!
		premiumImageFilter.bitmap = getDrawable(R.drawable.none)?.toBitmap()!!
		setListeners()
	}

	private fun setListeners() {
		binding.save.setOnClickListener {
			val bitmap = binding.mainImage.bitmap
			if (bitmap != null) {
				CoroutineScope(Dispatchers.Main).launch {
					try {
						saveMediaToStorage(bitmap)
					} catch (_: IOException) {

					}
				}
			}
		}
		binding.isPremium.setOnClickListener {
			showDialog()
		}
		binding.mainImage.setOnClickListener {
			pickImageLauncher.launch("image/*")
		}
		binding.mainFilter.setOnClickListener {
			mainFilter(binding.mainImage)
		}
		binding.None.setOnClickListener {
			noneFilter(binding.mainImage)
		}
		binding.blurFilter.setOnClickListener {
			blurFilter(binding.mainImage)
		}
		binding.pixelFilter.setOnClickListener {
			pixelFilter(binding.mainImage)
		}
		binding.sepia.setOnClickListener {
			sepiaFilter(binding.mainImage)
		}
		binding.sketch.setOnClickListener {
			sketchFilter(binding.mainImage)
		}
		binding.kuwahara.setOnClickListener {
			kuwaharaFilter(binding.mainImage)
		}
	}

	private fun kuwaharaFilter(image: ImageView) {
		initLoadingState(image)
		val job = CoroutineScope(Dispatchers.IO).async {
			if (isPremium) {
				premiumImageFilter.colorizeKuhawara()
			} else
				basicImageFilter.colorizeKuhawara()
		}
		awaitJobToLoad(job, image)
	}

	private fun sketchFilter(image: ImageView) {
		initLoadingState(image)
		val job = CoroutineScope(Dispatchers.IO).async {
			if (isPremium)
				premiumImageFilter.colorizeSketch()
			else
				basicImageFilter.colorizeSketch()
		}
		awaitJobToLoad(job, image)
	}

	private fun sepiaFilter(image: ImageView) {
		initLoadingState(image)
		val job = CoroutineScope(Dispatchers.IO).async {
			if (isPremium)
				premiumImageFilter.colorizeSepia()
			else
				basicImageFilter.colorizeSepia()
		}
		awaitJobToLoad(job, image)
	}

	private fun pixelFilter(image: ImageView) {
		initLoadingState(image)
		val job = CoroutineScope(Dispatchers.IO).async {
			if (isPremium)
				premiumImageFilter.colorizePixel()
			else
				basicImageFilter.colorizePixel()
		}
		awaitJobToLoad(job, image)
	}

	private fun blurFilter(image: ImageView) {
		initLoadingState(image)
		val job = CoroutineScope(Dispatchers.IO).async {
			if (isPremium)
				premiumImageFilter.colorizeBlur()
			else
				basicImageFilter.colorizeBlur()
		}
		awaitJobToLoad(job, image)
	}

	private fun noneFilter(image: ImageView) {
		CoroutineScope(Dispatchers.Main).launch {
			Glide.with(this@MainActivity)
				.load(bitmap)
				.into(image)
		}
	}

	private fun mainFilter(image: ImageView) {
		initLoadingState(image)
		val job = CoroutineScope(Dispatchers.IO).async {
			if (isPremium)
				premiumImageFilter.acceptToColorizeMain(premiumConfigurator)
			else
				basicImageFilter.acceptToColorizeMain(premiumConfigurator)
		}
		awaitJobToLoad(job as Deferred<RequestOptions>, image)
	}

	private fun awaitJobToLoad(job: Deferred<RequestOptions>, image: ImageView) {
		CoroutineScope(Dispatchers.Main).launch {
			val bm = job.await()
			Glide.with(this@MainActivity)
				.load(bitmap)
				.apply(bm)
				.into(image)
		}
	}

	val ImageView.bitmap: Bitmap? get() = (drawable as? BitmapDrawable)?.bitmap
	private fun initLoadingState(image: ImageView) {
		CoroutineScope(Dispatchers.Main).launch {
			Glide.with(applicationContext)
				.load(getDrawable(R.drawable.loading))
				.into(image)
		}
	}

	private fun showDialog() {
		val builder: AlertDialog.Builder = AlertDialog.Builder(this)
		builder.setTitle("Премиум подписка")
		val input = EditText(this)
		input.hint = "Введите промокод"
		input.inputType = InputType.TYPE_CLASS_TEXT
		builder.setView(input)

		builder.setPositiveButton("OK") { dialog, which ->
			var m_Text = input.text.toString()
			isPremium = m_Text == "Хочу автомат по ооап" || m_Text == "shevyakhov"
			binding.isPremium.isCheckable = isPremium
			binding.isPremium.isChecked = isPremium
			if (!isPremium) {
				binding.mainFilter.setImageResource(R.drawable.red_filter)
			} else
				binding.mainFilter.setImageResource(R.drawable.gold)
		}
		builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
		builder.show()
	}

	private fun saveMediaToStorage(bitmap: Bitmap) {
		val filename = "${System.currentTimeMillis()}.jpg"
		var fos: OutputStream? = null
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			this.contentResolver?.also { resolver ->
				val contentValues = ContentValues().apply {
					put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
					put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
					put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
				}
				val imageUri: Uri? =
					resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
				fos = imageUri?.let { resolver.openOutputStream(it) }
			}
		} else {
			val imagesDir =
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			val image = File(imagesDir, filename)
			fos = FileOutputStream(image)
		}

		fos?.use {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
			Toast.makeText(this@MainActivity, "Сохранено", Toast.LENGTH_SHORT).show()
		}
	}
}