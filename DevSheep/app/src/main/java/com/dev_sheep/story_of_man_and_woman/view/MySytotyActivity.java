package com.dev_sheep.story_of_man_and_woman.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dev_sheep.story_of_man_and_woman.R;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.lumyjuwon.richwysiwygeditor.RichWysiwyg;

import java.util.List;

public class MySytotyActivity extends AppCompatActivity {


    private RichWysiwyg wysiwyg;
    private Toolbar toolbar;
    private ImageView iv_back;
    private TextView tv_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_write);

        toolbar = (Toolbar)findViewById(R.id.toolbar_write);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //추가된 소스코드, Toolbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_add);

        wysiwyg = findViewById(R.id.richwysiwygeditor);

        wysiwyg.getContent()
                .setEditorFontSize(16)
                .setEditorPadding(16,16,16,8);


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

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            insertImages(images);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void insertImages(List<Image> images) {
        if (images == null) return;

        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0, l = images.size(); i < l; i++) {
            stringBuffer.append(images.get(i).getPath()).append("\n");
            // Handle this
            wysiwyg.getContent().insertImage("file://" + images.get(i).getPath(), "alt");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.next :
                Toast.makeText(getApplicationContext(), "Option Click", Toast.LENGTH_SHORT).show();
                Log.i("Rich Wysiwyg Headline", wysiwyg.getHeadlineEditText().getText().toString());
                if(wysiwyg.getContent().getHtml() != null)
                    Log.i("Rich Wysiwyg", wysiwyg.getContent().getHtml());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
