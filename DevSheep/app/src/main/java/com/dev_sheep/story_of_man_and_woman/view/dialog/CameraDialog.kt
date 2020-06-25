package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.dev_sheep.story_of_man_and_woman.R
import kotlinx.android.synthetic.main.dialog_camera.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraDialog : DialogFragment() {

    private val MY_PERMISSION_CAMERA = 1111
    private val REQUEST_TAKE_PHOTO = 2222
    private val REQUEST_TAKE_ALBUM = 3333
    private val REQUEST_IMAGE_CROP = 4444
    var mCurrentPhotoPath: String? = null
    var imageURI: Uri? = null
    var photoURI: Uri? = null
    var albumURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_camera, container, false)
        //어느 다이어로그에서 왔는지
        val bundle = arguments
        view.dialog_camera.setOnClickListener {
            //갤러리에 관한 권한을 받아오는 코드
            getAlbum()
            dismiss()
        }
        view.dialog_basic.setOnClickListener {
            //갤러리에 관한 권한을 받아오는 코드
            // 실제 카메라 구동 코드는 함수로 처리
//            captureCamera()
            dismiss()
        }

        checkPermission()
        return view
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.CAMERA
                )
            ) {
                AlertDialog.Builder(this.context).setTitle("알림").setMessage("저장소 권한이 거부되었습니다.")
                    .setNeutralButton(
                        "설정"
                    ) { dialogInterface, i ->
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package: " + this.context!!.packageName)
                        startActivity(intent)
                    }.setPositiveButton(
                        "확인"
                    ) { dialogInterface, i -> dismiss() }.setCancelable(false).create().show()
            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ),
                    MY_PERMISSION_CAMERA
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults[0] == 0) {
                Toast.makeText(this.context, "카메라 권한 승인완료", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.context, "카메라 권한 승인 거절", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                try {
                    Log.i("REQUEST_TAKE_PHOTO", "OK!!!!!!")
                    galleryAddPic()
//                    img_user.setImageURI(imageURI)
                } catch (e: Exception) {
                    Log.e("REQUEST_TAKE_PHOTO", e.toString())
                }
            } else {
                Toast.makeText(this.context, "저장공간에 접근할 수 없는 기기 입니다.", Toast.LENGTH_SHORT)
                    .show()
            }
            REQUEST_TAKE_ALBUM -> if (resultCode == Activity.RESULT_OK) {
                if (data!!.data != null) {
                    try {
                        var albumFile: File? = null
                        albumFile = createImageFile()
                        photoURI = data.data
                        albumURI = Uri.fromFile(albumFile)
                        cropImage()
                    } catch (e: IOException) {
                        Log.e("TAKE_ALBUM_SINLE_ERROR", e.toString())
                    }
                }
            }
            REQUEST_IMAGE_CROP -> if (resultCode == Activity.RESULT_OK) {
                galleryAddPic()
                //사진 변환 error
//                img_user.setImageURI(albumURI)
            }
        }
    }

    private fun getAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_TAKE_ALBUM)
    }

    private fun captureCamera() {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(this.context!!.packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (photoFile != null) {
                    val providerUri =
                        FileProvider.getUriForFile(
                            this.context!!,
                            this.context!!.packageName,
                            photoFile
                        )
                    imageURI = providerUri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerUri)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            } else {
                Toast.makeText(this.context, "접근 불가능 합니다", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        var imageFile: File? = null
        val storageDir = File(
            Environment.getExternalStorageDirectory().toString() + "/Pictures"
        )
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        imageFile = File(storageDir, imageFileName)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    //사진 crop할 수 있도록 하는 함수
    fun cropImage() {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        cropIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        cropIntent.setDataAndType(photoURI, "image/*")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("scale", true)
        cropIntent.putExtra("output", albumURI)
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
    }

    // 갤러리에 사진 추가 함수
    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(mCurrentPhotoPath)
        val contentURI = Uri.fromFile(file)
        mediaScanIntent.data = contentURI
//        sendBroadcast(mediaScanIntent)
        Toast.makeText(this.context, "앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
    }

}