package connect.app.com.connect.retrofit;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by 44260 on 2016/3/16.
 */
public class Retro {

    private Retrofit retrofit;
    private Account account;
    private Repo version;
    private boolean isHostReady = false;
    public static final String FILENAME = "wandering.mp3";

    public static void main(String args[]) throws Exception {
        Retro retro = new Retro();
        retro.post();
    }

    public void post() throws InterruptedException {
        final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", Accept).header("Accept_Encoding", Accept_Encoding).header("Accept_Language", getAcceptLanguage())
                        .method(original.method(), original.body());
                if (account != null) {
                    requestBuilder.header("Authorization", "mCloud " + account.access_token);
                }
                if (!isHostReady && version != null && version.apihost != null && version.apihost.length() > 0) {
                    isHostReady = true;
                    System.out.println("动态获取到api host:" + version.apihost);
                    HttpUrl newUrl = original.url().newBuilder()
                            .host(version.getApiHost())
                            .build();
                    requestBuilder.url(newUrl);
                } else {
                    System.out.println(isHostReady ? "host已经设置完毕" : "未动态获取到host");
                }
                return chain.proceed(requestBuilder.build());
            }
        }).build();
        retrofit = new Retrofit.Builder().baseUrl("http://api.mcloudlife.com").addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory()).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        ApiVersion apiVersion = retrofit.create(ApiVersion.class);
        ErrorHandlingCallAdapter.CloudCall<Repo> call = apiVersion.obtainVersion();
        call.enqueue(new ErrorHandlingCallAdapter.AbstractCloudCallBack<Repo>() {
            @Override
            public void success(Response<Repo> response) {
                System.out.println("SUCCESS >> url:" + response.raw().request().url() + "  >>  " + response.body().toString());
                version = response.body();
            }
        });
        Thread.sleep(1000);
        Login login = retrofit.create(Login.class);
        ErrorHandlingCallAdapter.CloudCall<Account> loginBack = login.login("18768177280", "123123");
        loginBack.enqueue(new ErrorHandlingCallAdapter.AbstractCloudCallBack<Account>() {
            @Override
            public void success(Response<Account> response) {
                System.out.println("SUCCESS  >>  url:" + response.raw().request().url() + "  >>  " + response.body().toString());
                account = response.body();
                apply();
            }
        });
    }

    private void apply() {
        ApplyAttachment applyAttachment = retrofit.create(ApplyAttachment.class);
        if (account == null) {
            System.out.print("account null");
            return;
        }
        final File file = new File(FILENAME);
        applyAttachment.obtain(account.access_token, FILENAME, (int) file.length()).enqueue(new ErrorHandlingCallAdapter.AbstractCloudCallBack<Attachment>() {
            @Override
            public void success(Response<Attachment> response) {
                Attachment attachment = response.body();
                System.out.println(attachment.toString());
                UploadClient uploadClient = new UploadClient().setAccessToken(account.access_token).setFile(file).setUrl(attachment.url).setLen(attachment.len).setOffset(attachment.offset);
                uploadClient.start();

            }
        });
    }

    public interface ApiVersion {
        @POST("/api/version")
        ErrorHandlingCallAdapter.CloudCall<Repo> obtainVersion();
    }

    public class Repo {
        private String apiversion;
        private String apihost;
        private Message message;

        public String getApiHost() {
            return apihost == null ? null : apihost.replace("http://", "").replace("https://", "");
        }

        @Override
        public String toString() {
            return "apiversion:" + apiversion + " , " + "apihost:" + apihost + " , " + "message:";
        }

        public class Message {
            private String title;
            private String content;
        }
    }

    public interface Login {
        @POST("/api/login")
        @FormUrlEncoded
        ErrorHandlingCallAdapter.CloudCall<Account> login(@Field("target") String target, @Field("password") String password);
    }


    public interface ApplyAttachment {
        @POST("/api/attachment")
        @FormUrlEncoded
        ErrorHandlingCallAdapter.CloudCall<Attachment> obtain(@Field("access_token") String accessToken, @Field("filename") String fileName, @Field("size") int size);
    }


    public class Attachment {
        private String url;
        private String method;
        private int len;
        private String finished;
        private int offset;

        @Override
        public String toString() {
            return "url:" + url + " , method:" + method + " , len:" + len + " , offset" + offset + " , finish:" + finished;
        }
    }

    public class Account {
        private int syncid;
        private String access_token;
        private String activated;
        private String expires_in;

        @Override
        public String toString() {
            return "syncid:" + syncid + ", " + "access_token:" + access_token + " , activated:" + activated;
        }
    }

    private static final String Accept = "application/json";
    private static final String Accept_Encoding = "application/json";
    private static final String USER_AGENT = "mCloud/3.1.0.7D";
    private static final String ACCEPT_LANGUAGE = getAcceptLanguage();

    public static String getAcceptLanguage() {
        Locale lc = Locale.getDefault();
        return lc.getLanguage() + ";en;q=1, fr;q=0.9, de;q=0.8, zh-Hans;q=0.7, zh-Hant;q=0.6, ja;q=0.5";
    }
}
