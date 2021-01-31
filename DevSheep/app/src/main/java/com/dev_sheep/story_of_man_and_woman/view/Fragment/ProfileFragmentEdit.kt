package com.dev_sheep.story_of_man_and_woman.view.Fragment
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import com.dev_sheep.story_of_man_and_woman.utils.RedDotImageView
import com.dev_sheep.story_of_man_and_woman.view.dialog.ImageDialog
import com.dev_sheep.story_of_man_and_woman.view.dialog.ProfileEditDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragmentEdit(my_m_seq:String): Fragment(),View.OnClickListener {

    private val memberViewModel: MemberViewModel by viewModel()
    private val MY_PERMISSION_CAMERA = 1111
    private val REQUEST_TAKE_PHOTO = 2222
    private val REQUEST_TAKE_PHOTO_BACKGROUND = 6666
    private val REQUEST_TAKE_ALBUM = 3333
    private val REQUEST_TAKE_ALBUM_BACKGROUND = 4444
    private val REQUEST_IMAGE_CROP = 5555
    private val REQUEST_IMAGE_CROP_BACKGROUND = 7777


    companion object{
         val REQUEST_INTRO = 1
         val REQUEST_GENDER = 2
         val REQUEST_AGE = 3
    }

    var mCurrentPhotoPath: String? = null
    var imageURI: Uri? = null
    var photoURI: Uri? = null
    var albumURI: Uri? = null
    var albumFile: File? = null
    var toolbar : Toolbar? = null
    var layoutManager: GridLayoutManager? = null
    var profileAdd : ImageView? = null
    var backgroundAdd: ImageView? = null
    var m_seq = my_m_seq // 나의 시퀀스 가져오기

    lateinit var email: String
    lateinit var profile_img: String
    lateinit var profileImage: ImageView
    lateinit var profileBackground: ImageView
    lateinit var profileNickname: TextView
    lateinit var profileEmail: TextView
    lateinit var gender : TextView
    lateinit var age : TextView
    lateinit var intro : TextView
    lateinit var iv_back : ImageView
    lateinit var tv_save_profile : TextView

    lateinit var layoutIntro : LinearLayout
    lateinit var layoutGender : LinearLayout
    lateinit var layoutAge : LinearLayout

    var resultCallProfileImg: Call<Member>? = null
    var resultCallProfileBackgroundImg: Call<Member>? = null
    var mMutiparBody_profile_img : MultipartBody.Part? = null
    var mMutiparBody_profile_background_img : MultipartBody.Part? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.activity?.let { ActivityCompat.postponeEnterTransition(it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = androidx.transition.TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_profile_edit, null)
        layoutManager = GridLayoutManager(view.context, 1)
        profileImage = view.findViewById<CircleImageView>(R.id.id_Profile_Image)
        profileBackground = view.findViewById<ImageView>(R.id.id_ProfileBackground_Image)
        profileAdd = view.findViewById<ImageView>(R.id.id_Profile_add)
        backgroundAdd = view.findViewById<ImageView>(R.id.id_ProfileBackgorund_add)
        iv_back = view.findViewById(R.id.iv_back)
        tv_save_profile = view.findViewById(R.id.tv_save_profile)
        profileNickname = view.findViewById(R.id.tv_nickname)
        profileEmail = view.findViewById(R.id.tv_email)
        gender = view.findViewById(R.id.tv_gender)
        age = view.findViewById(R.id.tv_age)
        intro = view.findViewById(R.id.tv_intro)

        layoutIntro = view.findViewById(R.id.layout_intro)
        layoutAge = view.findViewById(R.id.layout_age)
        layoutGender = view.findViewById(R.id.layout_gender)

        layoutIntro.setOnClickListener(this)
        layoutAge.setOnClickListener(this)
        layoutGender.setOnClickListener(this)
        iv_back?.setOnClickListener(this)
        tv_save_profile.setOnClickListener(this)
        profileImage?.setOnClickListener(this)
        profileAdd?.setOnClickListener(this)
        backgroundAdd?.setOnClickListener(this)


        initData()

        return view
    }


    private fun initData(){

        val single = MEMBER_SERVICE.getMember(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                profileNickname.text = it.nick_name.toString()
                profileEmail.text = it.email.toString()
                gender.text = it.gender.toString()
                age.text = it.age.toString()
                if(it.memo.toString().equals("null")){
                    intro.text = ""
                }else{
                    intro.text = it.memo.toString()
                }

                if(it.profile_img == "null"){
                    profile_img = "http://storymaw.com/data/member/user.png"
                    Glide.with(this)
                        .load(profile_img)
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profileImage)
                }
                else if(it.profile_img == null){
                    profile_img = "http://storymaw.com/data/member/user.png"
                    Glide.with(this)
                        .load(profile_img)
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profileImage)
                }
                else {
                    profile_img = it.profile_img!!
                    Glide.with(this)
                        .load(profile_img)
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profileImage)
                }

                if (it.background_img != null) {
                    Glide.with(this)
                        .load(it.background_img)
                        .placeholder(android.R.color.transparent)
                        .into(profileBackground)
                }

            }, {
                Log.e("실패 Get Member", "" + it.message)
            })

    }


    override fun onResume() {
        super.onResume()

        initData()

    }


    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(
                this.context!!,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
        {
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
                        intent.data = Uri.parse("package: " + context!!.packageName)
                        startActivity(intent)
                    }.setCancelable(false).create().show()
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
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 0){
            if(grantResults[0] == 0){
                Toast.makeText(context, "카메라 권한 승인완료", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "카메라 권한 승인 거절", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_TAKE_PHOTO -> {
                if (requestCode == Activity.RESULT_OK) {
                    try {
                        Log.e("REQUEST_TAKE_PHOTO", "OK!!!!!!")
//                        galleryAddPic()
                    } catch (e: Exception) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString())
                    }
                    refreshFragment(this, getFragmentManager()!!)
                } else {
                    Toast.makeText(this.context, "저장공간에 접근할 수 없는 기기 입니다.", Toast.LENGTH_SHORT)
                        .show()

                }
            }

            REQUEST_TAKE_PHOTO_BACKGROUND -> {
                if (requestCode == Activity.RESULT_OK) {
                    try {
                        Log.e("REQUEST_TAKE_PHOTO BACKGROUND", "OK!!!!!!")
//                        galleryAddPic()
                    } catch (e: Exception) {
                        Log.e("REQUEST_TAKE_PHOTO BACKGROUND", e.toString())
                    }
                    refreshFragment(this, getFragmentManager()!!)
                } else {
                    Toast.makeText(this.context, "저장공간에 접근할 수 없는 기기 입니다.", Toast.LENGTH_SHORT)
                        .show()

                }
            }

            REQUEST_TAKE_ALBUM -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.data != null) {
                        try {
                            albumFile = createImageFile()
                            photoURI = data!!.data
                            albumURI = Uri.fromFile(albumFile)

                            cropImage(REQUEST_IMAGE_CROP)
                        } catch (e: IOException) {
                            Log.e("TAKE_ALBUM_SINLE_ERROR", e.toString())
                        }
                    }
                    refreshFragment(this, getFragmentManager()!!)
                }
            }

            REQUEST_TAKE_ALBUM_BACKGROUND -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.data != null) {
                        try {
                            albumFile = createImageFile()
                            photoURI = data!!.data
                            albumURI = Uri.fromFile(albumFile)
                            cropImage(REQUEST_IMAGE_CROP_BACKGROUND)
                        } catch (e: IOException) {
                            Log.e("TAKE_ALBUM_SINLE_ERROR", e.toString())
                        }
                    }
                    refreshFragment(this, getFragmentManager()!!)
                }
            }

            REQUEST_IMAGE_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    val requestFile: RequestBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"), albumFile)
                    mMutiparBody_profile_img = MultipartBody.Part.createFormData("uploaded_file", albumFile?.name.toString(), requestFile)

                    resultCallProfileImg = MEMBER_SERVICE.uploadProfile(tv_email.text.toString(), mMutiparBody_profile_img)
                    resultCallProfileImg!!.enqueue(object : retrofit2.Callback<Member?> {
                        override fun onResponse(call: Call<Member?>, response: Response<Member?>) {

                            memberViewModel.editProfileImg(
                                m_seq,
                                "http://www.storymaw.com/data/member/" + tv_email.text.toString() + "/" + albumFile?.name.toString()
                            )
                            profileImage.setImageURI(albumURI)

                        }

                        override fun onFailure(call: Call<Member?>, t: Throwable) {
                            Log.e("에러", t.message)
                        }

                    })

