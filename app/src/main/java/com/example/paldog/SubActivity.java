package com.example.paldog;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


class Data{

     Bitmap temp;
     int loc;
     int height;
     int HP;
     boolean isMOVE = true;
     int charNUM;
     int attack;
     int animCount=0;

    public Data(Bitmap temp, int loc, int height, int HP ,int attack, int charNUM){
        this.temp = temp;
        this.loc = loc;
        this.height = height;
        this.HP = HP;
        this.attack = attack;
        this.charNUM = charNUM;
    }

}

class GameView extends View {

    public final List<Data> IMG = new CopyOnWriteArrayList<Data>();
    public final List<Data> IMG2 = new CopyOnWriteArrayList<Data>();

    int total = 0;
    int money = 500;
    int d = 0, d2 = 100;
    int w, h ;

    GameThread T;
    AttackThread T2;
    Random rand = new Random();

    Bitmap [] monsterArr_2=new Bitmap[8];
    Bitmap [] monsterArr_1=new Bitmap[8];
    Bitmap [] monsterAct_1=new Bitmap[4];
    Bitmap [] monsterAct_2=new Bitmap[4];

    Bitmap [] playerArr_1=new Bitmap[8];
    Bitmap [] playerAct_1=new Bitmap[4];
    Bitmap [] playerArr_2=new Bitmap[8];
    Bitmap [] playerAct_2=new Bitmap[4];

    Bitmap bg;
    Bitmap player1;
    Bitmap player2;
    Bitmap underbar;
    Bitmap candy;
    boolean isInit=false;

    //타격 이펙트 사운드
    private SoundPool soundManager;
    int attackSount;
    int scratchSound;
    int scratchSound2;
    int swordattack;

    public GameView(Context context){

        super(context);
        T = new GameThread();
        T2 = new AttackThread();
        T2.setPriority(7);
        T.setPriority(5);

        T.start();
        T2.start();

    }

    public  void InitSounds()
    {
        soundManager= new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        attackSount = soundManager.load(getContext(), R.raw.jab, 1);
        swordattack = soundManager.load(getContext(), R.raw.sword, 1);
        scratchSound = soundManager.load(getContext(), R.raw.bite, 1);
        scratchSound2 = soundManager.load(getContext(), R.raw.scratch,1);
    }

    public void InitBitmaps()
    {

        w = getWidth();
        h = getHeight();

        //걷기 이미지 초기화
        for(int i=0;i<8;i++)
        {
            //몬스터2 걷기 이미지
            monsterArr_2[i]=BitmapFactory.decodeResource(getResources(), R.drawable.monster2_0 + i);
            monsterArr_2[i]= Bitmap.createScaledBitmap(monsterArr_2[i], w / 8, h / 4, true);
            //몬스터1 걷기 이미지
            monsterArr_1[i]=BitmapFactory.decodeResource(getResources(), R.drawable.monster1_0 + i);
            monsterArr_1[i]= Bitmap.createScaledBitmap(monsterArr_1[i], w / 8, h / 4, true);
            //플레이어 1 걷기 이미지
            playerArr_1[i]= BitmapFactory.decodeResource(getResources(), R.drawable.character1_0 + i);
            playerArr_1[i] = Bitmap.createScaledBitmap(playerArr_1[i], w / 8, h/4, true);
            //플레이어 2 걷기 이미지
            playerArr_2[i]= BitmapFactory.decodeResource(getResources(), R.drawable.character2_0 + i);
            playerArr_2[i] = Bitmap.createScaledBitmap(playerArr_2[i], w / 8, h/4, true);

        }
        //공격 이미지 초기화
        for(int i = 0; i<4; i++){
            //몬스터 1 공격 이미지
            monsterAct_1[i]=BitmapFactory.decodeResource(getResources(), R.drawable.monsteract1_0 + i);
            monsterAct_1[i]= Bitmap.createScaledBitmap(monsterAct_1[i], w / 8, h / 4, true);
            //몬스터 2 공격 이미지
            monsterAct_2[i]=BitmapFactory.decodeResource(getResources(), R.drawable.monsteract2_0 + i);
            monsterAct_2[i]= Bitmap.createScaledBitmap(monsterAct_2[i], w / 8, h / 4, true);
            //플레이어 1 공격 이미지
            playerAct_1[i]= BitmapFactory.decodeResource(getResources(), R.drawable.attack1_0 + i);
            playerAct_1[i]= Bitmap.createScaledBitmap(playerAct_1[i], w / 8, h / 4, true);
            //플레이어 2 공격 이미지
            playerAct_2[i]= BitmapFactory.decodeResource(getResources(), R.drawable.attack2_0 + i);
            playerAct_2[i]= Bitmap.createScaledBitmap(playerAct_2[i], w / 8, h / 4, true);
        }

        bg= BitmapFactory.decodeResource(getResources(), R.drawable.pic1);// 메인 벡그라운드 이미지
        bg =  Bitmap.createScaledBitmap(bg, w , h, true);

        //ui 이미지 초기화
        player1 = BitmapFactory.decodeResource(getResources(), R.drawable.character1_0); //플레이어1 ui
        player1 = Bitmap.createScaledBitmap(player1, (int)(w / 8*0.75), (int)(h / 4*0.75), true);
        player2 = BitmapFactory.decodeResource(getResources(), R.drawable.character2_0); //플레이어2 ui
        player2 = Bitmap.createScaledBitmap(player2,  (int)(w / 8*0.75), (int)(h / 4*0.75), true);
        underbar = BitmapFactory.decodeResource(getResources(), R.drawable.under); //캐릭터바 ui
        underbar = Bitmap.createScaledBitmap(underbar, w , (int)(h/(1.2)) , true);
        candy = BitmapFactory.decodeResource(getResources(), R.drawable.candy);
        candy =  Bitmap.createScaledBitmap(candy, w/10, w/10 , true);

        isInit=true;
    }

