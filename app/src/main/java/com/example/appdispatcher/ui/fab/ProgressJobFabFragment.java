package com.example.appdispatcher.ui.fab;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appdispatcher.R;
import com.example.appdispatcher.VolleyMultipartRequest;
import com.example.appdispatcher.util.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProgressJobFabFragment extends Fragment {

    public static final int PICKFILE_RESULT_CODE = 1;
    public static final int REQUEST_PERMISSIONS = 100;
    public static final String ID_JOB = "id_job";
    public static final String GET_ID_JOB = "get_id_job";
    Button btn_upload;
    ImageView imgIdProf;
    TextView tvIdJob, textViewSelected;
    EditText etProgress;
    String progress_spinner, progress, id_job;
    ScrollView scrollView;
    private Bitmap bitmap;
    private String filePath, fileName;
    private ProgressDialog pd;
    Spinner spinner_progress;
    CharSequence text;
    String currentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress_job_fab, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        String id_jobb = extras.getString("id_job");

        tvIdJob = view.findViewById(R.id.tv_id_job);
        imgIdProf = view.findViewById(R.id.IdProf);
        etProgress = view.findViewById(R.id.eTextTask3);
        textViewSelected = view.findViewById(R.id.textviewSelected);
        scrollView = view.findViewById(R.id.scrollviewdone);
        btn_upload = view.findViewById(R.id.btn_upload);
        spinner_progress = view.findViewById(R.id.spinner_progress);
        pd = new ProgressDialog(getActivity(), R.style.MyTheme);

        fillDetail(id_jobb);

        imgIdProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                    if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
//
//                    } else {
//                        ActivityCompat.requestPermissions(getActivity(),
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                                REQUEST_PERMISSIONS);
//                    }
//                } else {
//                    Log.e("Else", "Else");
//                    showFileChooser();
//                }
                selectImage(getActivity());

            }

            private void showFileChooser() {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.progress_job,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_progress.setAdapter(adapter);

        spinner_progress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String selected = parentView.getItemAtPosition(position).toString();
                Context context = parentView.getContext();
                text = selected;
                int duration = Toast.LENGTH_SHORT;

//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                TextView errorText = (TextView)spinner_progress.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("Please select progress job");
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = spinner_progress.getSelectedItem().toString().trim() + " - " + etProgress.getText().toString().trim();
                id_job = tvIdJob.getText().toString().trim();
                if (spinner_progress.getSelectedItem().toString().equals("Please select progress job")) {
                    TextView errorText = (TextView)spinner_progress.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Please select progress job");
                } else if (etProgress.getText().toString().trim().length() == 0) {
                    etProgress.setError("Progress Job Should not be empty !");
                } else if (filePath != null) {
                    File file = new File(filePath);
                    if (file.length() > 10000000) {
                        Toast.makeText(getActivity(), "Your image more than 10mb, please upload less than 10mb!", Toast.LENGTH_SHORT).show();
                    } else {
                        submit(bitmap);
                    }
                } else {
                    submit(bitmap);
                }
            }

        });

        Log.d("cek spinner",spinner_progress.getSelectedItem().toString());

        return view;
    }

    private void selectImage(FragmentActivity activity) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
//                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, 0);
                    dispatchTakePictureIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE))) {

                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS);
                        }
                    } else {
                        Log.e("Else", "Else");
                        showFileChooser();
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }


            }

            private void showFileChooser() {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("image/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getActivity().getFilesDir();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.example.appdispatcher.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            Log.i("TAG", "dispatchTakePictureIntent: " + photoURI);
            Log.i("TAG", "dispatchTakePictureIntent2: " + currentPhotoPath);
            startActivityForResult(takePictureIntent, 0);
        }
//        }
    }

    private void fillDetail(String id_job) {
        JsonObjectRequest StrReq = new JsonObjectRequest(Request.Method.GET, server.progreesjob_withToken + "?id_job=" + id_job, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject job = response.getJSONObject("job");

                    tvIdJob.setText(job.getString("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "applicaion/json");
                // Barer di bawah ini akan di simpan local masing-masing device engineer

//                headers.put("Authorization", "Bearer 14a1105cf64a44f47dd6d53f6b3beb79b65c1e929a6ee94a5c7ad30528d02c3e");
                SharedPreferences mSetting = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
                headers.put("Authorization", mSetting.getString("Token", "missing"));
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(StrReq);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            File file = new File(filePath);
            fileName = file.getName();
            Log.i("file length", String.valueOf(Integer.parseInt(String.valueOf(file.length()))));
            if (filePath != null) {
                try {
                    textViewSelected.setText("File Selected");
                    Log.i("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    imgIdProf.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(
                        getActivity(), "no image selected",
                        Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == 0){
            textViewSelected.setText("File Selected");
            Bitmap selectedImage = BitmapFactory.decodeFile(currentPhotoPath);
            imgIdProf.setImageBitmap(selectedImage);

            Bitmap bitmap = selectedImage;

            if (android.os.Build.VERSION.SDK_INT >= 29){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name));
                values.put(MediaStore.Images.Media.IS_PENDING, true);
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try {
                        saveImageToStream(bitmap, getContext().getContentResolver().openOutputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContext().getContentResolver().update(uri, values, null, null);
                }
            } else {
                File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name));

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = System.currentTimeMillis() + ".png";
                File file = new File(directory, fileName);
                try {
                    saveImageToStream(bitmap, new FileOutputStream(file));
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();
//    }

    private String getPath(Uri picUri) {
        Cursor cursor = getActivity().getContentResolver().query(picUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void submit(Bitmap bitmap) {
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();

        scrollView.setVisibility(View.GONE);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap = ((BitmapDrawable) imgIdProf.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] image = byteArrayOutputStream.toByteArray();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, server.postJobUpdate_withToken, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.i("response", response.toString());
                pd.dismiss();
                Intent intent = getActivity().getIntent();
                intent.putExtra(ID_JOB, id_job);
                intent.putExtra(GET_ID_JOB, "id_job_progress");
//                Log.i("cek size image", String.valueOf(Integer.parseInt(String.valueOf(file.length()))));
                getActivity().setResult(1, intent);
                getActivity().finish();
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }
                        Toast.makeText(getActivity(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /**
             * Passing some request headers
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_job", id_job);
                params.put("detail_activity", progress);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("documentation_progress", new DataPart(imagename + ".jpeg", image));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "applicaion/json");
                SharedPreferences mSetting = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
                headers.put("Authorization", mSetting.getString("Token", "missing"));
                return headers;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(volleyMultipartRequest);
    }

    @Override
    public void onDestroy() {
        if (pd != null && pd.isShowing()){
            pd.dismiss();
        }
        super.onDestroy();
    }
}