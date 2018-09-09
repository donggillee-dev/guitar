package cse.ssu.guitar;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import Network.PostAudioFile;

public class MakeSheetFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXETERNAL_STORAGE = 1;
    // Environment.getExternalStorageDirectory()로 각기 다른 핸드폰의 내장메모리의 디렉토리를 알수있다.
    final private static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    private ToggleButton listenBtn;
    // MediaRecorder 클래스에  녹음에 관련된 메서드와 멤버 변수가 저장되어있다.
    private MediaRecorder recorder;
    private View view;
    private String filename;
    private String time;
    private String date;
    private String filepath;
    private ImageView loader;
    private Animation animation;
    private boolean check;
    private String response;
    private ProgressDialog mProgressDialog;



    public static MakeSheetFragment newInstance() {
        return new MakeSheetFragment();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_make_sheet, container, false);
        loader = (ImageView)view.findViewById(R.id.loader);
        listenBtn = (ToggleButton) view.findViewById(R.id.listenBtn);




        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenBtn.isChecked() == true) {
                    Toast.makeText(getActivity(), "녹음이 시작되었습니다.", Toast.LENGTH_LONG).show();
                    animation = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
                    loader.startAnimation(animation);
                    startRecording startRecording = new startRecording();
                    startRecording.start();

                } else {
                    loader.clearAnimation();
                    animation.setAnimationListener(null);
                    stopRecording stopRecording = new stopRecording();
                    stopRecording.execute();
//                    stopRecording.start();
//                    try {
//                        stopRecording.join();
//                        Log.v("***","finish join");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    Log.v("***","finish dismiss");
                }
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

    private class stopRecording extends AsyncTask<Void, Void, Void> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)

        protected void onPreExecute(){
            mProgressDialog = ProgressDialog.show(
                    getActivity(), "분석 중입니다",
                    "잠시만 기다려주세요..");

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (recorder == null)
                return null;

            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            SendAudioFile task = new SendAudioFile();
            task.start();

            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(Void nv) {
            super.onPostExecute(nv);
            mProgressDialog.dismiss();
            Log.v("before bundle", "***");
            if(check == true) {
                Log.v("response in make sheet", response);
                Bundle bundle = new Bundle();
                Fragment fragment = SheetFragment.newInstance();
                bundle.putString("response", response);
                bundle.putString("name", time);
                bundle.putString("date", date);
                bundle.putInt("flag",2);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        }
    }

    private class SendAudioFile extends Thread {
        @Override
        public void run() {
            super.run();

            Log.v("in send audiofile", "***");
            PostAudioFile tm = new PostAudioFile();
            response = null;
            try {
                response = tm.post(MainActivity.serverUrl+"record",filename, time);
                if(response != null)
                    check = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class startRecording extends Thread {
        @Override
        public void run() {
            super.run();
            Log.v("in start recording", "***");
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            // 실험 결과 왠만하면 아래 recorder 객체의 속성을 지정하는 순서는 이대로 하는게 좋다 위치를 바꿨을때 에러가 났었음
            // 녹음 시작을 위해  MediaRecorder 객체  recorder를 생성한다.
            recorder = new MediaRecorder();
            // 오디오 입력 형식 설정
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setAudioSamplingRate(44100);
            recorder.setAudioEncodingBitRate(96000);
            // 음향을 저장할 방식을 설정
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 오디오 인코더 설정
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            filepath = RECORDED_FILE.getAbsolutePath() + "/SSUGuitar";
            File file = new File(filepath);
            if(!file.exists()){
                file.mkdirs();
            }

            //파일명 임의로 생성
            SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            SimpleDateFormat format_2 = new SimpleDateFormat("yyyy-MM-dd");
            Date currentTime = new Date();
            time = format.format(currentTime);
            date = format_2.format(currentTime);
            filename = RECORDED_FILE.getAbsolutePath() + "/SSUGuitar/" + time + ".mp3";

            // 저장될 파일 지정
            recorder.setOutputFile(filename);
            try {
                // 녹음 준비,시작
                recorder.prepare();
                recorder.start();
            } catch (Exception ex) {
                Log.e("SampleAudioRecorder", "Exception : ", ex);
            }
        }
    }
/*
    private class stopRecording extends Thread {
        @Override
        public void run() {
            super.run();
            Log.v("in stop recording", "***");
            if (recorder == null)
                return;

            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;

            try {
                han = new Handler(){
                    public void handleMessage(Message msg) {
                        Log.v("aaa","???");
                        mProgressDialog.dismiss();
                        boolean retry = true;
                        while (retry) {

                            Log.v("aaa","count++");

                            retry = false;

                        }


                        Toast.makeText(getActivity(),
                                "종료되었습니다.", Toast.LENGTH_LONG).show();
                    }
                };
                SendAudioFile task = new SendAudioFile();
                //task.execute();
                task.start();
                task.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    */
}