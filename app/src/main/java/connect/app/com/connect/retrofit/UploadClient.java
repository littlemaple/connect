package connect.app.com.connect.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by 44260 on 2016/3/18.
 */
public class UploadClient {

    private static final String Accept = "application/json";
    private static final String Accept_Encoding = "application/json";
    private static final String USER_AGENT = "mCloud/3.1.0.7D";

    private UploadEntity uploadEntity = new UploadEntity();
    private String accessToken;
    private OkHttpClient client = new OkHttpClient();

    public UploadClient setFile(File file) {
        this.uploadEntity.setOriginFile(file);
        return this;
    }

    public UploadClient setLen(int len) {
        this.uploadEntity.setLen(len);
        return this;
    }

    public UploadClient setOffset(int offset) {
        this.uploadEntity.setOffset(offset);
        return this;
    }

    public UploadClient setUrl(String url) {
        this.uploadEntity.setUrl(url);
        return this;
    }

    public UploadClient setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public void start() {
        requestUpload();
    }


    private void requestUpload() {
        RequestBody requestBody = new RequestBody() {

            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(uploadEntity.getBytes());
            }
        };
        Request request = new Request.Builder()
                .url(uploadEntity.url).put(requestBody).header("User-Agent", USER_AGENT).header("Authorization", "mCloud " + accessToken)
                .header("Accept", Accept).header("Accept_Encoding", Accept_Encoding)
                .build();

        System.out.println(request.toString());
        System.out.println(request.headers().toString());
        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(Attachment.class));
            JsonReader jsonReader = new JsonReader(response.body().charStream());
            jsonReader.setLenient(true);
            Attachment attachment = (Attachment) adapter.read(jsonReader);
            System.out.println("解析返回内容" + attachment);
            if (attachment.isFinish()) {
                System.out.println("文件上传成功");
                return;
            }
            if (attachment.errcode != 0) {
                System.out.println("文件上传失败:" + attachment.errmsg);
                return;
            }
            uploadEntity.setLen(attachment.len);
            uploadEntity.setOffset(attachment.off);
            uploadEntity.setUrl(attachment.url);
            requestUpload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Attachment {
        private String url;
        private String method;
        private int len;
        private String finished;
        private int off;
        private int errcode;
        private String errmsg;

        @Override
        public String toString() {
            if (errcode == 0)
                return "url:" + url + " , method:" + method + " , len:" + len + " , off:" + off + " , finish:" + finished;
            return "errorcode:" + errcode + ",errormsg:" + errmsg;
        }

        public boolean isFinish() {
            return finished != null && finished.toLowerCase().equals("y");
        }
    }

    public class UploadEntity {
        private File originFile;
        private static final int MAX_BUFFER_SIZE = 1024;
        /**
         * 服务器要求传的大小
         */
        private int len;
        private int offset;
        private String url;

        public UploadEntity setUrl(String url) {
            this.url = url;
            return this;
        }

        public UploadEntity setOriginFile(File originFile) {
            this.originFile = originFile;
            return this;
        }

        public UploadEntity setLen(int len) {
            this.len = len;
            return this;
        }

        public UploadEntity setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public byte[] getBytes() throws IOException {
            final File file = this.originFile;
            final int MAX_BUFFER_SIZE = 1024;
            byte[] buffer;
            int readLen = 0;
            ByteArrayOutputStream outputStream = null;
            RandomAccessFile mRandomAccessFile = null;
            try {
                mRandomAccessFile = new RandomAccessFile(file, "r");
                mRandomAccessFile.seek(offset);
                outputStream = new ByteArrayOutputStream();
                while (readLen < len) {
                    int byteCount;
                    if (len - readLen > MAX_BUFFER_SIZE) {
                        byteCount = MAX_BUFFER_SIZE;
                    } else {
                        byteCount = len - readLen;
                    }
                    buffer = new byte[byteCount];
                    int length = mRandomAccessFile.read(buffer, 0, byteCount);
                    outputStream.write(buffer);
                    readLen += length;
                }
            } finally {
                if (mRandomAccessFile != null) {
                    mRandomAccessFile.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            byte[] bytes = outputStream.toByteArray();
            System.out.println("服务器所需:" + toString() + " ,待上传:" + bytes.length + ",readLen:" + readLen);
            return bytes;
        }

        @Override
        public String toString() {
            return "len:" + len + " , offset:" + offset + " , total:" + originFile.length() + " ,url:" + url;
        }
    }


}
