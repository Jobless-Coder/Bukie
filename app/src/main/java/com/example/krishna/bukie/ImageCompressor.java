package com.example.krishna.bukie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

import id.zelory.compressor.Compressor;

public class ImageCompressor {
    private ImageCompressor()
    {

    }

    public static File compressFromFile(Context context, File original)
    {
        try {
            original = new Compressor(context).compressToFile(original);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Image cannot be compressed!", Toast.LENGTH_SHORT).show();
        }
        return original;
    }

    public static Uri compressFromUri(Context context, Uri original)
    {
        File f = new File("demo");
        try {
            f = FileUtil.from(context, original);
            f = new Compressor(context).compressToFile(f);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Image cannot be compressed!", Toast.LENGTH_SHORT).show();
        }
        return Uri.fromFile(f);
    }
}
