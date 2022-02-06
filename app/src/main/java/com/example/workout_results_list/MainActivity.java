package com.example.workout_results_list;

import static io.reactivex.Single.fromCallable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    ArrayList<SampleData> movieDataList;
    ArrayList<Bitmap> imgBitMapArray;
    ArrayList<Integer> idArray;
    private String[][] exerMatrix;
    private String[][] subjMatrix;
    private Object[] vector;
    private String urlPhp;
    private String urlPhpPhoto;
    String photoBinary;
    Bitmap imgBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vector = (Object[])getIntent().getSerializableExtra("subjectArray");
        subjMatrix = Arrays.copyOf(vector, vector.length, String[][].class);
        idArray = new ArrayList<>(subjMatrix.length);

        for(int j = 0; j < subjMatrix.length; j++)
        {
            idArray.add(Integer.valueOf(subjMatrix[j][0]));
        }

        this.InitializeListData();

        ListView listView = (ListView)findViewById(R.id.listView);
        final MyAdapter myAdapter = new MyAdapter(this,movieDataList);

        listView.setAdapter(myAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        try {
                            urlPhp = "http://203.252.230.222/getExerDataset.php?subj_id=" + myAdapter.getItem(position).getMovieName();

                            URL url = new URL(urlPhp);
                            HttpClient client = new DefaultHttpClient();
                            HttpGet request = new HttpGet();
                            request.setURI(new URI(urlPhp));
                            HttpResponse response = client.execute(request);
                            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                            StringBuffer sb = new StringBuffer("");
                            String line = "";

                            while ((line = in.readLine()) != null) {
                                sb.append(line);
                                break;
                            }
                            // DB 에 Data 가 없는 경우
                            if (line == null) {
                                in.close();
//                        prevCount = 0;
                                Toast.makeText(getApplicationContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            else {
                                in.close();
                                String[] dbExerData = line.split("%");
                                exerMatrix = new String[dbExerData.length][];

                                int r = 0;
                                for (String row : dbExerData) {
                                    exerMatrix[r++] = row.split("&");
                                }
                                Intent intent = new Intent(MainActivity.this, Plot.class);
                                intent.putExtra("exerData", exerMatrix);
                                startActivity(intent);

//                        prevCount = Integer.valueOf(dbExerData[2]);
                                return true;               // String 형태로 반환
                            }
                        } catch (Exception e) {
                            return true;
                        }

                        // RxJava does not accept null return value. Null will be treated as a failure.
                        // So just make it return true.
                    }
                }) // Execute in IO thread, i.e. background thread.
                        .subscribeOn(Schedulers.newThread())
                        // report or post the result to main thread.
                        .observeOn(AndroidSchedulers.mainThread())
                        // execute this RxJava
                        .subscribe();
            }
        });
    }

    public void dbPhotoGet(int id){
        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    urlPhpPhoto = "http://203.252.230.222/getSubjPhoto.php?subj_id=" + String.valueOf(id);
                    int arrIndex = idArray.indexOf(id);

                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(urlPhpPhoto));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    // 1st line 을 복사(null 없애기 위해)
                    line = in.readLine();
                    photoBinary = line;
                    while ((line = in.readLine()) != null) {
                        photoBinary = photoBinary.concat(line);
                        sb.append(line);
                    }

                    if(photoBinary == null) {
                        imgBitMapArray.add(arrIndex,null);
                        in.close();
                        return true;
                    }
                    else {
                        byte[] decodedString = Base64.decode(photoBinary, Base64.DEFAULT | Base64.NO_WRAP);
                        imgBitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgBitMapArray.add(arrIndex, imgBitMap);
                        in.close();
                        return true;
                    }
/*
                    // DB 에 Data 가 없는 경우
                    if (line == null) {
                        in.close();
//                        imgBitMapArray.add(null);
//                        prevCount = 0;
                        return true;
                    }
                    else {
                        while(imgBitMap == null) {};

                        in.close();
//                        prevCount = Integer.valueOf(dbExerData[2]);
                        return true;               // String 형태로 반환
                    }
*/
                } catch (Exception e) {
                    return true;
                }

                // RxJava does not accept null return value. Null will be treated as a failure.
                // So just make it return true.
            }
        })
                // Execute in IO thread, i.e. background thread.
                .subscribeOn(Schedulers.io()).blockingGet();

//        while(imgBitMap == null) {};

    }

    public void InitializeListData()
    {

//        final Integer[] idArray;
//        idArray = new Integer[subjMatrix.length];
        imgBitMapArray = new ArrayList<Bitmap>(subjMatrix.length);

        for (int i = 0; i < idArray.size(); i++ )
        {
//            idArray[i] = Integer.valueOf(subjMatrix[i][0]);
            dbPhotoGet(Integer.valueOf(subjMatrix[i][0]));
        }

        movieDataList = new ArrayList<SampleData>();
        int i = 0;
        while(i < subjMatrix.length) {
            movieDataList.add(new SampleData(R.drawable.exer1, subjMatrix[i][0], subjMatrix[i][1], imgBitMapArray.get(i)));
            i++;
        }
    }


    public void onsuccessfulComplete() {

    }

    public void onBackPressed() {
        finishAffinity();
    }

    private void loadPhoto(int id) {

    }

}