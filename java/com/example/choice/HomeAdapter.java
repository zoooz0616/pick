package com.example.choice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    List<MyItem> list;
    //이미지 레이아웃 파라미터
    ViewGroup.LayoutParams img1_params1, img2_params2;
    LinearLayout.LayoutParams btn1_params, btn2_params;
    Context context;
    String TAG = "HomeAdapter";
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

            //왼쪽 투표버튼 클릭시 이벤트처리
            this.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //투표 시 버튼 비활성화
                    btn1.setEnabled(false);
                    btn2.setEnabled(false);

                    int i = getAdapterPosition();
                    list.get(i).incCnt1(); //항목1에 대한 득표수 증가
                    list.get(i).setVoted(); //투표여부 체크

                    //버튼 폰트컬러, 배경색 변경
                    if (list.get(i).cnt1 > list.get(i).cnt2) {
                        btn1.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
                        btn1.setTextColor(Color.BLACK);
                        btn2.setBackgroundColor(Color.WHITE);
                        btn2.setTextColor(Color.BLACK);
                    } else {
                        btn2.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                        btn2.setTextColor(Color.BLACK);
                        btn1.setBackgroundColor(Color.WHITE);
                        btn1.setTextColor(Color.BLACK);
                    }

                    btn1.setText((int) ((float) list.get(i).cnt1 / (list.get(i).cnt1 + list.get(i).cnt2) * 100) + "%\n" + list.get(i).btn1);
                    btn2.setText(100 - (int) ((float) list.get(i).cnt1 / (list.get(i).cnt1 + list.get(i).cnt2) * 100) + "%\n" + list.get(i).btn2);

                    btn1_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt1);
                    btn2_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt2);
                    btn1.setLayoutParams(btn1_params);
                    btn2.setLayoutParams(btn2_params);

                    IncCntRequest incCntRequest = new IncCntRequest(SaveSharedPreferences.getUserEmail(context), list.get(i).getId(), 1, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    queue.add(incCntRequest);
                }
            });

            //오른쪽 투표 버튼 클릭시 이벤트처리
            this.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //투표 시 버튼 비활성화
                    btn1.setEnabled(false);
                    btn2.setEnabled(false);

                    int i = getAdapterPosition();
                    list.get(i).incCnt2(); //항목2에 대한 득표수 증가
                    list.get(i).setVoted();//투표여부 체크

                    //버튼 폰트컬러, 배경색 변경
                    if (list.get(i).cnt1 > list.get(i).cnt2) {
                        btn1.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
                        btn1.setTextColor(Color.BLACK);
                        btn2.setBackgroundColor(Color.WHITE);
                        btn2.setTextColor(Color.BLACK);
                    } else {
                        btn2.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                        btn2.setTextColor(Color.BLACK);
                        btn1.setBackgroundColor(Color.WHITE);
                        btn1.setTextColor(Color.BLACK);
                    }

                    btn1.setText(100 - (int) ((float) list.get(i).cnt2 / (list.get(i).cnt1 + list.get(i).cnt2) * 100) + "%\n" + list.get(i).btn1);
                    btn2.setText((int) ((float) list.get(i).cnt2 / (list.get(i).cnt1 + list.get(i).cnt2) * 100) + "%\n" + list.get(i).btn2);

                    btn1_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt1);
                    btn2_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt2);
                    btn1.setLayoutParams(btn1_params);
                    btn2.setLayoutParams(btn2_params);

                    IncCntRequest incCntRequest = new IncCntRequest(SaveSharedPreferences.getUserEmail(context), list.get(i).getId(), 2, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(view.getContext());
                    queue.add(incCntRequest);
                }
            });

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

    public HomeAdapter(List<MyItem> list, Context context) {
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
        viewHolder.btn1.setText(list.get(i).btn1);
        viewHolder.btn2.setText(list.get(i).btn2);

        //img1 적용
        viewHolder.img1.setLayoutParams(img1_params1);
        System.out.println("--------------- 홈어뎁터" + list.get(i).img1);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                // TODO Auto-generated method stub
                try{
                    URL url1 = new URL(list.get(i).img1);
                    InputStream is = url1.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
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
        if (list.get(i).img2.equals("-1")) { //list.get(i).img2 == -1
            viewHolder.img2.setLayoutParams(img2_params2);

        //img2가 있는 경우(이미지 2개)
        } else {
            viewHolder.img2.setLayoutParams(img1_params1);
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {    // 오래 거릴 작업 구현
                    // TODO Auto-generated method stub
                    try{
                        URL url = new URL(list.get(i).img2);
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
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

        if(list.get(i).isClipped == 0){ //스크랩 안되어있는 경우 빈스크랩 아이콘 적용
            viewHolder.scrap.setImageResource(R.drawable.save_1);
        }else{ //스크랩 되어있는 경우 꽉찬 스크랩 아이콘 적용
            viewHolder.scrap.setImageResource(R.drawable.save_2);
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Log.i(TAG, "onBindViewHolder, responseListener 이미 투표된 게시글");
                        //버튼 폰트컬러, 배경색 변경
                        if (list.get(i).cnt1 > list.get(i).cnt2) { //왼쪽은 보라, 오른쪽은 핑크..
                            viewHolder.btn1.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
                            viewHolder.btn1.setTextColor(Color.BLACK);
                            viewHolder.btn2.setBackgroundColor(Color.WHITE);
                            viewHolder.btn2.setTextColor(Color.BLACK);
                        } else {
                            viewHolder.btn2.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                            viewHolder.btn2.setTextColor(Color.BLACK);
                            viewHolder.btn1.setBackgroundColor(Color.WHITE);
                            viewHolder.btn1.setTextColor(Color.BLACK);
                        }

                        //득표율 계산
                        viewHolder.btn1.setText((int) ((float) list.get(i).cnt1 / (list.get(i).cnt1 + list.get(i).cnt2) * 100) + "%\n" + list.get(i).btn1);
                        viewHolder.btn2.setText((int) ((float) list.get(i).cnt2 / (list.get(i).cnt1 + list.get(i).cnt2) * 100) + "%\n" + list.get(i).btn2);

                        //득표율에 따른 버튼 weight 변경
                        btn1_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt1);
                        viewHolder.btn1.setLayoutParams(btn1_params);
                        btn2_params = new LinearLayout.LayoutParams(0, 250, list.get(i).cnt2);
                        viewHolder.btn2.setLayoutParams(btn2_params);

                        //중복투표 방지
                        viewHolder.btn1.setEnabled(false);
                        viewHolder.btn2.setEnabled(false);
                    } else {
                        Log.i(TAG, "onBindViewHolder, responseListener 아직 투표 하지 않은 게시글");
                        //득표율에 따른 버튼 weight 변경
                        btn1_params = new LinearLayout.LayoutParams(0, 250, 1);
                        viewHolder.btn1.setLayoutParams(btn1_params);
                        btn2_params = new LinearLayout.LayoutParams(0, 250, 1);
                        viewHolder.btn2.setLayoutParams(btn2_params);

                        viewHolder.btn1.setBackgroundColor(Color.WHITE);
                        viewHolder.btn1.setTextColor(Color.BLACK);
                        viewHolder.btn2.setBackgroundColor(Color.WHITE);
                        viewHolder.btn2.setTextColor(Color.BLACK);

                        //중복투표 방지
                        viewHolder.btn1.setEnabled(true);
                        viewHolder.btn2.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Log.i(TAG, SaveSharedPreferences.getUserEmail(context));
        VoteRequest voteRequest = new VoteRequest(SaveSharedPreferences.getUserEmail(context),
                list.get(i).getId(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(voteRequest);
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //사용은 안하는데,, 이 밑에 두개가 비트맵 크기 조정하는 함수.. res > values > dimens.xml에서 item_width랑 item_height 둘다 200dp로 설정했는데, 이거는 이 함수 인자 newHeight, newWidth로 받으면 됨
    //함수사용은 final Bitmap bm = getResizedBitmap(decodeUri(is), 200,200); 이렇게 쓰면 됨.. 여기서 200처럼 직접 숫자 넣지 말고 바로 밑 주석처럼 res >values> dimens.xml에서 item_width, item_height 사용하길 권장....
    //bm = getResizedBitmap(decodeUri(bis), getResources().getDimensionPixelSize(R.dimen.idcard_pic_height), getResources().getDimensionPixelSize(R.dimen.idcard_pic_width));
    //                        bis.close();
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

    private Bitmap decodeUri(InputStream bis) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(bis, null, o);

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
        return BitmapFactory.decodeStream(bis, null, o2);
    }
}