    public void onDraw(Canvas canvas){
        Paint p = new Paint();
        w = getWidth();
        h = getHeight();

        if(!isInit) {
            InitSounds();
            InitBitmaps();
        }
        p.setColor(Color.WHITE);
        p.setTextSize(50);
        canvas.drawBitmap(bg,  0, 0, null); //백그라운드 이미지
        canvas.drawText(" "+ money, 200 ,130 ,p); // 사탕개수

        total+= 1;

        //Bitmap BackGround = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);

        if(T.run == false){
            Bitmap Loose = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
            Bitmap GameOver = BitmapFactory.decodeResource(getResources(), R.drawable.gameoverletter);
            Loose = Bitmap.createScaledBitmap(Loose, w / 2, h / 2, true);
            GameOver = Bitmap.createScaledBitmap(GameOver, w / 2, h / 2, true);
            canvas.drawBitmap(Loose, w/4, 0, null);
            canvas.drawBitmap(GameOver, w/4, 100, null);
        }


        //적군 생성과 시간
        if(total % 50 == 0) {

            IMG2.add(new Data(monsterArr_1[0], w - 250, rand.nextInt(4)*(-20) + (h*4/10), 90,20,2));

        }
        else if( total %  149 == 0){

            total = 0;
            IMG2.add(new Data(monsterArr_2[0], w - 250,rand.nextInt(4)*(-20) + (h*4/10) , 140,30,1));

        }


        //플레이어 애니메이션 컨트롤
        for(int i = 0; i< IMG.size(); i++){
            canvas.drawBitmap(IMG.get(i).temp, IMG.get(i).loc, IMG.get(i).height, null);

            // 플레이어 1 걷기 상태일때
            if(IMG.get(i).isMOVE == true && IMG.get(i).charNUM==3 &&IMG.size()>0){
                IMG.get(i).temp =  playerArr_1[IMG.get(i).animCount];
            }
            //플레이어 1 공격 상태일때
            else if(IMG.get(i).isMOVE == false && IMG.get(i).charNUM == 3 && IMG.size()>0){
                if(IMG.get(i).animCount ==3) //공격시 사운드이펙트 재생
                    soundManager.play(attackSount,0.8f,0.8f,0,0,1);

                if(IMG.size()>0) {

                    if (IMG.get(i).animCount > 3) {
                        IMG.get(i).animCount = 0;
                    }

                    IMG.get(i).temp = playerAct_1[IMG.get(i).animCount];
                }

            }

            //플레이어 2 걷기상태일때
            else if(IMG.get(i).isMOVE == true && IMG.get(i).charNUM==4 &&IMG.size()>0){
                IMG.get(i).temp =  playerArr_2[IMG.get(i).animCount];
            }
            //플레이어 2 공격 상태일때
            else if(IMG.get(i).isMOVE == false && IMG.get(i).charNUM == 4 && IMG.size()>0){

                if(IMG.get(i).animCount ==3) //공격시 사운드이펙트 재생
                    soundManager.play(swordattack,0.8f,0.8f,0,0,1);

                if(IMG.size()>0) {

                    if (IMG.get(i).animCount > 3) {
                        IMG.get(i).animCount = 0;
                    }

                    IMG.get(i).temp = playerAct_2[IMG.get(i).animCount];
                }


            }

            // 다음 이미지로 설정
            if(IMG.size() > 0) {
                IMG.get(i).animCount++;

                if (IMG.get(i).animCount == 8) {
                    IMG.get(i).animCount = 0;
                }
            }

        }
        //적 몬스터 애니메이션 컨트롤
        for(int i = 0; i< IMG2.size(); i++){
            canvas.drawBitmap(IMG2.get(i).temp, IMG2.get(i).loc, IMG2.get(i).height, null);
            //System.out.println(i + "번째" + IMG2.get(i).animCount);

            if(IMG2.get(i).isMOVE==true && IMG2.get(i).charNUM == 1 && IMG2.size() > 0 ) { // index : 0 size: 0 => 추측: 인덱스 번호 0에 접근하려하는데, 0위치의 배열이 존재하지 않아서, 이미지 값을 변경 시켜줄 수 없는 상태로 오류발생!
                IMG2.get(i).temp = monsterArr_2[IMG2.get(i).animCount];
            }
            else if(IMG2.get(i).isMOVE==false&& IMG2.get(i).charNUM == 1 && IMG2.size() > 0 )
            {

                if(IMG2.get(i).animCount ==2)
                    soundManager.play(scratchSound2,1f,1f,0,0,1);

                if(IMG2.size()>0) {
                    if (IMG2.get(i).animCount > 3) {
                        IMG2.get(i).animCount = 0;
                    }
                    IMG2.get(i).temp = monsterAct_2[IMG2.get(i).animCount];
                }

            }
            else if(IMG2.get(i).isMOVE==true && IMG2.get(i).charNUM ==2){
                IMG2.get(i).temp = monsterArr_1[IMG2.get(i).animCount];
            }
            else if(IMG2.get(i).isMOVE == false && IMG2.get(i).charNUM == 2){

                if(IMG2.get(i).animCount ==2)
                    soundManager.play(scratchSound,1f,1f,0,0,1);

                if(IMG2.size()>0) {
                    if (IMG2.get(i).animCount > 3) {
                        IMG2.get(i).animCount = 0;
                    }

                    IMG2.get(i).temp = monsterAct_1[IMG2.get(i).animCount];

                }

            }

            //공격 소리 재생

            if(IMG2.size() >0) {

                IMG2.get(i).animCount++;

                if (IMG2.get(i).animCount == 8) {
                    IMG2.get(i).animCount = 0;
                }

            }

            //7 넘어갈경우 0으로 바꿈
        }

        //ui 배치
        canvas.drawBitmap(underbar, 0 , h- (int)(h/(1.2)), null);
        canvas.drawBitmap(candy,  50, 50, null);
        canvas.drawBitmap(player1,  60, 812, null);
        canvas.drawBitmap(player2,  190+(int)(w / 8*0.75), 812, null);

        //플례이어 컈릮터 1 가격글씨
        p.setTextSize(40);
        canvas.drawText("200", 120 ,h-20 ,p);

        //플례이어 컈릭터 2 가격글씨
        canvas.drawText("300", 230+(int)(w / 8*0.75) ,h-20 ,p);
    }

