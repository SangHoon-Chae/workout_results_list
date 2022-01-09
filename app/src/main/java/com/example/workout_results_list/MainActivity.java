package com.example.workout_results_list;

import static io.reactivex.Single.fromCallable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    ArrayList<SampleData> movieDataList;
    private String[][] exerMatrix;
    private String[][] subjMatrix;
    private Object[] vector;
    private String urlPhp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vector = (Object[])getIntent().getSerializableExtra("subjectArray");
        subjMatrix = Arrays.copyOf(vector, vector.length, String[][].class);

        this.InitializeMovieData();

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

                Intent intent = new Intent(MainActivity.this, Plot.class);
                intent.putExtra("exerData", exerMatrix);
                startActivity(intent);
            }
        });
    }

    public void InitializeMovieData()
    {
        movieDataList = new ArrayList<SampleData>();
        int i = 0;
        while(i < subjMatrix.length) {
            movieDataList.add(new SampleData(R.drawable.exer1, subjMatrix[i][0], subjMatrix[i][1]));
            i++;
        }
    }

    private void loadResultsBackground(int id) {
        fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    urlPhp = "http://203.252.230.222/getExerDataset.php?subj_id=" + String.valueOf(id);

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
                        return true;
                    }
                    else {
                        in.close();
                        String[] dbSubjData = line.split("%");
                        String[][] subjMatrix = new String[dbSubjData.length][];

                        int r = 0;
                        for (String row : dbSubjData) {
                            subjMatrix[r++] = row.split("&");
                        }

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

}