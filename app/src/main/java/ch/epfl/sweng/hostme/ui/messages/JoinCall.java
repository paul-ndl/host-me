package ch.epfl.sweng.hostme.ui.messages;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import ch.epfl.sweng.hostme.R;
import ch.epfl.sweng.hostme.database.Auth;
import ch.epfl.sweng.hostme.database.Database;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

@RequiresApi(api = Build.VERSION_CODES.S)
public class JoinCall extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_CONNECT};
    private RtcEngine mRtcEngine;
    private ImageView audioButt;
    private ImageView leaveButt;
    private ImageView videoButt;
    private final static CollectionReference reference = Database.getCollection("users");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initAgoraEngine();
        }
        audioButt = findViewById(R.id.audioBtn);
        audioButt.setOnClickListener(l -> onAudioMuteClicked(audioButt));
        leaveButt = findViewById(R.id.leaveBtn);
        leaveButt.setOnClickListener(l -> onLeaveChannelClicked());
        videoButt = findViewById(R.id.videoBtn);
        videoButt.setOnClickListener(l -> onVideoMuteClicked(videoButt));
        findViewById(R.id.joinBtn).setVisibility(View.GONE);
        joinChannel();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }

    private void setupLocalVideoFeed() {
        FrameLayout videoContainer = findViewById(R.id.floating_video_container);
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoSurface.setZOrderMediaOverlay(true);
        videoContainer.addView(videoSurface);
        mRtcEngine.setupLocalVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            initAgoraEngine();
        }
    }

    private void initAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        setupSession();
    }

    private void setupSession() {
        mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_COMMUNICATION);
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    public void joinChannel() {
        reference.document(Auth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String roomName = document.getString("roomName");
                    mRtcEngine.joinChannel(null, roomName, null, 0);
                    setupLocalVideoFeed();
                    audioButt.setVisibility(View.VISIBLE);
                    leaveButt.setVisibility(View.VISIBLE);
                    videoButt.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRemoteVideoStream(int uid) {
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoContainer.addView(videoSurface);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, uid));
        mRtcEngine.setRemoteSubscribeFallbackOption(io.agora.rtc.Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY);
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                    "Join channel success, uid: " + (uid & 0xFFFFFFFFL), Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideoStream(uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> onRemoteUserLeft());
        }

        @Override
        public void onRemoteVideoStateChanged(final int uid, final int state, int reason, int elapsed) {
            runOnUiThread(() -> onRemoteUserVideoToggle(state));
        }
    };

    private void onRemoteUserVideoToggle(int state) {
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);

        SurfaceView videoSurface = (SurfaceView) videoContainer.getChildAt(0);
        videoSurface.setVisibility(state == 0 ? View.GONE : View.VISIBLE);

        if (state == 0) {
            ImageView noCamera = new ImageView(this);
            noCamera.setImageResource(R.drawable.video_disabled);
            videoContainer.addView(noCamera);
        } else {
            ImageView noCamera = (ImageView) videoContainer.getChildAt(1);
            if (noCamera != null) {
                videoContainer.removeView(noCamera);
            }
        }
    }

    private void onRemoteUserLeft() {
        removeVideo(R.id.bg_video_container);
    }


    public void onLeaveChannelClicked() {
        leaveChannel();
        removeVideo(R.id.floating_video_container);
        removeVideo(R.id.bg_video_container);
        audioButt.setVisibility(View.GONE); // set the audio button hidden
        leaveButt.setVisibility(View.GONE); // set the leave button hidden
        videoButt.setVisibility(View.GONE); // set the video button hidden
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    private void removeVideo(int containerID) {
        FrameLayout videoContainer = findViewById(containerID);
        videoContainer.setVisibility(View.GONE);
        videoContainer.removeAllViews();
    }

    public void onAudioMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.audio_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.audio_toggle_active_btn);
        }
        mRtcEngine.muteLocalAudioStream(btn.isSelected());
    }

    public void onVideoMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.video_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.video_toggle_active_btn);
        }
        mRtcEngine.muteLocalVideoStream(btn.isSelected());

        FrameLayout container = findViewById(R.id.floating_video_container);
        container.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
        SurfaceView videoSurface = (SurfaceView) container.getChildAt(0);
        videoSurface.setZOrderMediaOverlay(!btn.isSelected());
        videoSurface.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
    }
}