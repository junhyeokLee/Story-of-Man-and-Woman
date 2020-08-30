package com.dev_sheep.story_of_man_and_woman.view.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media.MediaBrowserServiceCompat.RESULT_OK
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
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


class MystoryActivity : AppCompatActivity() {


    private val feedViewModel: FeedViewModel by viewModel()
    private val REQ_CODE_SELECT_IMAGE = 1001
    lateinit var TYPE_VALUE : String
    lateinit var TAG_SEQ : String
    lateinit var TAG_NAME : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_write)
        toolbar_write.setTitle("")
        setSupportActionBar(toolbar_write)

        if (intent.hasExtra("type")) {
            TYPE_VALUE = intent.getStringExtra("type")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        if(intent.hasExtra("tag_seq")){
            TAG_SEQ = intent.getStringExtra("tag_seq")
        }else{
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()

        }
        if(intent.hasExtra("tag_name")){
            TAG_NAME = intent.getStringExtra("tag_name")
            richwysiwygeditor.tagName.text = "# "+TAG_NAME
        }else{
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()

        }

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_add);

        //        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_add);


        richwysiwygeditor.getContent()
            .setEditorFontSize(16)
            .setEditorPadding(16, 16, 16, 8)


//        wysiwyg.getCancelButton().setText("Cancel");
//
//        wysiwyg.getConfirmButton().setText("Write");
//        wysiwyg.getConfirmButton().setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                // Handle this
//                Log.i("Rich Wysiwyg Headline", wysiwyg.getHeadlineEditText().getText().toString());
//                if(wysiwyg.getContent().getHtml() != null)
//                    Log.i("Rich Wysiwyg", wysiwyg.getContent().getHtml());
//            }
//        });

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
        if (images == null) return
        val stringBuffer = StringBuilder()
        var i = 0
        val l = images.size
        while (i < l) {

            //파일 생성
//img_url은 이미지의 경로
//파일 생성
//img_url은 이미지의 경로
            val file = File(images.get(i).path)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body =
                MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
            val resultCall: Call<Feed> = FEED_SERVICE.uploadImage(body)
            resultCall.enqueue(object : Callback<Feed?> {
                override fun onResponse(
                    call: Call<Feed?>,
                    response: Response<Feed?>
                ) {
                    Log.e("성공함",response.toString())
                    Log.e("filename",file.name)

                    stringBuffer.append(file.name).append("\n")
                    richwysiwygeditor.getContent().insertImage("http://www.storymaw.com/data/feed/"+TAG_SEQ+"/"+ file.name, "alt")
                }
                override fun onFailure(
                    call: Call<Feed?>,
                    t: Throwable
                ) {
                    Log.e("에러",t.message)
                }
            })


            i++
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.next -> {

                // 저장된 m_seq 가져오기
                val getM_seq = getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
                val M_SEQ = getM_seq.getString("inputMseq", null)

                Toast.makeText(applicationContext, "완료.", Toast.LENGTH_SHORT).show()

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
                    Integer.parseInt(TAG_SEQ),
                    M_SEQ,
                    TYPE_VALUE
                )
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
}