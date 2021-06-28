package com.example.retrofitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView textViewResult;
    private JSONPlaceholderApi jsonPlaceholderApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.text_view_result);
        Gson gson = new GsonBuilder().serializeNulls().create();//GSON will NOT ignore null values

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Interceptor-Header", "xyz")
                                .build();

                                return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();



        Retrofit retrofit = new Retrofit.Builder() //instance of retrofit
                .baseUrl("https://jsonplaceholder.typicode.com/") //must put / at the end, nothing more after that
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)//okHttp interceptor linked to Retrofit Client
                .build();

        jsonPlaceholderApi= retrofit.create(JSONPlaceholderApi.class);
        getPosts();
        //getComments();
        //createPost();
       //updatePost();
        //deletePost();
    }

    private void getPosts()
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId","1"); //Hashmap can take only one key-value pair at a time
        parameters.put("_sort","id");
        parameters.put("_order","desc");

        Call<List<Post>> call = jsonPlaceholderApi.getPosts(parameters);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (!response.isSuccessful()) { //if response is not successful
                    textViewResult.setText("Code: " + response.code()); //404 code
                    return; //REQUIRED, else if the result is null and the code runs, the app crashes
                }
                //if response is successful HTTP code 200-300
                List<Post> posts = response.body();

                for (Post post : posts) { //display the body/text contents from website
                    String content = "";
                    content += "ID: " + post.getId() + "\n"; //id from post object by getter method
                    content += "User ID: " + post.getUserId() + "\n";
                    content += "Title: " + post.getTitle() + "\n";
                    content += "Text: " + post.getText() + "\n\n"; //two line breaks to prevent next object to start
                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable throwable) {
                textViewResult.setText(throwable.getMessage());
                //throwable is a superclass of exception error
            }
        });
    }

    private void getComments()
    {
        Call<List<Comment>> call = jsonPlaceholderApi
                .getComments("posts/3/comments");
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                if (!response.isSuccessful()) { //if response is not successful
                    textViewResult.setText("Code: " + response.code()); //404 code
                    return; //REQUIRED, else if the result is null and the code runs, the app crashes
                }

                List<Comment> comments = response.body();
                for (Comment comment : comments) { //display all the comments when app starts
                    String content = "";
                    content += "ID: " + comment.getId() + "\n";
                    content += "Post ID: " + comment.getPostId() + "\n";
                    content += "Name: " + comment.getName() + "\n";
                    content += "Email: " + comment.getEmail() + "\n";
                    content += "Text: " + comment.getText() + "\n\n";
                    textViewResult.append(content);
                }

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                textViewResult.setText(t.getMessage());

            }
        });

    }

    private void createPost(){
        Post post = new Post(23,"New Title", "New Text");//generally accept user-input

        Map<String, String> fields = new HashMap<>();
        fields.put("userId","25");
        fields.put("title", "New Title");



        //Call<Post> call = jsonPlaceholderApi.createPost(post);//send JSON object "post" contents to the server
        //Call<Post> call = jsonPlaceholderApi.createPost(23,"New Title","New Text");//url
        Call<Post> call = jsonPlaceholderApi.createPost(fields); //as FieldMap
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) { //if response is not successful
                    textViewResult.setText("Code: " + response.code()); //404 code
                    return; //REQUIRED, else if the result is null and the code runs, the app crashes
                }
                Post postResponse = response.body();
                String content = "";
                content += "Code: " + response.code() + "\n"; //responsecode
                content += "ID: " + postResponse.getId() + "\n";
                content += "User ID: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";
                textViewResult.setText(content);

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());

            }
        });
    }

    private void updatePost()
    {
        Post post = new Post(12,null,"New Text");//by default GSON completely ignores null value

        Map <String, String> headers = new HashMap<>();
        headers.put("Map-Header1", "def");
        headers.put("Map-Header2", "ghi");

        Call<Post> call = jsonPlaceholderApi.patchPost(headers,5,post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (!response.isSuccessful()) { //if response is not successful
                    textViewResult.setText("Code: " + response.code()); //404 code
                    return; //REQUIRED, else if the result is null and the code runs, the app crashes
                }
                Post postResponse = response.body();
                String content = "";
                content += "Code: " + response.code() + "\n"; //responsecode
                content += "ID: " + postResponse.getId() + "\n";
                content += "User ID: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";
                textViewResult.setText(content);


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void deletePost()
    {
        Call<Void> call = jsonPlaceholderApi.deletePost(5);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                textViewResult.setText("Code:" + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                textViewResult.setText(t.getMessage());

            }
        });
    }
}