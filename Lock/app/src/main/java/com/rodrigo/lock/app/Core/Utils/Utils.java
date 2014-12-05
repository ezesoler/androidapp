package com.rodrigo.lock.app.Core.Utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Rodrigo
 */
public class Utils {

    public static String getExtensionFile(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension.toLowerCase();
    }

    public static String removeExtensionFile(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(0,i );
        } else {
            return fileName;
        }

    }



    public static String getEncExtension() {
        return  ("pbx");
    }



    public static void delete(File file) throws IOException{

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){

                file.delete();
                //  System.out.println("Directory is deleted : "  + file.getAbsolutePath());

            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //checkAndInit the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    //  System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }

        }else{
            //if file, then delete it
            file.delete();
            //   System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }


    private static byte[] PANDORABOX = hexStringToByteArray("50414e444f5241424f58");


    public  static byte[] getPANDORABOX(){
        return PANDORABOX;
    }


    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a)
    {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }





    public static String  getPathFileNoExists(String path,String name, String extension){
        int i=0;

        if(!TextUtils.isEmpty(extension)){
            extension= "."+extension;
        }

        String newF = path + name + extension;

        while (new File(newF).exists()){
            i++;
            newF=path + name + "("+ i + ")" + extension;
        }

        return newF;
    }


    /**
     * Return pseudo unique ID
     * @return ID
     */
    public static String getUniquePsuedoID()
    {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a duplicate entry
        String serial = null;
        try
        {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
        catch (Exception e)
        {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    public static Intent shareExludingApp (Context c, Uri uri)  {
        String packageNameToExclude =c.getPackageName();
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = createShareIntent(uri);
        List<ResolveInfo> resInfo = c.getPackageManager().queryIntentActivities(createShareIntent(uri),0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = createShareIntent(uri);

                if (!info.activityInfo.packageName.equalsIgnoreCase(packageNameToExclude)) {
                    targetedShare.setPackage(info.activityInfo.packageName);
                    targetedShareIntents.add(targetedShare);
                }
            }

            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0),
                    "Select app to share");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    targetedShareIntents.toArray(new Parcelable[] {}));
            return chooserIntent;
        }
        return null;
    }

    private   static   Intent createShareIntent (Uri uri)  {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/zip");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        return share ;
    }




}
