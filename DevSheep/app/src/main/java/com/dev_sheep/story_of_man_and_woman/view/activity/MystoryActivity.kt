package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.testService
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
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


    private val testViewModel: TestViewModel by viewModel()
    private val REQ_CODE_SELECT_IMAGE = 1001
    private val TYPE_PUBLIC : String = "public"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_write)
        toolbar_write.setTitle("")
        setSupportActionBar(toolbar_write)


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
            val resultCall: Call<Feed> = testService.uploadImage(body)
            resultCall.enqueue(object : Callback<Feed?> {
                override fun onResponse(
                    call: Call<Feed?>,
                    response: Response<Feed?>
                ) {
                    Log.e("성공함",response.toString())
                    Log.e("filename",file.name)

                    stringBuffer.append(file.name).append("\n")
                    richwysiwygeditor.getContent().insertImage("http://www.storymaw.com/data/feed/" + file.name, "alt")
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
                Toast.makeText(applicationContext, "완료.", Toast.LENGTH_SHORT).show()
                // 저장된 m_seq 가져오기
                val getM_seq = getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
                var m_seq = getM_seq.getString("inputMseq", null)

                if(richwysiwygeditor.getHeadlineEditText().getText().toString() == ""){
                    Toast.makeText(applicationContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }
                if(richwysiwygeditor.getContent().getHtml().toString() == ""){
                    Toast.makeText(applicationContext, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return false
                }

                testViewModel.insertFeed(
                    richwysiwygeditor.getHeadlineEditText().getText().toString(),
                    richwysiwygeditor.getContent().getHtml().plus("<br>"),
                    2,
                    m_seq,
                    TYPE_PUBLIC
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