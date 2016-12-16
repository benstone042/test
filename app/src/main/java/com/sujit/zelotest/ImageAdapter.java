package com.sujit.zelotest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by sujit yadav on 16-12-2016.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<AndroidVersion> android;
    private Context context;
    Downloader mDownloader;
    private File fileDir;
    String urlStr ;
    String file;
    String  fileExtension ;
    String  filename;
    SharedPreferences settings ;
    File pathfile;

    int progress ;

    public ImageAdapter(Context context,ArrayList<AndroidVersion> android) {
        this.android = android;
        this.context = context;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.tv_android.setText(android.get(i).getAndroid_version_name());

        viewHolder.img_android.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.loading_layout.setVisibility(View.VISIBLE);
                viewHolder.img_android.setVisibility(View.GONE);
            }
        });

        viewHolder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder.asyncTask= new AsyncTask<URL, Message, Downloader>() {




                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        urlStr = android.get(i).getAndroid_image_url();
                        file = urlStr.substring(urlStr.lastIndexOf("/") + 1);
                        fileExtension = file.substring(file.lastIndexOf("."));
                        filename = file.substring(0, file.lastIndexOf("."));
                        settings = context.getSharedPreferences("MyResumableDownloadPrefsFile", 0);

                        progress = settings.getInt("Progress", 0);

                        viewHolder.progress_bar.setProgress(progress);
                        viewHolder.progress_percentage.setText(String.format("%1$" + 3 + "s", progress) + "%");
                        mDownloader = new Downloader(settings.getString("LastModified", ""), mDownloader.PAUSE);
                        fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                "resumedownload");
                    }

                    protected Downloader doInBackground(URL... params) {

                        try {

                            if (!fileDir.exists()) { fileDir.mkdirs();}
                            pathfile=new File(fileDir.getAbsolutePath(), filename + fileExtension);
                            mDownloader.downloadFile(urlStr,
                                    pathfile.getAbsolutePath(),
                                    new Downloader.DownloadListener() {
                                        public void progressUpdate(Message value) {
                                            publishProgress(value);
                                        }
                                    });
                        } catch (FileNotFoundException e) {
                            // set status holder textview message
                        } catch (IOException e) {
                            e.printStackTrace();
                            mDownloader.setStatus(mDownloader.ERROR);
                            // set status holder textview message
//                            message(e.getMessage().substring(0, 50));
                        }
                        return mDownloader;
                    }

                    protected void onProgressUpdate(Message... values) {
                        super.onProgressUpdate(values);
                        int progress = (values[0]).getProgress();
                        if (progress != 0) {
                            viewHolder.progress_percentage.setText(String.format("%1$" + 3 + "s", progress) + "%");
                            viewHolder.progress_bar.setProgress(values[0].getProgress());
                        }
                        if (progress == 100) {
                            viewHolder.message.setText("status: " + mDownloader.getStatusStr());
                            viewHolder.loading_layout.setVisibility(View.GONE);
                            viewHolder.img_android.setVisibility(View.VISIBLE);
                            viewHolder.img_android.setImageBitmap(BitmapFactory.decodeFile(pathfile.getAbsolutePath()));
                            viewHolder.img_android.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent= new Intent(context,ImageFullScreenActivity.class);
                                    intent.putExtra("uri",pathfile.getAbsolutePath());
                                    context.startActivity(intent);
                                }
                            });
                            // downloader layout gone
                            // view layout visible
                        }
                        String msg = values[0].getMessage();
                        if (msg != "") {
//                            message(msg);
                            viewHolder.message.setText(msg);
                        }
                    }

                    protected void onPostExecute(Downloader o) {
                        super.onPostExecute(o);

                    }

                    protected void onCancelled() {
                        super.onCancelled();

                    }
                }.execute();
            }
        });
        viewHolder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDownloader != null) {
                    SharedPreferences settings = context.getSharedPreferences("MyResumableDownloadPrefsFile", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("LastModified", mDownloader.getLastModified());
                    editor.putInt("Progress", viewHolder.progress_bar.getProgress());
                    editor.commit();
                    if (mDownloader.getStatus() == mDownloader.DOWNLOADING) {
                        mDownloader.setStatus(mDownloader.PAUSE);
                        viewHolder.asyncTask.cancel(true);
                        viewHolder.message.setText("paused & asyncTask is cancelled - " + viewHolder.asyncTask.isCancelled());
                    }
                    viewHolder.message.setText("status: " + mDownloader.getStatusStr());

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return android.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_android,progress_percentage,message;
        private ImageView img_android;
        private ProgressBar progress_bar;
        Button start,pause;
        private AsyncTask asyncTask;
        private LinearLayout loading_layout;


        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView) view.findViewById(R.id.tv_android);
            img_android = (ImageView) view.findViewById(R.id.img_android);
            progress_percentage = (TextView) view.findViewById(R.id.progress_percentage);
            message = (TextView) view.findViewById(R.id.message);
            progress_bar=(ProgressBar)view.findViewById(R.id.progress_bar);
            start=(Button)view.findViewById(R.id.start);
            pause=(Button)view.findViewById(R.id.pause);
            loading_layout=(LinearLayout) view.findViewById(R.id.loading_layout);


        }
    }



    private void startDownload() {


    }
}