//                    profileImage.setImageURI(albumURI)

                }

            }

            REQUEST_IMAGE_CROP_BACKGROUND -> {
                if (resultCode == Activity.RESULT_OK) {
//                    galleryAddPic()
                    //사진 변환 error
                    val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), albumFile)
                    mMutiparBody_profile_background_img = MultipartBody.Part.createFormData("uploaded_file", albumFile?.name.toString(), requestFile)

                    resultCallProfileBackgroundImg = MEMBER_SERVICE.uploadProfile(tv_email.text.toString(), mMutiparBody_profile_background_img)
                    resultCallProfileBackgroundImg!!.enqueue(object : retrofit2.Callback<Member?> {
                        override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                            Log.e("성공함", response.toString())
                            Log.e("filename", albumFile?.name.toString())
                            memberViewModel.editProfileBackgroundImg(
                                m_seq,
                                "http://www.storymaw.com/data/member/" + tv_email.text.toString() + "/" + albumFile?.name.toString()
                            )
                            profileBackground.setImageURI(albumURI)

                        }

                        override fun onFailure(call: Call<Member?>, t: Throwable) {
                            Log.e("에러", t.message)
                        }
                    })
                }
            }
        }
    }



    private fun getAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_TAKE_ALBUM)
    }
    private fun getBackGroundAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_TAKE_ALBUM_BACKGROUND)
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
                            this.context!!.packageManager.toString(),
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
    fun cropImage(type: Int) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        cropIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        cropIntent.setDataAndType(photoURI, "image/*")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("scale", true)
        cropIntent.putExtra("output", albumURI)

        if(type == REQUEST_IMAGE_CROP) {
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP)
        }else{
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP_BACKGROUND)
        }
    }

    // 갤러리에 사진 추가 함수