    //플레이어 생성하는 메소드
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_DOWN){

            if( (int)event.getY() > h/2 && (int) event.getX() <w/8 && money >=200){
                money -= 200;
                Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.character1_0);
                temp = Bitmap.createScaledBitmap(temp, w/8, h/4, true);
                IMG.add(new Data(temp,d2, rand.nextInt(4)*(-20) + (h*4/10) , 120,10,3));

            }
            else if((int)event.getY() > h/2 && (int) event.getX() > 190+(int)(w / 8*0.75) && (int) event.getX() < 190+(int)(w / 8*0.75)*2 && money >= 300){
                money -= 300;
                Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.character2_0);
                temp = Bitmap.createScaledBitmap(temp, w/8, h/4, true);
                IMG.add(new Data(temp,d2, rand.nextInt(4)*(-20) + (h*4/10) , 80,40,4));
            }


        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        T.run = false;
        T2.run = false;
        super.onDetachedFromWindow();
    }



    class AttackThread extends Thread{
            boolean run = true;
        public void run() {

            while(run) {

                money += 15;
                //System.out.println("사이즈: " + IMG.size());


                //player 충돌
                for (int i = 0; i < IMG.size(); i++) {
                    //System.out.println("체력 : " + i + "의 " +IMG.get(i).HP);

                    if(IMG2.size() >0) {
                        if (IMG.get(i).loc  > IMG2.get(0).loc - 200) { //충돌했을경우

                            if(IMG.get(i).isMOVE == true){

                                IMG.get(i).isMOVE = false;

                            }

                        }
                        else{
                            IMG.get(i).isMOVE = true;
                        }

                    } else{
                        IMG.get(i).isMOVE = true;
                    }

                }

                for (int i = 0; i < IMG2.size(); i++) {

                        if(IMG.size()>0) {
                            if (IMG.get(0).loc > IMG2.get(i).loc - 200) {

                                if (IMG2.get(i).isMOVE == true) {

                                    IMG2.get(i).isMOVE = false;

                                }

                            }
                            else{
                                IMG2.get(i).isMOVE = true;
                            }
                        }
                        else{
                            IMG2.get(i).isMOVE = true;
                        }
                }

                //공격 부분

                    for(int i = 0; i < IMG.size(); i++){

                        if(IMG.get(i).isMOVE == false && IMG2.size() >0){

                            IMG2.get(0).HP-=IMG.get(i).attack;

                            if(IMG2.get(0).HP <=0){
                                money += 100;

                                int temp = rand.nextInt(100);

                                if(IMG2.get(0).charNUM==1){
                                    System.out.println("temp: " + temp);
                                    if(temp <= 30){

                                        money = 0;
                                    }

                                }

                                IMG2.remove(0);
                            }

                        }

                    }

                for(int i = 0; i < IMG2.size(); i++){

                    if(IMG2.get(i).isMOVE == false && IMG.size() >0){
                        IMG.get(0).HP-=IMG2.get(i).attack;

                        if(IMG.get(0).HP <=0){
                            IMG.remove(0);
                        }

                    }

                }


                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    class GameThread extends Thread {
        public boolean run = true;

        public void run() {
            while(run) {
                try {
                    postInvalidate();

                    for(int i = 0; i<IMG.size(); i++){
                        if(IMG.get(i).isMOVE == true) {
                            IMG.get(i).loc += 5;
                        }
                    }


                    for(int i = 0; i<IMG2.size(); i++){
                        if(IMG2.get(i).isMOVE == true) {
                            IMG2.get(i).loc -= 5;
                        }
                    }

                    if(IMG2.size() >0){
                        if(IMG2.get(0).loc <100 ) {
                            //System.out.println("finish");
                            onDetachedFromWindow();
                        }
                    }
                    else if(IMG.size() >0){
                        if(IMG.get(0).loc >w - 250 ) {
                            //System.out.println("finish");
                            onDetachedFromWindow();
                        }
                    }

                    sleep(100);

                }
                 catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}





public class  SubActivity extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // SubActivity를 생성하면 manifests안에 등록을 해줘야한다.
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(new GameView(this));
        // 이부분은 화면을 누워서 출력하게 해준다. -> 게임에서 주로 사용하는 것

        /*

            19.12.05(금요일)

            6.PPT 만들기

         */

    }
}
