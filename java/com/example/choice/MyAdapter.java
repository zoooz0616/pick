package com.example.choice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<MyItem> list;
    //이미지 레이아웃 파라미터
    ViewGroup.LayoutParams img1_params1, img2_params2;
    LinearLayout.LayoutParams btn1_params, btn2_params;
    Context context;
    String TAG = "MyAdapter";
    Handler handler = new Handler();
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView text;
        ImageView img1;
        ImageView img2;
        Button btn1, btn2;
        ImageButton scrap;

        public ViewHolder(View view) {
            super(view);
            this.date = view.findViewById(R.id.date);
            this.text = view.findViewById(R.id.text);
            this.img1 = view.findViewById(R.id.img1);
            this.img2 = view.findViewById(R.id.img2);
            this.btn1 = view.findViewById(R.id.btn1);
            this.btn2 = view.findViewById(R.id.btn2);
            this.scrap = view.findViewById(R.id.scrap);

            //이 리스너 사실 필요없는데 테스트용으로 일단 놔둠
            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            Log.i(TAG, "ViewHolder, responseListener 투표 반영됨");
                        } else {
                            Log.i(TAG, "ViewHolder, responseListener 투표 반영안됨");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            this.scrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = getAdapterPosition();
                    int isClipped = list.get(i).getIsClipped(); //스크랩 여부 체크
                    if(isClipped == 0){ // 스크랩이 안되어있다면(0) 저장 요청
                        scrap.setImageResource(R.drawable.save_2);
                        list.get(i).setIsClipped(1); // 스크랩 했음을 표시
                        ScrapRequest scrapRequest = new ScrapRequest(SaveSharedPreferences.getUserEmail(context), list.get(i).getId(), 0, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(view.getContext());
                        queue.add(scrapRequest);
                    } else{ // 스크랩이 되어있다면(1) 삭제 요청
                        scrap.setImageResource(R.drawable.save_1);
                        list.get(i).setIsClipped(0); //스크랩 해제함을 표시
                        ScrapRequest scrapRequest = new ScrapRequest(SaveSharedPreferences.getUserEmail(context), list.get(i).getId(), 1, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(view.getContext());
                        queue.add(scrapRequest);
                    }
                }
            });
        }

    }

    public MyAdapter(List<MyItem> list, Context context) {
        this.list = list;
        //이미지 레이아웃 파라미터
        img1_params1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        img2_params2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.date.setText(list.get(i).toString());
        viewHolder.text.setText(list.get(i).title);

        //img1 적용
        viewHolder.img1.setLayoutParams(img1_params1);
        System.out.println("--------------- 홈어뎁터" + list.get(i).img1);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 걸릴 작업 구현, home 어뎁터와 패턴 유사
                // TODO Auto-generated method stub
                try{
                    URL url1 = new URL(list.get(i).img1);
                    InputStream is = url1.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    System.out.println("여기-------------" + bm);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            viewHolder.img1.setImageBitmap(bm);
                        }
                    });
                } catch(Exception e){
                }
            }
        });
        t.start();

        //img2가 없는 경우(이미지 1개)
        if (list.get(i).img2.equals("-1")) {
            viewHolder.img2.setLayoutParams(img2_params2);

            //img2가 있는 경우(이미지 2개)
        } else {
            viewHolder.img2.setLayoutParams(img1_params1);
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {    // 오래 거릴 작업을 구현한다
                    // TODO Auto-generated method stub
                    try{
                        URL url = new URL(list.get(i).img2);
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        //final Bitmap bm = getResizedBitmap(decodeUri(is), 200,200); // 200처럼 직접 숫자 넣지 말고 res >values> dimens.xml에서 item_width, item_height 사용하길 권장....
                        System.out.println("여기-------------" + bm);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {  // 화면에 그려줄 작업
                                viewHolder.img2.setImageBitmap(bm);
                            }
                        });
                    } catch(Exception e){
                    }
                }
            });
            t2.start();
        }

//        //img1 적용
//        viewHolder.img1.setLayoutParams(img1_params1);
//        viewHolder.img1.setImageBitmap(list.get(i).img1);
//
//        //img2가 없는 경우(이미지 1개)
//        if (list.get(i).img2 == null) { // -1
//            viewHolder.img2.setLayoutParams(img2_params2);
//
//        //img2가 있는 경우(이미지 2개)
//        } else {
//            viewHolder.img2.setLayoutParams(img1_params1);
//            viewHolder.img2.setImageBitmap(list.get(i).img2);
//        }

        if(list.get(i).isClipped == 0){ //스크랩 안되어있는 경우 빈스크랩 아이콘 적용
            viewHolder.scrap.setImageResource(R.drawable.save_1);
        }else{ //스크랩 되어있는 경우 꽉찬 스크랩 아이콘 적용
            viewHolder.scrap.setImageResource(R.drawable.save_2);
        }

        Log.i(TAG, "onBindViewHolder, responseListener");
        //버튼 폰트컬러, 배경색 변경
        if (list.get(i).cnt1 > list.get(i).cnt2) {
            viewHolder.btn1.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
            viewHolder.btn1.setTextColor(Color.BLACK);
            viewHolder.btn2.setBackgroundColor(Color.WHITE);
            viewHolder.btn2.setTextColor(Color.BLACK);
        } else if (list.get(i).cnt1 < list.get(i).cnt2){
            viewHolder.btn2.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
            viewHolder.btn2.setTextColor(Color.BLACK);
            viewHolder.btn1.setBackgroundColor(Color.WHITE);
            viewHolder.btn1.setTextColor(Color.BLACK);
        } else{
            viewHolder.btn2.setBackgroundColor(Color.WHITE);
            viewHolder.btn2.setTextColor(Color.BLACK);
            viewHolder.btn1.setBackgroundColor(Color.WHITE);
            viewHolder.btn1.setTextColor(Color.BLACK);
        } //이거 홈어뎁터에도 동일하게 적용하기

        //득표율 계산 및 설정
        viewHolder.btn1.setText(list.get(i).cnt1 + "표\n" + list.get(i).btn1);
        viewHolder.btn2.setText(list.get(i).cnt2 + "표\n" + list.get(i).btn2);

        //득표율에 따른 버튼 weight 변경
        if(list.get(i).cnt1 != 0 || list.get(i).cnt2 != 0) { //둘중 적어도 하나가 0표가 아닌 경우 득표수 비율에 맞춰서 설정
            btn1_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt1);
            btn2_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt2);
        } else{ //둘다 0표라면 비율 1 : 1로 설정
            btn1_params = new LinearLayout.LayoutParams(0, 250, 1);
            btn2_params = new LinearLayout.LayoutParams(0, 250, 1);
        }
        viewHolder.btn1.setLayoutParams(btn1_params);
        viewHolder.btn2.setLayoutParams(btn2_params);

        //자기 게시물은 투표 버튼 비활성화
        viewHolder.btn1.setEnabled(false);
        viewHolder.btn2.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}