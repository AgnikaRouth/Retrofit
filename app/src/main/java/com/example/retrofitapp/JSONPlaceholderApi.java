package com.example.retrofitapp;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JSONPlaceholderApi {


@GET("posts") //only relative url - list of posts
    Call<List<Post>> getPosts(
            @Query("userId") Integer[] userId, // retrofit all already do - (?,=) comments?postId=1
             //@Query("userId")Integer userId2,//int is not nullable whereas Integer IS nullable
            @Query("_sort") String sort,
            @Query("_order") String order
    ); //retrofit will add & before params

    @GET("posts") //only relative url - list of posts
    Call<List<Post>> getPosts(@QueryMap Map<String, String> parameters); //different key-value pass

@GET("posts/{id}/comments") //to get any id value
Call<List<Comment>> getComments(@Path("id") int postId); //retrofit will convert this int postId as {id} as String

@GET
    Call<List<Comment>> getComments(@Url String url);

//send some data TO the server
@POST("posts")
   Call<Post> createPost(@Body Post post);
    //post will be serialised to JSON format by GSON

    @FormUrlEncoded//encoded same as the url : userId=23&title=New%20Title&body=New%20Text
    @POST("posts")//define posts end point
    Call<Post> createPost(
            @Field("userId") int userId,
            @Field("title") String title,
            @Field("body") String text
    );

    @FormUrlEncoded
    @POST("posts")
    Call<Post> createPost(@FieldMap Map<String, String> fields); //define @field if you want to pass list/array

    @Headers({"Static-Header: 123", "Static-Header2: 456"})
    @PUT("posts/{id}") //completely replace the old whole content
    Call<Post> putPost(@Header ("Dynamic-Header") String header,
                       @Path("id") int id, @Body Post post); //post is an object of type Post

    @PATCH("posts/{id}")//replaces only selected fields
    Call<Post> patchPost(@HeaderMap Map<String, String> headers,
                         @Path("id") int id, @Body Post post);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);
}