//    private fun galleryAddPic() {
//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        val file = File(mCurrentPhotoPath)
//        val contentURI = Uri.fromFile(file)
//        mediaScanIntent.data = contentURI
////        sendBroadcast(mediaScanIntent)
//        Toast.makeText(this.context, "앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show()
//    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.id_Profile_add -> {
                getAlbum()
            }
            R.id.id_ProfileBackgorund_add -> {
                getBackGroundAlbum()
            }
            R.id.id_Profile_Image -> {
                ImageDialog(context!!,profile_img).start("")
            }

            R.id.layout_intro -> {
                val dialog = ProfileEditDialog(REQUEST_INTRO,intro.text.toString(),object :ProfileEditDialog.OnClickMemoSaveListener{
                    override fun OnClickSaveMemo(tv: TextView) {
                        intro.text = tv.text
                    }

                },object :ProfileEditDialog.OnClickAgeSaveListener{
                    override fun OnClickSaveAge(s: String) {
                    }

                },object :ProfileEditDialog.OnClickGenderSaveListener{
                    override fun OnClickSaveGender(s: String) {
                    }

                })
                dialog.show(childFragmentManager, dialog.tag)
            }
            R.id.layout_gender -> {
                val dialog = ProfileEditDialog(REQUEST_GENDER,gender.text.toString(),object :ProfileEditDialog.OnClickMemoSaveListener{
                    override fun OnClickSaveMemo(tv: TextView) {
                    }

                },object :ProfileEditDialog.OnClickAgeSaveListener{

                    override fun OnClickSaveAge(s: String) {
                    }

                },object :ProfileEditDialog.OnClickGenderSaveListener{
                    override fun OnClickSaveGender(s: String) {
                        gender.text = s
                    }

                })
                dialog.show(childFragmentManager, dialog.tag)
            }
            R.id.layout_age ->{
                val dialog = ProfileEditDialog(REQUEST_AGE,age.text.toString(),object :ProfileEditDialog.OnClickMemoSaveListener{
                    override fun OnClickSaveMemo(tv: TextView) {
                    }

                },object :ProfileEditDialog.OnClickAgeSaveListener{
                    override fun OnClickSaveAge(s: String) {
                        age.text = s
                    }

                },object :ProfileEditDialog.OnClickGenderSaveListener{
                    override fun OnClickSaveGender(s: String) {
                    }

                })
                dialog.show(childFragmentManager, dialog.tag)
            }

            R.id.iv_back -> {
                activity?.onBackPressed()
            }

            R.id.tv_save_profile -> {
                memberViewModel.updateProfile(m_seq,intro.text.toString(),gender.text.toString(),age.text.toString())
                Toast.makeText(context, "저장완료", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }



}

