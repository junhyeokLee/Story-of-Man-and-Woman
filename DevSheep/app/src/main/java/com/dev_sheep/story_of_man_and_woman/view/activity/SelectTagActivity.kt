package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.Tag_Select_Adapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tag_select.progressBar_tag
import kotlinx.android.synthetic.main.activity_tag_select.recyclerView_tag
import kotlinx.android.synthetic.main.activity_tag_select.toolbar_write

class SelectTagActivity : AppCompatActivity(){


    var TYPE_VALUE = ""
    val TYPE_PUBLIC = "public"
    val TYPE_SUBSCRIBER = "subscriber"
    val TYPE_PRIVATE = "private"
    var CHECKED_TAG_SEQ = ""
    var CHECKED_TAG_NAME = ""

    lateinit var mAdapter : Tag_Select_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_select)
        toolbar_write.setTitle("")
        setSupportActionBar(toolbar_write)

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val layoutManager_Tag = GridLayoutManager(this,3)
        recyclerView_tag.layoutManager = layoutManager_Tag

        val single_tag = APIService.FEED_SERVICE.getTagList()
        single_tag.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (it.isNotEmpty()) {
                    recyclerView_tag.layoutManager = layoutManager_Tag
                    recyclerView_tag.adapter = Tag_Select_Adapter(it,this,object : Tag_Select_Adapter.OnTagCheckedSeq{
                        override fun getTagCheckedSeq(tag_seq: String,tag_name:String) {
                            CHECKED_TAG_SEQ = tag_seq
                            CHECKED_TAG_NAME = tag_name
                        }

                    })
                }else {
                    progressBar_tag.visibility = View.VISIBLE
                }
//                recyclerViewTag?.layoutManager = layoutManager_Tag
//                recyclerViewTag?.adapter = Test_Searchtag_Adapter(it,view.context)
//                mTagAdapter = object : Test_tag_Adapter(it,contexts)

            }
                ,{

                })

        if (intent.hasExtra("type")) {
            TYPE_VALUE = intent.getStringExtra("type")
            Log.e("TYPE 테스트 ",""+TYPE_VALUE)
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tag_select,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){

            android.R.id.home ->{
                onBackPressed()
                true
            }

            R.id.next_activity ->{

                Log.e("TAG 이름 전송",""+CHECKED_TAG_SEQ)

                if(TYPE_VALUE == TYPE_PUBLIC) {
                    val intent = Intent(this, MystoryActivity::class.java)
                    intent.putExtra("type",TYPE_PUBLIC)
                    intent.putExtra("tag_seq",CHECKED_TAG_SEQ)
                    intent.putExtra("tag_name",CHECKED_TAG_NAME)
                    startActivity(intent)
                    finish()
                }else if(TYPE_VALUE == TYPE_SUBSCRIBER){
                    val intent = Intent(this, MystoryActivity::class.java)
                    intent.putExtra("type",TYPE_SUBSCRIBER)
                    intent.putExtra("tag_seq",CHECKED_TAG_SEQ)
                    intent.putExtra("tag_name",CHECKED_TAG_NAME)
                    startActivity(intent)
                    finish()
                }else if(TYPE_VALUE == TYPE_PRIVATE){
                    val intent = Intent(this, MystoryActivity::class.java)
                    intent.putExtra("type",TYPE_PRIVATE)
                    intent.putExtra("tag_seq",CHECKED_TAG_SEQ)
                    intent.putExtra("tag_name",CHECKED_TAG_NAME)
                    startActivity(intent)
                    finish()
                }

                Log.e("TYPE VALUE ",""+TYPE_VALUE)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}