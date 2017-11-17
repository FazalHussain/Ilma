package com.ilma.testing.ilmaapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ilma.testing.ilmaapp.Adapter.CommentAdapter;
import com.ilma.testing.ilmaapp.Adapter.PostAdapter;
import com.ilma.testing.ilmaapp.Adapter.PostPhtoGridAdapter;
import com.ilma.testing.ilmaapp.Models.Comment;
import com.ilma.testing.ilmaapp.Models.Image;
import com.ilma.testing.ilmaapp.Models.Post;
import com.ilma.testing.ilmaapp.R;
import com.ilma.testing.ilmaapp.Utility.Utils;
import com.ilma.testing.ilmaapp.data.LocalStorage;
import com.ilma.testing.ilmaapp.data.TimeAgo;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PostAdapter.OnItemClickListener{

    private static final int REQUEST_COMPOSE = 101;
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().getRoot();

    List<Post> itemList = new ArrayList<>();
    List<Post> listPost = itemList;
    List<Comment> commentList = new ArrayList<>();
    Image base64Img_path_post;

    ProfilePictureView userProfilePic;
    private TextView userNameTextView;
    private TextView workTextView;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ProgressBar mProgressBar;

    private NavigationView navigationView;
    private RecyclerView post_rv;
    private PostAdapter adapter;
    private View inflatedView;
    private PopupWindow popWindow;
    private PostPhtoGridAdapter adapter_photo;
    private String UserID;
    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       // verifyCalenderPermissions(this);


        SetupDrawer();

        setUpProfileBasicInfo(navigationView);

        SetupRecyclerView();

        //GenerateKeyHashes();
    }

    private void GenerateKeyHashes() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.testing.CDS",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {

        }
    }

    private void SetupRecyclerView() {

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        post_rv = (RecyclerView) findViewById(R.id.post_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        post_rv.setLayoutManager(layoutManager);

        //for divider in recyler view item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(post_rv.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider_rv_post)
        );
        post_rv.addItemDecoration(dividerItemDecoration);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        post_rv.setHasFixedSize(true);

        getPostList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemList.clear();
        getPostList();
    }

    private void getPostList() {
        final long[] childCount = {0};
        final DatabaseReference mDatabasePost = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef = mDatabasePost.child("Posts");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    itemList.clear();
                    Utils utils =new Utils(HomeActivity.this);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        if(!utils.getdata("InstalledDate").equalsIgnoreCase("")) {
                            Date InstalledDate = df.parse(utils.getdata("InstalledDate"));

                            Post post = d.getValue(Post.class);
                            Date date = df.parse(post.getDuration());
                            long time = date.getTime();

                            String time_duration = TimeAgo.getTimeAgo(time);
                            post.setDuration(time_duration);

                            if (date.after(InstalledDate)) {
                                itemList.add(0,post);
                            }
                        }
                    }

                    mProgressBar.setVisibility(View.GONE);
                    if(itemList!=null && itemList.size()>0){
                        adapter = new PostAdapter(itemList,HomeActivity.this);
                        post_rv.setAdapter(adapter);
                        post_rv.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*mDatabasePost.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childCount[0] = dataSnapshot.getChildrenCount();
                mDatabaseRef = mDatabasePost.child("Posts");

                itemList.clear();
                mDatabaseRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        long count = childCount[0];

                        getData(dataSnapshot,count);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/



    }


    private void getData(DataSnapshot dataSnapshot, final long count) {

        new AsyncTask<DataSnapshot,Void,List<Post>>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                post_rv.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            protected List<Post> doInBackground(DataSnapshot... dataSnapshots) {
                Iterator i = dataSnapshots[0].getChildren().iterator();
                if(itemList.size() == count) return itemList;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    while (i.hasNext()) {
                        try {
                            Post comment = (Post) ((DataSnapshot) i.next()).getValue(Post.class);

                        }catch (DatabaseException e){
                            e.printStackTrace();
                        }

                        long commentsCount = (long) ((DataSnapshot) i.next()).getValue();
                        String duration = (String) ((DataSnapshot) i.next()).getValue();
                        String imageEncode = (String) ((DataSnapshot) i.next()).getValue();
                        String post_key = (String) ((DataSnapshot) i.next()).getValue();

                        String post_content = (String) ((DataSnapshot) i.next()).getValue();
                        String post_owner = (String) ((DataSnapshot) i.next()).getValue() + " updated a status on CDS Office App";

                        String UserID = (String) ((DataSnapshot) i.next()).getValue();
                        Date date = df.parse(duration);
                        long time = date.getTime();

                        String time_duration = TimeAgo.getTimeAgo(time);

                        DatabaseReference mDatabaseRef_comment = mDatabaseRef.child("Comments");



                        Utils utils =new Utils(HomeActivity.this);
                        if(!utils.getdata("InstalledDate").equalsIgnoreCase("")){
                            Date InstalledDate = df.parse(utils.getdata("InstalledDate"));
                            if(date.after(InstalledDate)){
                                Post post = new Post(post_owner, time_duration, UserID,
                                        post_content,post_key,imageEncode);
                                post.setCommentsCount(commentsCount);
                                itemList.add(0,post);
                            }
                        }

                        //String time_duration = TimeAgo.ConvertTimeStampintoAgo(time);



                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return itemList;
            }

            @Override
            protected void onPostExecute(List<Post> posts) {

                mProgressBar.setVisibility(View.GONE);
                if(posts!=null && posts.size()>0){
                    adapter = new PostAdapter(itemList,HomeActivity.this);
                    post_rv.setAdapter(adapter);
                    post_rv.setVisibility(View.VISIBLE);
                }
            }
        }.execute(dataSnapshot);

    }

    private void SetupDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_compose();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void post_compose() {
        Intent i = new Intent(HomeActivity.this, ComposeActivity.class);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            i.putExtra("UserID", profile.getId());
            i.putExtra("Username", profile.getName());

        }

        startActivityForResult(i,REQUEST_COMPOSE);
    }

    private void profile_click() {
        Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            i.putExtra("uri", profile.getLinkUri().toString());
        }

        startActivity(i);
    }

    private void setUpProfileBasicInfo(NavigationView navigationView) {
        try {
            if (navigationView != null) {

                String response = LocalStorage.getDefaults("FbJson", getApplicationContext());
                JSONObject jsonObject = new JSONObject(response);

                //Profile profile = Profile.getCurrentProfile();
                View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_home);
                userProfilePic = navHeaderView.findViewById(R.id.profilePic);
                workTextView = navHeaderView.findViewById(R.id.job);
                userNameTextView = navHeaderView.findViewById(R.id.Post_Owner);
                userProfilePic.setProfileId(jsonObject.getString("id"));
                LocalStorage.setDefaults("UserName",jsonObject.getString("name"),getApplicationContext());
                UserName = jsonObject.getString("name");
                userNameTextView.setText(UserName);


                JSONArray jsonArray = jsonObject.has("work") ?
                        jsonObject.getJSONArray("work") : null;
                JSONObject jsonObjWork = null;
                String position = "";
                JSONObject jsonEmploy = null;
                if(jsonArray!=null) {
                    jsonObjWork = jsonArray.getJSONObject(0);
                    JSONObject jsonPosition = jsonObjWork.getJSONObject("position");
                    position = jsonPosition.getString("name");
                    jsonEmploy = jsonObjWork.has("employer") ?
                            jsonObjWork.getJSONObject("employer") : null;
                }


                String employer = "";
                if(jsonEmploy!=null){
                    employer = jsonEmploy.getString("name");
                }

                /*JSONObject jsonPosition = jsonObjWork.getJSONObject("position");

                String position = jsonPosition.getString("name");*/
                String pos_employe = position.length() > 0 && employer.length() > 0 ?
                        position + " at " + employer : "";
                workTextView.setText(pos_employe);

                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.app_bar);
                RelativeLayout home_content = (RelativeLayout) coordinatorLayout.findViewById(R.id.content_home);
                userProfilePic = home_content.findViewById(R.id.profilePic);
                UserID = jsonObject.getString("id");
                userProfilePic.setProfileId(UserID);

                ComposeClick(home_content);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onShowCommentPopup(View v, final Post post){

        commentList.clear();

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        inflatedView = layoutInflater.inflate(R.layout.comment_popup_layout, null,false);
        // find the ListView in the popup layout
        final ListView listView_comment = (ListView)inflatedView.findViewById(R.id.commentsListView);
        final TextView textView_header = inflatedView.findViewById(R.id.header);
        final EditText comment_et = inflatedView.findViewById(R.id.writeComment);
        comment_et.requestFocus();
        final ImageView send = inflatedView.findViewById(R.id.send);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;




        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, width,height-50, true );
        // set a background drawable with rounders corners
        //popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
        popWindow.setBackgroundDrawable (new BitmapDrawable());
        popWindow.setOutsideTouchable(true);

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);

        final String postID = post.getPostKey();
        final DatabaseReference mDatabaseRef_comments = mDatabaseRef.child(postID)
                .child("comments").child("Comments");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if(comment_et.getText().length() == 0) return;

                    DrawableCompat.setTint(send.getDrawable(), ContextCompat.getColor(
                            getApplicationContext(), R.color.color_send_pressed));

                    String commentKey = mDatabaseRef.push().getKey();

                    String username = LocalStorage.getDefaults("UserName",getApplicationContext());
                    Comment comment = new Comment(username, comment_et.getText().toString(),
                            userProfilePic.getProfileId(), df.format(new Date()), commentKey);

                    DatabaseReference mDatabaseRef_comments_unique = mDatabaseRef_comments.child(commentKey);

                    mDatabaseRef_comments.child("comment").removeValue();
                    mDatabaseRef_comments.child("commentID").removeValue();
                    mDatabaseRef_comments.child("comment_UserID").removeValue();
                    mDatabaseRef_comments.child("comment_user").removeValue();
                    mDatabaseRef_comments.child("duration").removeValue();


                    // fill the data to the list items
                    //setSimpleList(listView);

                    mDatabaseRef_comments_unique.setValue(comment);
                    DrawableCompat.setTint(send.getDrawable(), ContextCompat.getColor(
                            getApplicationContext(), R.color.color_send));
                    comment_et.setText("");


                    loadComments(mDatabaseRef_comments,listView_comment,postID,textView_header);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        loadComments(mDatabaseRef_comments,listView_comment,postID,textView_header);


    }

    private void loadComments(DatabaseReference mDatabaseRef_comments,
                              final ListView listView_comment, final String postID, final TextView textView) {

        mDatabaseRef_comments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    commentList.clear();
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Comment comment = d.getValue(Comment.class);
                        if(!comment.getComment_UserID().equalsIgnoreCase("NA"))
                        commentList.add(comment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mDatabaseRef.child(postID).child("commentsCount").setValue(commentList.size());

                String header = commentList.size()>1 ?  commentList.size() + " Comments" :
                        commentList.size() + " Comment";
                textView.setText(header);

                CommentAdapter adapter = new CommentAdapter(HomeActivity.this,R.layout.comment_row,commentList);
                listView_comment.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        CommentAdapter adapter = new CommentAdapter(this,R.layout.comment_row,commentList);
        listView_comment.setAdapter(adapter);
    }

    public void ComposeClick(View view) {
        ((LinearLayout) view.findViewById(R.id.compose_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_compose();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.newsfeed) {
            getPostList();

        } else if (id == R.id.post_compose) {
            post_compose();

        }else if (id == R.id.leave_request) {
             sendRequest();
             Intent i = new Intent(this, SendRequestActivity.class);
             i.putExtra("Username", UserName);
             startActivity(i);

         } else if (id == R.id.profileinfo) {
            profile_click();
        } else if (id == R.id.nav_logout) {
            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendRequest() {
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
       /* RequestBody requestbody = new FormBody.Builder()
                .add("token",token)
                .add("Name","")
                .build();*/
                //Log.d("UserID",utils.getdata("Userid"));

                Request request = new Request.Builder()
                        .url("https://tippiest-reactors.000webhostapp.com/PKT/send_notification.php?" +
                                "message=Fazal Send you a leave request&title=Leave Request")
                        .build();

                try{
                    Response response = client.newCall(request).execute();
                    Log.d("token_send",response.body().string());

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    private void logout() {
        LoginManager.getInstance().logOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onItemClicked(int pos, View view) {
        Post post = itemList.get(pos);
        if(view.getId() == R.id.action_edit){
            onEditPost(view, post);
        }else {
            onShowCommentPopup(view, post);
        }
    }

    public void onEditPost(View v, final Post post){

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");



        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        inflatedView = layoutInflater.inflate(R.layout.edit_popup, null,false);
        // find the ListView in the popup layout

        ProfilePictureView profilepicview = inflatedView.findViewById(R.id.profilePic);
        TextView mUserName = inflatedView.findViewById(R.id.Post_Owner);
        profilepicview.setDrawingCacheEnabled(true);
        profilepicview.setProfileId(UserID);
        mUserName.setText(UserName);

        final EditText post_et_edit = inflatedView.findViewById(R.id.post_et_edit);
        post_et_edit.setText(post.getPostStatus());
        post_et_edit.requestFocus();
        final Button edit = inflatedView.findViewById(R.id.edit_btn);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;




        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, width,height-50, true );
        // set a background drawable with rounders corners
        //popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
        popWindow.setBackgroundDrawable (new BitmapDrawable());
        popWindow.setOutsideTouchable(true);

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    final DatabaseReference mDatabasePost = FirebaseDatabase.getInstance().getReference();
                    mDatabaseRef = mDatabasePost.child("Posts");
                    //post_et_edit.setText(post.getPostStatus());
                    if(post_et_edit.getText().length()>0) {
                        mDatabaseRef.child(post.getPostKey()).child("postStatus").
                                setValue(post_et_edit.getText().toString());
                        post_et_edit.getText().clear();
                        popWindow.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }





}
