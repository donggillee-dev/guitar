package cse.ssu.guitar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MakeSheetFragment extends Fragment {

    View view;

    public static MakeSheetFragment newInstance() {
        return new MakeSheetFragment();
    }

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXETERNAL_STORAGE = 1;
    ToggleButton listenBtn;
    Button play, stop;
    // Environment.getExternalStorageDirectory()로 각기 다른 핸드폰의 내장메모리의 디렉토리를 알수있다.
    final private static File RECORDED_FILE = Environment.getExternalStorageDirectory();
    private String filename;
    private String time;
    private String date;
    private String filepath;
    private ImageView loader;
    private Animation animation;

    // MediaRecorder 클래스에  녹음에 관련된 메서드와 멤버 변수가 저장되어있다.
    MediaRecorder recorder;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_make_sheet, container, false);
        loader = (ImageView)view.findViewById(R.id.loader);
        listenBtn = (ToggleButton) view.findViewById(R.id.listenBtn);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // 권한 없음
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // 권한 없음
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXETERNAL_STORAGE);
        }
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenBtn.isChecked() == true) {
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
                    Log.v("time", time);
                    filename = RECORDED_FILE.getAbsolutePath() + "/SSUGuitar/" + time + ".mp3";

                    // 저장될 파일 지정
                    recorder.setOutputFile(filename);
                    try {
                        Toast.makeText(getActivity(), "녹음이 시작되었습니다.", Toast.LENGTH_LONG).show();
                        animation = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
                        loader.startAnimation(animation);
                        // 녹음 준비,시작
                        recorder.prepare();
                        recorder.start();
                    } catch (Exception ex) {
                        Log.e("SampleAudioRecorder", "Exception : ", ex);
                    }
                } else {
                    if (recorder == null)
                        return;
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    Toast.makeText(getActivity(),
                            "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
                    // TODO Auto-generated method stub
                    loader.clearAnimation();
                    animation.setAnimationListener(null);
                    Bundle bundle = new Bundle();
                    Fragment fragment = SheetFragment.newInstance();

                    bundle.putString("name", time);
                    bundle.putString("date", date);
                    fragment.setArguments(bundle);
                    replaceFragment(fragment);
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
}
