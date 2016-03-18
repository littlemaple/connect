package connect.app.com.connect;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import connect.app.com.lib.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        TextView textView = (TextView) findViewById(R.id.tv_config);
//        textView.setText(Constant.isDevelopMode ? "开发模式" : "不是开发模式");
//        textView.setText("\n");
//        textView.setText(Constant.isTestMode ? "测试模式" : "不是测试模式");
//        textView.append("\n");
//        textView.append(Constant.name);
//        textView.append("\n");
//        textView.append(obtainMeta());
    }


    private String obtainMeta() {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo == null ? "applicationInfo null" : applicationInfo.metaData == null ?
                "meta data null" : applicationInfo.metaData.getString("JPUSH_APPKEY");
    }

    public void post(View view) {
        showPopWindow(view);
        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Create an instance of our GitHub API interface.
        GitHub github = retrofit.create(GitHub.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<Contributor>> call = github.contributors("square", "retrofit");

        // Fetch and print a list of the contributors to the library.
//            contributors = call.execute().body();
        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                System.out.print("");
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                System.out.print(t.toString());
            }
        });

    }

    public static final String API_URL = "http://api.mcloudlife.com";
    public static final String API_URL_GIT = "https://api.github.com";

    public void show(View view) {
        PopupUtil.show(this, view, null);
    }


    public static class Contributor {
        public final String login;
        public final int contributions;

        public Contributor(String login, int contributions) {
            this.login = login;
            this.contributions = contributions;
        }
    }

    public interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        Call<List<Contributor>> contributors(
                @Path("owner") String owner,
                @Path("repo") String repo);
    }

    private PopupWindow popWindow;
    private StatusAdapter adapter;

    private void showPopWindow(View view) {
        initMenuDialog(view);
        adapter.notifyDataSetChanged();
        if (popWindow.isShowing())
            popWindow.dismiss();
        else {
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            popWindow.getContentView().measure(0, heightSpec);
            popWindow.showAsDropDown(view, 0, -popWindow.getContentView().getMeasuredHeight());
        }
    }

    public void initMenuDialog(View view) {
        if (popWindow != null) {
            return;
        }
        RecyclerView recyclerView = new RecyclerView(getBaseContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        if (adapter == null)
            adapter = new StatusAdapter();
        recyclerView.setAdapter(adapter);
        popWindow = new PopupWindow(recyclerView);
        popWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.message_ic_consultframe));
        popWindow.setOutsideTouchable(true);
        popWindow.update();
    }

    public class StatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(50, 40, 50, 40);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            return new RecyclerView.ViewHolder(tv) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((TextView) holder.itemView).setText(position == 0 ? "--" : "qwertyqwertyqwe");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWindow.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 9;
        }
    }
}
