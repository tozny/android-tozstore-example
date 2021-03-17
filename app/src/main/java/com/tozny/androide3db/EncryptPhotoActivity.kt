package com.tozny.androide3db

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PHOTO_PATH = "com.tozny.androidE3DB.encrypt_photo_path"

class EncryptPhotoActivity : AppCompatActivity() {
    val clients = ClientGenerator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypt_photo)

    }

    fun dispatchTakePictureIntent(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("files", "could not create file to store image", ex)
                    return
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.tozny.androide3db",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val findViewById = findViewById<ImageView>(R.id.bigPicture)
            val filePath = intent.extras?.getString(PHOTO_PATH)
            findViewById.setImageBitmap(scalePhoto(filePath)
            )

        }
    }

    private fun scalePhoto(filePath: String?): Bitmap? {
        val imageView = findViewById<ImageView>(R.id.bigPicture)
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        return BitmapFactory.decodeFile(filePath, bmOptions)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(filesDir, "images")
        if (storageDir.exists().not()) {
            storageDir.mkdirs()
        }
        val tempFile = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        return tempFile.apply {
            // Save a file: path for use with ACTION_VIEW intents
            intent.putExtra(PHOTO_PATH, absolutePath)
        }

    }

    fun deleteLocalPhotos(view: View) {
        deleteLocalPhotos()
    }

    private fun deleteLocalPhotos() {
        val imageView = findViewById<ImageView>(R.id.bigPicture)
        imageView.setImageDrawable(null)
        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        for (listFile in externalFilesDir?.listFiles()!!) {
            listFile.delete()
        }
    }

    fun encryptCurrentPhoto(view: View) {
        val path = intent.extras?.getString(PHOTO_PATH)
        if (path != null) {

            clients.e3dbClient.value?.let {
                it.writeFile("demo-photo", File(path), null) { result ->
                    if (result.isError) {
                        Toast.makeText(
                            applicationContext,
                            "Failed to encrypt and upload please try again",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("EncryptTextActivity", "Failed to encrypt and upload", result?.asError()?.other())
                    } else {
                        Toast.makeText(applicationContext, "Photo encrypted and uploaded", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }


}
