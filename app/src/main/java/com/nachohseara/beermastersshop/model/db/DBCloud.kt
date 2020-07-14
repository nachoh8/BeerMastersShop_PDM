package com.nachohseara.beermastersshop.model.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import java.io.ByteArrayOutputStream
import java.lang.Exception

class DBCloud(private val connector: IFBConnector) {
    companion object {
        const val TAG = "DBCloud"
        const val FOLDER = "Products"

        const val UPLOAD_IMG_OK = 0
        const val UPLOAD_IMG_FAILURE = 1
        const val GET_URL_FAILURE = 2
        const val IMG_DELETE_FAILURE = 3
        const val GET_IMAG_OK = 4
        const val GET_IMAG_FAILURE = 5
    }
    private val db = FirebaseStorage.getInstance()
    private val baseRef = db.reference

    fun uploadImg(bitmap: Bitmap, prodId: String) {
        val imgRef = baseRef.child("$FOLDER/$prodId")
        val data = toUploadData(bitmap)
        val uploadTask = imgRef.putBytes(data)
            .addOnFailureListener {e->
                connector.onFailureFB(UPLOAD_IMG_FAILURE, e.message.toString())
                Log.d(TAG, e.toString())
            }.addOnSuccessListener {taskSnapshot ->
                //connector.onSuccessFB(UPLOAD_IMG_OK, null)
                Log.d(TAG, "Upload Image complete")
            }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imgRef.downloadUrl
        }.addOnSuccessListener {uri ->
            connector.onSuccessFB(UPLOAD_IMG_OK, uri)
            Log.d(TAG, "Download url get")
        }.addOnFailureListener { exception ->
            connector.onFailureFB(GET_URL_FAILURE, exception.message.toString())
            Log.d(TAG, exception.toString())
        }
    }

    private fun toUploadData(bitmap: Bitmap) : ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos)

        return baos.toByteArray()
    }

    /*fun downloadImg(prodId: String) {
        Log.d(TAG, "START")
        val imgRef = baseRef.child("$FOLDER/$prodId")
        val ONE_MEGABYTE: Long = 1024 * 1024
        try {
            imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {bytes ->
                var bm: Bitmap? = null
                if (bytes != null && bytes.isNotEmpty()) {
                    bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
                connector.onSuccessFB(GET_IMAG_OK, bm)
                Log.d(TAG, "Img $prodId downloaded succesfully")
            }.addOnFailureListener {exception ->
                connector.onFailureFB(GET_IMAG_FAILURE, exception.message.toString())
                Log.d(TAG, exception.toString())
            }
        } catch (e: StorageException) {
            connector.onFailureFB(GET_IMAG_FAILURE, e.message.toString())
            Log.d(TAG, e.toString())
        }
    }*/

    fun deleteImg(prodId: String) {
        val imgRef = baseRef.child("$FOLDER/$prodId")
        imgRef.delete().addOnSuccessListener {
            Log.d(TAG, "Image $prodId delete")
        }.addOnFailureListener { exception ->
            connector.onFailureFB(IMG_DELETE_FAILURE, exception.message.toString())
            Log.d(TAG, exception.toString())
        }
    }

    /*fun uploadImg(uri: Uri) {
        Log.d(TAG, uri.toString())
        val file = Uri.fromFile(File(uri.path!!))
        val imgRef = baseRef.child("$FOLDER/${file.lastPathSegment}")
        val uploadTask = imgRef.putFile(file)
            .addOnFailureListener {e->
                connector.onFailureFB(UPLOAD_FAILURE, e.message.toString())
                Log.d(TAG, e.toString())
            }.addOnSuccessListener {taskSnapshot ->
                Log.d(TAG, "Upload complete")
            }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imgRef.downloadUrl
        }.addOnSuccessListener { uri->
            connector.onSuccessFB(UPLOAD_OK, uri)
            Log.d(TAG, "Download url get")
        }.addOnFailureListener { exception ->
            connector.onFailureFB(GET_URL_FAILURE, exception.message.toString())
            Log.d(TAG, exception.toString())
        }
    }*/
}