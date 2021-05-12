package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import kotlinx.android.synthetic.main.activity_feed_write.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream


class MystoryActivity : AppCompatActivity() {


    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()
    private val REQ_CODE_SELECT_IMAGE = 1001
    lateinit var TAG_SEQ : String
    lateinit var TAG_NAME : String
    lateinit var EMAIL : String
    lateinit var M_SEQ: String
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_write)
        toolbar_write.setTitle("")
        setSupportActionBar(toolbar_write)


        // 저장된 m_seq 가져오기
        val getMSEQ = getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
        M_SEQ = getMSEQ.getString("inputMseq", null)

        val getEMAIL = getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)
        EMAIL = getEMAIL.getString("inputEmail", null)

        if (intent.hasExtra("type")) {
            richwysiwygeditor.typeValue = intent.getStringExtra("type")

            if(richwysiwygeditor.typeValue == "public"){
                richwysiwygeditor.layout_iv_lock_open.visibility = View.VISIBLE
                richwysiwygeditor.layout_iv_lock.visibility = View.GONE
                richwysiwygeditor.layout_iv_lock_sub_open.visibility = View.GONE

            }else if(richwysiwygeditor.typeValue == "subscriber"){
                richwysiwygeditor.layout_iv_lock_sub_open.visibility = View.VISIBLE
                richwysiwygeditor.layout_iv_lock.visibility = View.GONE
                richwysiwygeditor.layout_iv_lock_open.visibility = View.GONE
            }else if(richwysiwygeditor.typeValue == "private"){
                richwysiwygeditor.layout_iv_lock.visibility = View.VISIBLE
                richwysiwygeditor.layout_iv_lock_open.visibility = View.GONE
                richwysiwygeditor.layout_iv_lock_sub_open.visibility = View.GONE

            }

        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }



        Log.e("TypeValue = ",""+richwysiwygeditor.typeValue)

//        if(intent.hasExtra("tag_seq")){
//            TAG_SEQ = intent.getStringExtra("tag_seq")
//        }else{
//            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
//
//        }
//        if(intent.hasExtra("tag_name")){
//            TAG_NAME = intent.getStringExtra("tag_name")
//            richwysiwygeditor.tagName.text = "# "+TAG_NAME
//        }else{
//            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
//        }
        memberViewModel.getMember(M_SEQ)
        memberViewModel.memberLivedata.observe(this, androidx.lifecycle.Observer {
            tv_creater.text = it.nick_name
            tv_gender.text = it.gender
            tv_age.text = it.age
            if(it.profile_img.toString().equals("null")){
                Glide.with(this)
                    .load("http://storymaw.com/data/member/user.png")
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .error(R.drawable.error_loading)
                    .into(img_profile)
            }else {
                Glide.with(this)
                    .load(it.profile_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .error(R.drawable.error_loading)
                    .into(img_profile)
            }
        })

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // 글 작성할때는 이미지 클릭 제어
        richwysiwygeditor.content.feedActivityType = 999


        richwysiwygeditor.getContent()
            .setEditorFontSize(16)
            .setEditorPadding(16, 16, 16, 8)

        richwysiwygeditor.getContent().setPlaceholder("어떤 이야기를 나누고 싶으세요?")

        richwysiwygeditor.getContent().focusEditor()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val images =
                ImagePicker.getImages(data)

            insertImages(images)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun insertImages(images: List<Image>?) {

        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences = getSharedPreferences("m_seq", Context.MODE_PRIVATE)
        val m_seq = preferences.getString("inputMseq", "")

        if (images == null) return
        val stringBuffer = StringBuilder()
        var i = 0
        val l = images.size
        while (i < l) {
            //파일 생성
            //img_url은 이미지의 경로
            val file = File(images.get(i).path)
//            val reFile = saveBitmapToFile(file)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body =
                MultipartBody.Part.createFormData("uploaded_file", file?.name.toString(), requestFile)


            val resultCall: Call<Feed> = FEED_SERVICE.uploadImage(EMAIL,body)
            resultCall.enqueue(object : Callback<Feed?> {
                override fun onResponse(
                    call: Call<Feed?>,
                    response: Response<Feed?>
                ) {

//                    Log.e("성공함",response.toString())
//                    Log.e("filename",file.name)
                    if(response.isSuccessful) {
                        stringBuffer.append(file?.name).append("\n")

                        richwysiwygeditor.getContent().insertImage(
                            "http://www.storymaw.com/data/feed/" + EMAIL + "/" + file?.name,
                            file?.name
                        )
//                        increaseCount()

                    }
                }
                override fun onFailure(
                    call: Call<Feed?>,
                    t: Throwable
                ) {
                    Toast.makeText(applicationContext, "이미지 업로드 실패.", Toast.LENGTH_SHORT).show()

                    Log.e("에러",t.message)
                }
            })


            i++

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        val positionOfMenuItem = 0 // or whatever...

        val item = menu!!.getItem(positionOfMenuItem)
        val s = SpannableString("업로드")
        s.setSpan(ForegroundColorSpan(resources.getColor(R.color.text_subscrib_color)), 0, s.length, 0)
        item.setTitle(s)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.next -> {
                if(richwysiwygeditor.getHeadlineEditText().getText().toString() == ""){
                    Toast.makeText(applicationContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
                if(richwysiwygeditor.getContent().getHtml().toString() == ""){
                    Toast.makeText(applicationContext, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }



                feedViewModel.insertFeed(
                    richwysiwygeditor.getHeadlineEditText().getText().toString(),
                    richwysiwygeditor.getContent().getHtml().plus("<br>"),
                    richwysiwygeditor.tagSeq,
                    M_SEQ,
                    richwysiwygeditor.typeValue
                )

                Toast.makeText(applicationContext, "업로드 성공.", Toast.LENGTH_SHORT).show()

                finish()

                Log.i(
                    "Rich Wysiwyg Headline",
                    richwysiwygeditor.getHeadlineEditText().getText().toString()
                )
                if (richwysiwygeditor.getContent().getHtml() != null) Log.i(
                    "Rich Wysiwyg",
                    richwysiwygeditor.getContent().getHtml()
                )
                true


            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 파일 사이즈 조정
    fun saveBitmapToFile(file: File): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o: BitmapFactory.Options = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 1
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2: BitmapFactory.Options = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap: Bitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: Exception) {
            null
        }
    }



    private fun getCount():Int{
       return this.count
    }
    private fun increaseCount(){
        this.count += 1
    }


    fun resize(context:Context, uri: Uri, resize:Int):Bitmap {
         var resizeBitmap:Bitmap? = null;

       var options: BitmapFactory.Options  = BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            var width = options.outWidth;
            var height = options.outHeight;
            var samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            var bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (e : FileNotFoundException) {
            e.printStackTrace();
        }
        return resizeBitmap!!;
    }




}