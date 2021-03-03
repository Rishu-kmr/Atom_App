package com.example.atom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button button_google;
    private Button button_guest;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
//    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.terms);
        SpannableString string = new SpannableString("By creating an account you agree to our Terms \n of Service & Privacy Policy");
        string.setSpan(new RelativeSizeSpan(1.15f), 40, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new UnderlineSpan(), 40, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(string);
        mAuth = FirebaseAuth.getInstance();
//        progressBar = (ProgressBar)findViewById(R.id.progressbar_userinfo);
        button_google = (Button)findViewById(R.id.btn_google);
        button_guest = (Button)findViewById(R.id.btn_guest);
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser!=null) {
                    Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
                login();
            }
        });
        button_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserInfo.class));
            }
        });
        OnlineImage();
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
    }
    public void displayImage(){
        imageView = findViewById(R.id.imageView);
        String ImageURL = "https://www.jccchicago.org/wp-content/uploads/2020/03/blog-yoga.png";
        String imageurl = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.jccchicago.org%2Fcreating-zen-bringing-yoga-to-your-home%2F&psig=AOvVaw20ZNdSEG3PGlGwxNb68Fcr&ust=1614864521185000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNiCqqadlO8CFQAAAAAdAAAAABAE";
        URL url = null;
        try {
            url = new URL(ImageURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bmp);
    }
    public void OnlineImage(){
        imageView = (ImageView)findViewById(R.id.imageView);
        String imageurl = "https://www.jccchicago.org/wp-content/uploads/2020/03/blog-yoga.png";
        Glide.with(this).load(imageurl).into(imageView);
    }


    public void login() {
        Intent signInintent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInintent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignResult(task);
        }
    }

    private void handleSignResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Google Signed In Successfully",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,HomeScreen.class));
            finish();
//            progressBar.setVisibility(View.INVISIBLE);
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Google Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,HomeScreen.class));
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void checkLogin(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            startActivity(new Intent(MainActivity.this,HomeScreen.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
//        progressBar.setVisibility(View.INVISIBLE);
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
