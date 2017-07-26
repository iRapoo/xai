package org.dualcom.xai;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dualcom.xai.MyClass.Storage;
import org.dualcom.xai.MyClass.Windows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.logging.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class TeacherActivity extends Activity {

    public AQuery aq;
    public String response;
    public Context context = this;
    public TextView ProfileName;
    public TextView ProfileRank;
    public TextView ProfileExtra;
    public ImageView ProfileIMG;
    public TextView LoadText;
    public ImageButton ProfileEdit;
    public ImageButton ProfileEditApply;
    public LinearLayout ProfileAvailable;
    public LinearLayout ProfileUpdate;
    public EditText ProfileEditName;
    public EditText ProfileEditRank;
    public EditText ProfileEditExtra;
    public String HOST = "http://rapoo.mysit.ru/teachers/";
    public String _def = null;
    public String URL = null;
    public StringBuffer buffer = null;
    private final int Pick_image = 1;

    ProgressDialog pd;
    Handler h;

    Bitmap bitmap;
    ProgressDialog pDialog;

    //Проверка доступности сети
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo == null) return false;
        boolean res = (!activeNetworkInfo.isConnected())?false:true;
        res = (!activeNetworkInfo.isAvailable())?false:true;
        return res;
    }
    /*************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7148094931915684/7613101450");

        AdView mAdView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);

        aq = new AQuery(this);

        Storage.saveData(context,"tmp_imageUrl", "false");

        ProfileName = (TextView) findViewById(R.id.ProfileName);
        ProfileRank = (TextView) findViewById(R.id.ProfileRank);
        ProfileExtra = (TextView) findViewById(R.id.ProfileExtra);
        ProfileIMG = (ImageView) findViewById(R.id.ProfileIMG);
        LoadText = (TextView) findViewById(R.id.LoadText);
        ProfileEdit = (ImageButton) findViewById(R.id.ProfileEdit);
        ProfileEditApply = (ImageButton) findViewById(R.id.ProfileEditApply);
        ProfileAvailable = (LinearLayout) findViewById(R.id.ProfileAvailable);
        ProfileUpdate = (LinearLayout) findViewById(R.id.ProfileUpdate);
        ProfileEditName = (EditText) findViewById(R.id.ProfileEditName);
        ProfileEditRank = (EditText) findViewById(R.id.ProfileEditRank);
        ProfileEditExtra = (EditText) findViewById(R.id.ProfileEditExtra);

        ProfileEdit.setOnClickListener(new View.OnClickListener() { //Вкл режима редактирования
            @Override
            public void onClick(View v) {

                ProfileEdit.setVisibility(ProfileAvailable.GONE);
                ProfileEditApply.setVisibility(ProfileUpdate.VISIBLE);

                ProfileAvailable.setVisibility(ProfileAvailable.GONE);
                ProfileUpdate.setVisibility(ProfileUpdate.VISIBLE);

                ProfileEditName.setText(ProfileName.getText());
                ProfileEditRank.setText(ProfileRank.getText());
                ProfileEditExtra.setText(ProfileExtra.getText());
                //ProgressUpload.setVisibility(View.VISIBLE);

                ProfileIMG.setClickable(true);

                aq.id(R.id.ProfileIMG).image(R.drawable.teacher_profile_upload);

                Windows.Open(context, getString(R.string.attention), getString(R.string.teacher_attention1));

            }
        });

        ProfileEditApply.setOnClickListener(new View.OnClickListener() { //Выкл режима редактирования
            @Override
            public void onClick(View v) {

                new FilesUploadingTask(Storage.loadData(context,"tmp_imageUrl"),
                        ProfileEditName.getText()+"", ProfileEditRank.getText()+"", ProfileEditExtra.getText()+"").execute();

            }
        });

        ProfileIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ProfileIMG.isClickable()) {
                    //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    //Тип получаемых объектов - image:
                    photoPickerIntent.setType("image/*");
                    //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                    startActivityForResult(photoPickerIntent, Pick_image);
                }

            }
        });



        Intent intent = getIntent();
        _def = intent.getStringExtra("teacher");
        ProfileName.setText(_def);
        ProfileRank.setVisibility(ProfileRank.GONE);
        ProfileExtra.setVisibility(ProfileExtra.GONE);

        if(isNetworkAvailable()) {
            setProfile();
            new MyAsyncTask().execute("getProfile.php", "value=" + _def);
        }else{
            setProfile();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){
                    try {

                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ProfileIMG.setImageBitmap(selectedImage);

                        Storage.saveData(context,"tmp_imageUrl", getPath(imageUri));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }}

    public String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }


    @SuppressWarnings("deprecation")
    class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                DefaultHttpClient hc = new DefaultHttpClient();
                HttpPost postMethod = new HttpPost(HOST+params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                int count = params.length;
                String[] param = null;

                for(int i = 1; i < count; i++) {
                    param = params[i].split("=");
                    nameValuePairs.add(new BasicNameValuePair(param[0], param[1]));
                }

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse httpResponse = hc.execute(postMethod);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity, "UTF-8");

            }catch(Exception e){
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            LoadText.setVisibility(LoadText.GONE);

            String[] params = res.split("///");

            Storage.saveData(context, _def+"", res);

            if(!params[1].equals("false"))
                ProfileName.setText(params[1]);
            if(!params[2].equals("false")) {
                ProfileRank.setText(params[2]);
                ProfileRank.setVisibility(ProfileRank.VISIBLE);
            }
            if(!params[3].equals("false")) {
                ProfileExtra.setText(params[3]);
                ProfileExtra.setVisibility(ProfileExtra.VISIBLE);
            }

            //Picasso.with(context).load("http://rapoo.mysit.ru/teachers/asd.php?img="+params[0]).into(ProfileIMG);
            aq.id(R.id.ProfileIMG).image(HOST+"getImage.php?img="+params[0], true, true, 0, R.drawable.teacher_profile, null, AQuery.FADE_IN);

        }
    }

    protected void setProfile(){

        if(!Storage.emptyData(context, _def + "")) {
            LoadText.setVisibility(LoadText.GONE);
            ProfileIMG.setImageResource(R.drawable.teacher_profile);

            String[] params = Storage.loadData(context, _def + "").split("///");

            if (!params[1].equals("false"))
                ProfileName.setText(params[1]);
            if (!params[2].equals("false")) {
                ProfileRank.setText(params[2]);
                ProfileRank.setVisibility(ProfileRank.VISIBLE);
            }
            if (!params[3].equals("false")) {
                ProfileExtra.setText(params[3]);
                ProfileExtra.setVisibility(ProfileExtra.VISIBLE);
            }

            aq.id(R.id.ProfileIMG).image(HOST + "getImage.php?img=" + params[0], true, true, 0, R.drawable.teacher_profile, null, AQuery.FADE_IN);
        }else {
            ProfileIMG.setImageResource(R.drawable.teacher_profile);
            LoadText.setVisibility(LoadText.GONE);
        }

        ProfileIMG.setClickable(false);

    }

    /**
     * Загружает файл на сервер
     */
    public class FilesUploadingTask extends AsyncTask<Void, Void, String> {

        // Конец строки
        private String lineEnd = "\r\n";
        // Два тире
        private String twoHyphens = "--";
        // Разделитель
        private String boundary =  "----WebKitFormBoundary9xFB2hiUhzqbBQ4M";

        // Переменные для считывания файла в оперативную память
        private int bytesRead, bytesAvailable, bufferSize;
        private byte[] buffer;
        private int maxBufferSize = 10*1024*1024;

        // Путь к файлу в памяти устройства
        private String filePath;
        private String name;
        private String rank;
        private String extra;

        // Адрес метода api для загрузки файла на сервер
        public static final String API_FILES_UPLOADING_PATH = "http://rapoo.mysit.ru/teachers/uploadImage.php";

        // Ключ, под которым файл передается на сервер
        public static final String FORM_FILE_NAME = "photo";



        public FilesUploadingTask(String filePath, String name, String rank, String extra) {
            this.filePath = filePath;
            this.name = name;
            this.rank = rank;
            this.extra = extra;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SpannableString ss2 = new SpannableString(getString(R.string.send_data));
            //ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

            pd = new ProgressDialog(context);
            pd.setMessage(ss2);
            pd.setCancelable(false);
            pd.show();

        }

        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(Void... params) {
            // Результат выполнения запроса, полученный от сервера
            String result = null;

            try {
                // Создание ссылки для отправки файла
                URL uploadUrl = new URL(API_FILES_UPLOADING_PATH);

                // Создание соединения для отправки файла
                HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();

                // Разрешение ввода соединению
                connection.setDoInput(true);
                // Разрешение вывода соединению
                connection.setDoOutput(true);
                // Отключение кеширования
                connection.setUseCaches(false);

                // Задание запросу типа POST
                connection.setRequestMethod("POST");

                // Задание необходимых свойств запросу
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                // Создание потока для записи в соединение
                //DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                BufferedWriter outputStream2 = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Формирование multipart контента
                addFormField(outputStream2, "value", _def);
                addFormField(outputStream2, "name", (name.length()>0) ? name : "false");
                addFormField(outputStream2, "rank", (rank.length()>0) ? rank : "false");
                addFormField(outputStream2, "extra", (extra.length()>0) ? extra : "false");

                if(!Storage.loadData(context,"tmp_imageUrl").equals("false")) {
                    // Начало контента
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    // Заголовок элемента формы
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                            FORM_FILE_NAME + "\"; filename=\"" + filePath + "\"" + lineEnd);
                    // Тип данных элемента формы
                    outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    // Конец заголовка
                    outputStream.writeBytes(lineEnd);

                // Поток для считывания файла в оперативную память
                FileInputStream fileInputStream = new FileInputStream(new File(filePath));


                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // Считывание файла в оперативную память и запись его в соединение
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    long totalBytesWritten = 0;

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);

                        totalBytesWritten += bufferSize;
                        //ProgressUpload.setProgress((int) (totalBytesWritten * 100 / bytesAvailable));

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                        //ProgressUpload.setProgress(Integer.valueOf((int) (totalBytesWritten / 1024L)));
                    }

                    // Конец элемента формы
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Получение ответа от сервера
                    int serverResponseCode = connection.getResponseCode();

                    // Закрытие соединений и потоков
                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    // Считка ответа от сервера в зависимости от успеха
                    if(serverResponseCode == 200) {
                        result = readStream(connection.getInputStream());
                    } else {
                        result = readStream(connection.getErrorStream());
                    }

                }else {


                    // Конец элемента формы
                    //outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Получение ответа от сервера
                    int serverResponseCode = connection.getResponseCode();

                    // Закрытие соединений и потоков
                    outputStream.flush();
                    outputStream.close();

                    // Считка ответа от сервера в зависимости от успеха
                    if(serverResponseCode == 200) {
                        result = readStream(connection.getInputStream());
                    } else {
                        result = readStream(connection.getErrorStream());
                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        public void addFormField(BufferedWriter dos, String parameter, String value){
            try {
                dos.write(twoHyphens + boundary + lineEnd);
                dos.write("Content-Disposition: form-data; name=\""+parameter+"\"" + lineEnd);
                dos.write("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                dos.write(lineEnd);
                dos.write(value + lineEnd);
                dos.flush();
            }
            catch(Exception e){

            }
        }

        // Считка потока в строку
        public String readStream(InputStream inputStream) throws IOException {
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            if (pd.isShowing()) {
                pd.dismiss();
            }

            ProfileEdit.setVisibility(ProfileAvailable.VISIBLE);
            ProfileEditApply.setVisibility(ProfileUpdate.GONE);

            ProfileAvailable.setVisibility(ProfileAvailable.VISIBLE);
            ProfileUpdate.setVisibility(ProfileUpdate.GONE);

            ProfileEditName.getText();
            ProfileEditRank.getText();
            ProfileEditExtra.getText();
            //ProgressUpload.setVisibility(View.GONE);

            ProfileIMG.setClickable(false);

            if(isNetworkAvailable()) {
                setProfile();
                new MyAsyncTask().execute("getProfile.php", "value=" + _def);
            }else{
                setProfile();
            }

            Storage.saveData(context,"tmp_imageUrl", "false");

            Windows.Open(context, getString(R.string.thank4add), getString(R.string.teacher_attention2));

        }

    }

}
