package com.example.choice;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Post extends Fragment implements View.OnClickListener {
    private View view;
    CognitoCachingCredentialsProvider credentialsProvider;
    ImageView imageView1, imageView2;
    ImageButton btn;
    final int PICTURE_REQUEST_CODE = 100;
    EditText title, option1, option2;
    String category, email, fileFullPath1, fileFullPath2;
    Button[] buttons;
    TransferUtility transferUtility;
    AmazonS3 s3;
    File f1, f2;
    String imagePath;
    String TAG = "Post";
    String TimeStamp;
    Boolean isOne;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.writing, container, false);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                Post.this.getContext(),
                "ap-northeast-2:7e99edfb-3dc3-40c0-b033-ec9bbeb0a4df", // Identity Pool ID
                Regions.AP_NORTHEAST_2
        );
        isOne = false;
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        transferUtility = TransferUtility.builder().s3Client(s3).context(Post.this.getActivity()).build();
        TransferNetworkLossHandler.getInstance(Post.this.getActivity());

        //이미지 갤러리에서 불러오기
        imageView1 = view.findViewById(R.id.photo1);
        imageView2 = view.findViewById(R.id.photo2);
        btn=view.findViewById(R.id.btn);

        email = SaveSharedPreferences.getUserEmail(view.getContext());
        title = view.findViewById(R.id.title);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);

        buttons = new Button[]{view.findViewById(R.id.fashion), view.findViewById(R.id.interior),
                view.findViewById(R.id.media), view.findViewById(R.id.daily),view.findViewById(R.id.food)
        , view.findViewById(R.id.animal), view.findViewById(R.id.book), view.findViewById(R.id.digital)
        ,view.findViewById(R.id.economy), view.findViewById(R.id.travel)};

        for(int i = 0; i < 10; i++){
            buttons[i].setOnClickListener(this);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICTURE_REQUEST_CODE);
            }
        });

        view.findViewById(R.id.continue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferUtility.upload("sswu-pick", TimeStamp + f1.getName(), f1).setTransferListener(transferListener);
                if(!isOne){
                    transferUtility.upload("sswu-pick", TimeStamp + ""+f2.getName(), f2).setTransferListener(transferListener);
                }else{
                    fileFullPath2 = "-1"; //두번쨰 이미지 없는 경우
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 게시글 등록에 성공한 경우
                                imageView1.setImageResource(0);
                                imageView2.setImageResource(0);
                                title.setText("");
                                option1.setText("");
                                option2.setText("");

                                Toast.makeText(Post.this.getActivity(),"게시글 등록에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Home home2 = new Home();
//                                post.setArguments(bundle); //나중에 값 넘길일 있으면 사용하면 됨..
                                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.Main_Frame, home2).commit();
                            } else { // 게시글 등록에 실패한 경우
                                Toast.makeText(Post.this.getActivity(),"게시글 등록에 실패하였습니다.",Toast.LENGTH_SHORT).show(

                                );
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Log.i(TAG, "------제발------------" + f1.getName());
                // 서버 요청
                PostRequest postRequestTemp = new PostRequest(email, title.getText().toString(), option1.getText().toString(), option2.getText().toString(), category, fileFullPath1, fileFullPath2, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Post.this.getContext());
                queue.add(postRequestTemp);
            }
        });
        return view;
    }

    TransferListener transferListener = new TransferListener() {
        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.d(TAG, "onStateChanged: " + id + ", " + state.toString());

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
            int percentDone = (int)percentDonef;
            Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
        }

        @Override
        public void onError(int id, Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    };

    @Override
    public void onClick(View v){
        int position = 0;
        for(int i = 0; i < 10; i++){
            buttons[i].setTextColor(0xff000000);
        }

        switch (v.getId()) {
            case R.id.fashion:
                category = "fashion";
                position = 0;
                break;
            case R.id.interior:
                category = "interior";
                position = 1;
                break;
            case R.id.media:
                category = "media";
                position = 2;
                break;
            case R.id.daily:
                category = "daily";
                position = 3;
                break;
            case R.id.food:
                category = "food";
                position = 4;
                break;
            case R.id.animal:
                category = "animal";
                position = 5;
                break;
            case R.id.book:
                category = "book";
                position = 6;
                break;
            case R.id.digital:
                category = "digital";
                position = 7;
                break;
            case R.id.economy:
                category = "economy";
                position = 8;
                break;
            case R.id.travel:
                category = "travel";
                position = 9;
                break;
        }
        buttons[position].setTextColor(0xff7366ff);
    }

    //이미지 결과 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onSelectFromGalleryResult");
                Bitmap bm;
                //ClipData 또는 Uri 가져오기
                ClipData clipData = data.getClipData();
                if(clipData.getItemCount() == 1) isOne = true;
                if(clipData!=null) { // 기존 코드에서 수정함
                    for(int i = 0; i < 3; i++) {
                        if(i<clipData.getItemCount()) {
                            Uri urione =  clipData.getItemAt(i).getUri();
                            if (Build.VERSION.SDK_INT < 11) {
                                imagePath = RealPathUtil.getRealPathFromURI_BelowAPI11(Post.this.getContext(), urione);
                                Log.d(TAG, Build.VERSION.SDK_INT + "");
                            } else if (Build.VERSION.SDK_INT < 19) {
                                Log.d(TAG, Build.VERSION.SDK_INT + "");
                                imagePath = RealPathUtil.getRealPathFromURI_API11to18(Post.this.getContext(), urione);
                            } else {
                                Log.d(TAG, Build.VERSION.SDK_INT + "");
                                imagePath = RealPathUtil.getRealPathFromURI_API19(Post.this.getContext(), urione);
                            }
                            Log.d(TAG, imagePath);

                            System.out.println();
                            TimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                            try {
                                bm = getResizedBitmap(decodeUri(urione), getResources().getDimensionPixelSize(R.dimen.idcard_pic_height), getResources().getDimensionPixelSize(R.dimen.idcard_pic_width));
                                switch (i){
                                    case 0:
                                        f1 = new File(imagePath);
                                        imageView1.setImageBitmap(bm);
                                        fileFullPath1 = "https://sswu-pick.s3.ap-northeast-2.amazonaws.com/" + TimeStamp + f1.getName();
                                        break;
                                    case 1:
                                        f2 = new File(imagePath);
                                        imageView2.setImageBitmap(bm);
                                        fileFullPath2 = "https://sswu-pick.s3.ap-northeast-2.amazonaws.com/" + TimeStamp + f2.getName();
                                        break;
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    public Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        if (Build.VERSION.SDK_INT <= 19) {
            //matrix.postRotate(90);
        }
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                Post.this.getContext().getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                Post.this.getContext().getContentResolver().openInputStream(selectedImage), null, o2);
    }
}