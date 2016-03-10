package com.softwinner.TvdVideo.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;

/**
 * 作者：lixiang on 2015/12/16 10:55
 * 邮箱：xiang.li@spreadwin.com
 */
@SuppressLint("NewApi")
public class VideoUtils {
    /**
     * 获取视频第一帧图片
     *
     * @param filePath
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        try {
            media.setDataSource(filePath);
            bitmap = media.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
        } finally {
            try {
                media.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    private static  Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            //重置baos即清空baos
            baos.reset();
            //这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            //每次都减少10
            options -= 10;
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap mBitmap = BitmapFactory.decodeStream(isBm, null, null);
        return mBitmap;
    }

    public static Bitmap getBitmap(String myJpgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
        return bm;
    }

 // 将毫秒转化为时间
    public static String getTime(int time) {
        Date date = new Date();// 获取当前时间
        SimpleDateFormat hms = new SimpleDateFormat("mm:ss");
        date.setTime(-8 * 60 * 60 * 1000 + time);
        String data = hms.format(date);
        return data;
    }
    
}
