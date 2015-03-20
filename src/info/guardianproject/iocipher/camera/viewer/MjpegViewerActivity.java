package info.guardianproject.iocipher.camera.viewer;


import info.guardianproject.iocipher.File;
import info.guardianproject.iocipher.FileInputStream;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MjpegViewerActivity extends Activity {
    private static final String TAG = "MjpegActivity";

    private MjpegView mv;
    private AudioTrack at;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mv = new MjpegView(this);
        setContentView(mv);        

        final String ioCipherVideoPath = getIntent().getExtras().getString("video");
        final String ioCipherAudioPath = getIntent().getExtras().getString("audio");
        
        try {

	        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
	     //   mv.showFps(true);

			File fileAudio = new File(ioCipherAudioPath);
			if (fileAudio.exists())
			{
				initAudio(ioCipherAudioPath);
				new Thread ()
				{
					public void run ()
					{
						try {							
							playAudio();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
			}
			mv.setSource(new MjpegInputStream(new FileInputStream(ioCipherVideoPath)));
	        

		        
	    	} 
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    InputStream isAudio = null;
    
    public void initAudio(String vfsPath) throws IOException {

        
        int sampleRate = 11025;// AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);

        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)*8;
        
        isAudio = new BufferedInputStream(new FileInputStream(vfsPath));

        at = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize, AudioTrack.MODE_STREAM);
    }
    
    public void playAudio () throws IOException
    {
        try{
        	byte[] music = null;
        	music = new byte[512];
            at.play();

            int i = 0;
            while((i = isAudio.read(music)) != -1)
                at.write(music, 0, i);

        } catch (IOException e) {
            e.printStackTrace();
        }

        at.stop();
        at.release();
        isAudio.close();
        at = null;
    }

    public void onPause() {
        super.onPause();
        mv.stopPlayback();
        
        if (at!=null)
        	at.stop();
        
    }

}