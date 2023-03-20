package com.example.api1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.RestrictionsManager;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


interface SpeechToTextService {
    @Multipart
    @POST("asr/v2/decode")
    Call<SpeechToTextResponse> recognizeSpeech(
            @Part MultipartBody.Part audio
    );
}
interface SpeechToTextResponse {
    String getStatus();

    String getTimeTaken();

    String getTranscript();

    String getVtt();

}
/*public class MainActivity extends AppCompatActivity {

    private EditText t1;
    private EditText t2;
    private EditText result;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://asr.iitm.ac.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient())
            .build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkTask().execute();
            }
        });
    }

    private class NetworkTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            String input_language = "hindi";
            String input_audio_path = "./input.mp3";
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("vtt","true")
                    .addFormDataPart("language","hindi")
                    .addFormDataPart("file","",
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    new File("pamgqBada/Recording.mp3")))
                    .build();
            Request request = new Request.Builder()
                    .url("https://asr.iitm.ac.in/asr/v2/decode ")
                    .method("POST", body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return input_language;
        }
            MediaRecorder recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/recording.3gp");
            try {
                recorder.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            recorder.start();
            File file = new File("recording.3gp");
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("audio/*"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

            SpeechToTextService service = retrofit.create(SpeechToTextService.class);
            Call<SpeechToTextResponse> call = service.recognizeSpeech(body);
            call.enqueue(new Callback<SpeechToTextResponse>() {

                @Override
                public void onResponse(Call<SpeechToTextResponse> call, retrofit2.Response<SpeechToTextResponse> response) {
                    if (response.isSuccessful()) {
                        SpeechToTextResponse speechToTextResponse = response.body();
                        // Do something with the response
                        Log.d("API Response", speechToTextResponse.getTranscript());
                    } else {
                        // Handle error
                        Log.d("API Error", response.message());
                    }
                }

                @Override
                public void onFailure(Call<SpeechToTextResponse> call, Throwable t) {
                    System.out.println("Failed");
                    // Handle failure
                }
            });
            recorder.stop();
            recorder.release();
            return null;
        }
        @Override
        protected void onPostExecute(String resultText) {
            result.setText(resultText);
        }
    }
}*/
public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://asr.iitm.ac.in/";
    private static final String LANGUAGE = "hindi";
    private static final String FILE_TYPE = "audio/wav";

    private Button recordButton;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private SpeechToTextService speechToTextService;

    private static int MICROPHONE_PERMISSION_CODE=200;
    private Context ContextCompact;
    private RestrictionsManager ActivityCompact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isMicroPhonePresent())
          {
            getMicrophonePermission();
          }

        recordButton = findViewById(R.id.button);

        speechToTextService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpeechToTextService.class);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
    }

    private boolean isMicroPhonePresent()
    {
     if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
       {
        return true;
       }
     else
       {
        return false;
       }
    }

    private void getMicrophonePermission()
      {
       if(ContextCompact.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED)
         {
          ActivityCompact.requestPermission(this,new String[] {android.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
         }
      }

     private String getRecordingFilePath()
     {
         ContextWrapper cw = new ContextWrapper(getApplicationContext());
         File musicDirectory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
         File file = new File(musicDirectory,"Recording"+".mp3");
         return file.getPath();
     }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //audioFilePath = getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
        mediaRecorder.setOutputFile(getRecordingFilePath());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordButton.setText("Recording...");
        recordButton.setEnabled(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording();
                sendAudioFile();
            }
        }, 5000); // stop recording after 5 seconds
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        recordButton.setText("Record");
        recordButton.setEnabled(true);
    }

    private void sendAudioFile() {
        File audioFile = new File(getRecordingFilePath());
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(FILE_TYPE), audioFile);
        MultipartBody.Part multipartBody =
                MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile);

        Call<SpeechToTextResponse> call = speechToTextService.recognizeSpeech(multipartBody);
        call.enqueue(new Callback<SpeechToTextResponse>() {
            /*@Override
            public void onResponse(Call<SpeechToTextResponse> call, Response<SpeechToTextResponse> response) {
                if (response.isSuccessful()) {
                    String transcript = response.body().getTranscript();
                    // Handle the transcript as needed
                } else {
                    // Handle the error case
                }
            }*/

            @Override
            public void onResponse(Call<SpeechToTextResponse> call, retrofit2.Response<SpeechToTextResponse> response) {
                if (response.isSuccessful()) {
                    String transcript = response.body().getTranscript();
                    // Handle the transcript as needed
                } else {
                    System.out.println("Response error");
                    // Handle the error case
                }
            }

            @Override
            public void onFailure(Call<SpeechToTextResponse> call, Throwable t) {
                // Handle the failure case
                System.out.println("Failed");
            }
        });
    }
}