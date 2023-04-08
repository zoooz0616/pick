package com.example.choice;

public class MyItem {
    int id;
    String date; //게시글 작성일
    String title; //게시글 제목
    String img1, img2;
    String btn1; //투표 버튼의 항목명
    String btn2;
    int cnt1, cnt2; //항목1,2의 득표수
    int isClipped; //스크랩 여부
    boolean isVoted; //해당 게시글 투표 여부

    public MyItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public void setBtn1(String btn1) {
        this.btn1 = btn1;
    }

    public void setBtn2(String btn2) {
        this.btn2 = btn2;
    }

    public void setCnt1(int cnt1) {
        this.cnt1 = cnt1;
    }

    public void setCnt2(int cnt2) {
        this.cnt2 = cnt2;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public int getIsClipped() { return isClipped; }

    public void setIsClipped(int isClipped) { this.isClipped = isClipped; }

    @Override
    public String toString() {
        return date;
    }

    //버튼 클릭 이벤트시 득표수 1증가
    public void incCnt1() {
        cnt1++;
    }
    //버튼 클릭 이벤트시 득표수 1증가
    public void incCnt2() {
        cnt2++;
    }

    public void setVoted() {
        isVoted = true;
    }
}
