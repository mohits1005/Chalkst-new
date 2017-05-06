package com.example.mohits1005.chalkst;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.R.attr.color;
import static android.R.attr.width;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private Button mButtonAdd;
    private ImageButton middleButton;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Random mRandom = new Random();
    private Button previewButton;
    private Button returnButton;
    List<list_element> myList;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int pos;
    int image_insert_flag = 0;
    private Bitmap bitmap;
    Button addBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
        mButtonAdd = (Button) findViewById(R.id.btn_add);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        myList = new ArrayList<>();
        middleButton = (ImageButton) findViewById(R.id.ib_start);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        // Initialize a new instance of RecyclerView Adapter instance
        mAdapter = new NewAdapter(getApplication(),myList);
        mAdapter.setHasStableIds(false);
        // Set the adapter for RecyclerView
        mRecyclerView.setAdapter(mAdapter);
        // Set a click listener for add item button
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_insert_flag = 0;
                CardView cv = (CardView) findViewById(R.id.card_view_start);
                cv.setVisibility(view.GONE);
                myList.add(myList.size(),new list_element());
                mAdapter.notifyItemInserted(myList.size()-1);
                mRecyclerView.scrollToPosition(myList.size()-1);
            }
        });
        // Set listener for center add button
        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_insert_flag = 0;
                CardView cv = (CardView) findViewById(R.id.card_view_start);
                cv.setVisibility(view.GONE);
                myList.add(myList.size(),new list_element());
                mAdapter.notifyItemInserted(myList.size()-1);
                mRecyclerView.scrollToPosition(myList.size()-1);
            }
        });
        StoragePermissions(this);
    }

    public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {
        private List<list_element> mDataSet;
        private Context mContext;

        public NewAdapter(Context context, List<list_element> list) {
            mDataSet = list;
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public Button mRemoveButton;
            public RelativeLayout mRelativeLayout;
            public ImageButton mgallery;

            public ViewHolder(View v) {
                super(v);
                mImageView = (ImageView) v.findViewById(R.id.iv);
                mRelativeLayout = (RelativeLayout) v.findViewById(R.id.rl);
                mgallery = (ImageButton) v.findViewById(R.id.ib_gallery);
            }
        }

        @Override
        public NewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Create a new View
            View v = LayoutInflater.from(mContext).inflate(R.layout.custom_view, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (mDataSet.get(holder.getAdapterPosition()).getImage() != null) {
                holder.mImageView.setVisibility(View.VISIBLE);
                holder.mgallery.setVisibility(View.INVISIBLE);
                //holder.mRemoveButton.setVisibility(View.INVISIBLE);
                holder.mImageView.setImageBitmap(mDataSet.get(holder.getAdapterPosition()).getImage());

            } else {
                holder.mgallery.setVisibility(View.VISIBLE);
                holder.mImageView.setVisibility(View.INVISIBLE);
                //Gallery image upload
                holder.mgallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        pos = holder.getAdapterPosition();
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }
                });
                Log.d("reached", pos + "");
            }
        }
        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("new", pos + "");
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                image_insert_flag = 1;
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                list_element object = myList.get(pos);
                object.setImage(bitmap);
                mAdapter.notifyDataSetChanged();
                ImageButton gallery = (ImageButton) findViewById(R.id.ib_gallery);
                gallery.setVisibility(View.GONE);
            } catch (IOException e) {
                image_insert_flag = 0;
                e.printStackTrace();
            }
        }
    }
    public void StoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

    }
}

