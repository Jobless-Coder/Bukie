package com.example.krishna.bukie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class ImageCompressorTestActivity extends AppCompatActivity {
public int SELECT_PHOTO = 123;
public Uri imageUri;
public String code;
File f, cmp, cmpwebp;
Date original, compressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_compressor_test);
    }

    public void pickImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK
                && null != data) {

            imageUri = data.getData();
            code="data couldn't be retrieved";
            //String destinationFileName = "temp_profile_picture.png";
            ((ImageView)findViewById(R.id.originalimage)).setImageURI(data.getData());
            try
            {
                f = FileUtil.from(this, imageUri);
                code= "Original file: "+getReadableFileSize(f.length());
                Date dc = new Date();
                cmp = ImageCompressor.compressFromFile(this, f);
                code += " Compresion time: "+((new Date().getTime() - dc.getTime())/1000.0)+"s\n";
                ((ImageView)findViewById(R.id.compressedimage)).setImageBitmap(BitmapFactory.decodeFile(cmp.getAbsolutePath()));
                Toast.makeText(this, "image bitmap added", Toast.LENGTH_SHORT).show();
                code += " compressed file:(JPEG) "+getReadableFileSize(cmp.length());

                /*
                dc = new Date();
                cmp = new Compressor(this).setCompressFormat(Bitmap.CompressFormat.JPEG).compressToFile(f);
                code += "\ncompressed file:(JPEG) "+ getReadableFileSize(cmp.length());
                code += " Compresion time: "+((new Date().getTime() - dc.getTime())/1000.0)+"s\n";
                */

                dc = new Date();
                cmpwebp = new Compressor(this).setCompressFormat(Bitmap.CompressFormat.WEBP).compressToFile(f);
                ((ImageView)findViewById(R.id.compressedimagewebp)).setImageBitmap(BitmapFactory.decodeFile(cmpwebp.getAbsolutePath()));
                code += "\ncompressed file:(WEBP) "+ getReadableFileSize(cmpwebp.length());
                code += " Compresion time: "+((new Date().getTime() - dc.getTime())/1000.0)+"s\n";
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Error everywhere!", Toast.LENGTH_SHORT).show();
            }
            ((TextView)findViewById(R.id.imagedetails)).setText(code);
            uploadOriginal();
        }
        else
        {
            Toast.makeText(this, "Bam..", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadOriginal() {
        StorageReference st = FirebaseStorage.getInstance().getReference().child("demo/original.png");
        original = new Date();
        st.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                code += " original upload time: "+ ((new Date()).getTime() - original.getTime())/1000.0;
                ((TextView)findViewById(R.id.imagedetails)).setText(code);
                uploadCompressed();
            }
        });
    }

    private void uploadCompressed() {
        StorageReference st = FirebaseStorage.getInstance().getReference().child("demo/compressedjpeg.jpeg");
        compressed = new Date();
        st.putFile(Uri.fromFile(cmp)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                code += " compressed upload time: "+ ((new Date()).getTime() - compressed.getTime())/1000.0;
                ((TextView)findViewById(R.id.imagedetails)).setText(code);
                uploadCompressed2();
            }
        });
    }

    private void uploadCompressed2() {
        StorageReference st = FirebaseStorage.getInstance().getReference().child("demo/compressedwebp.webp");
        compressed = new Date();
        st.putFile(Uri.fromFile(cmpwebp)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                code += " compressed upload time: "+ ((new Date()).getTime() - compressed.getTime())/1000.0;
                ((TextView)findViewById(R.id.imagedetails)).setText(code);
            }
        });
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
