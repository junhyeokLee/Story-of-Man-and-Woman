package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.adapter.ProfileViewpagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.ImageDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment: Fragment(),View.OnClickListener {

    private var menu : Menu? = null
    private val testViewModel: TestViewModel by viewModel()
    private val MY_PERMISSION_CAMERA = 1111
    private val REQUEST_TAKE_PHOTO = 2222
    private val REQUEST_TAKE_ALBUM = 3333
    private val REQUEST_IMAGE_CROP = 4444
    var mCurrentPhotoPath: String? = null
    var imageURI: Uri? = null
    var photoURI: Uri? = null
    var albumURI: Uri? = null

    var collapsingToolbar : CollapsingToolbarLayout? = null
    var appBarLayout : AppBarLayout? = null
    var toolbar : Toolbar? = null
    var viewPagerAdapter: ProfileViewpagerAdapter? = null
    var messageBtn : ImageView? = null
    var recyclerView : RecyclerView? = null
    var progressBar : ProgressBar? = null
    var layoutManager: GridLayoutManager? = null
    var profileImage: com.mikhaellopez.circularimageview.CircularImageView? = null
    var profileAdd : ImageView? = null
    var backgroundAdd: ImageView? = null
    var viewpager : ViewPager? = null
    var tablayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile,null)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar)
        collapsingToolbar = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        messageBtn = view.findViewById<ImageView>(R.id.message_btn)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        layoutManager = GridLayoutManager(view.context, 1)
        profileImage = view.findViewById<com.mikhaellopez.circularimageview.CircularImageView>(R.id.id_Profile_Image)
        profileAdd = view.findViewById<ImageView>(R.id.id_Profile_add)
        backgroundAdd = view.findViewById<ImageView>(R.id.id_ProfileBackgorund_add)
        viewpager = view.findViewById(R.id.viewPager) as ViewPager
        tablayout = view.findViewById(R.id.tabLayout) as TabLayout
        recyclerView?.layoutManager = layoutManager
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        collapsingToolbarInit()

        tablayout?.apply {
            addTab(this.newTab().setIcon(R.drawable.ic_write))
            addTab(this.newTab().setIcon(R.drawable.ic_lock_empty))
            addTab(this.newTab().setIcon(R.drawable.ic_bookmark_hollow))
        }

        viewPagerAdapter = ProfileViewpagerAdapter(childFragmentManager,tablayout!!.tabCount)
        viewpager?.adapter = viewPagerAdapter
        viewpager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

        tablayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager?.setCurrentItem(tab!!.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                viewpager?.setCurrentItem(tab!!.position)
            }
            override fun onTabSelected(tab: TabLayout.Tab?)
            {
                if(tab!!.position == 0){
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_lock_empty)
                    tablayout?.getTabAt(2)?.setIcon(R.drawable.ic_bookmark_hollow)
                }else if(tab!!.position == 1){
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write_empty)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_lock)
                    tablayout?.getTabAt(2)?.setIcon(R.drawable.ic_bookmark_hollow)
                }else if(tab!!.position == 2){
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write_empty)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_lock_empty)
                    tablayout?.getTabAt(2)?.setIcon(R.drawable.ic_bookmark_filled)
                }
                viewpager?.setCurrentItem(tab!!.position)
            }
        })

        profileImage?.setImageResource(R.mipmap.user)
        profileImage?.setOnClickListener(this)
        profileAdd?.setOnClickListener(this)
//        val manager: FragmentManager? = activity?.supportFragmentManager


//        testViewModel.getListPokemon().observe(this, Observer {
//            val pokemons: List<Test> = it
//            recyclerView?.adapter = TestAdapter(pokemons, view.context,childFragmentManager)
//
//            if (pokemons.isNotEmpty()) {
//                progressBar?.visibility = View.GONE
//            }else {
//                progressBar?.visibility = View.VISIBLE
//            }
//
//        })



        return view
    }


    private fun collapsingToolbarInit(){

        collapsingToolbar.apply {
            this?.setExpandedTitleColor((activity as AppCompatActivity).getColor(R.color.color_primary_text))
            this?.setCollapsedTitleTextColor((activity as AppCompatActivity).getColor(R.color.color_primary_text))
            this?.setCollapsedTitleTextAppearance(R.style.CollapsingTitleStyle)
            this?.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        }

        appBarLayout.apply {
            var isShow = true
            var scrollRange = -1
            this?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                if (scrollRange == -1){
                    scrollRange = barLayout?.totalScrollRange!!
                }
                showAndHideOption(R.id.action_info,scrollRange + verticalOffset)
            })
        }
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this.context!!,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
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
                        intent.data = Uri.parse("package: " + context!!.packageName )
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.menu_scrolling,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId

        if(id == R.id.action_info){
            return true
        }
        return super.onOptionsItemSelected(item)
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
            REQUEST_TAKE_PHOTO ->{
                if(requestCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK!!!!!!")
                        galleryAddPic()
                        id_Profile_Image.setImageURI(imageURI)
                    } catch (e: Exception) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString())
                    }
                }else{
                    Toast.makeText(this.context, "저장공간에 접근할 수 없는 기기 입니다.", Toast.LENGTH_SHORT)
                        .show()

                }
            }

            REQUEST_TAKE_ALBUM ->{
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.data != null) {
                        try {
                            var albumFile: File? = null
                            albumFile = createImageFile()
                            photoURI = data!!.data
                            albumURI = Uri.fromFile(albumFile)
                            cropImage()
                        } catch (e: IOException) {
                            Log.e("TAKE_ALBUM_SINLE_ERROR", e.toString())
                        }
                    }
                }
            }

            REQUEST_IMAGE_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic()
                    //사진 변환 error
                    id_Profile_Image.setImageURI(albumURI)
                }
            }
        }

    }

    private fun showAndHideOption(id: Int,vertical: Int) {
        if(vertical > 150){
            if(menu != null) {
                collapsingToolbar?.title = ""
                val item = menu!!.findItem(id)
                item.isVisible = false
            }
        }else if(vertical < 150) {
            if (menu != null) {
                collapsingToolbar?.title = "junhyeoklee616"
                val item = menu!!.findItem(id)
                item.isVisible = true
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.id_Profile_add -> {
                getAlbum()
            }
            R.id.id_Profile_Image -> {
                Log.e("dwqdwq","dwqdwq");
                ImageDialog(context!!,R.mipmap.user).start("dqwq")

            }
        }
//            val dialog = CameraDialog()
//        dialog.show(requireFragmentManager(), "")
//            dialog.show(fragmentManager!!, dialog.tag)
//
//
//            val pop = PopupMenu(context!!, view!!)
//            MenuInflater(context).inflate(R.menu.menu_camera, pop.menu)
//
//            pop.setOnMenuItemClickListener { item ->
//                when(item.itemId){
//                        // 실제 카메라 구동 코드는 함수로 처리
//                        R.id.one -> {
//                            captureCamera()
//                        }
//                        //갤러리에 관한 권한을 받아오는 코드
//                        R.id.two -> {
//                            getAlbum()
//                        }
//                        //기본이미지
//                        R.id.three -> {
//                            profileImage?.setImageResource(R.mipmap.ic_launcher)
//                        }
//                }
//                true
//            }
//            pop.gravity = Gravity.CENTER
//            pop.show()
//                checkPermission()

    }
}

