package connect.app.com.connect.retrofit;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by 44260 on 2016/3/17.
 */
public class JsonConverterFactory extends Converter.Factory {

    public JSONObject jsonObject;

    public JsonConverterFactory(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public static JsonConverterFactory create(JSONObject jsonObject) {
        return new JsonConverterFactory(jsonObject);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.responseBodyConverter(type, annotations, retrofit);
    }
}
