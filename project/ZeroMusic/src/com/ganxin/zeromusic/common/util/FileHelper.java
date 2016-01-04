
package com.ganxin.zeromusic.common.util;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 
 * @Description 文件相关操作帮助类
 * @author ganxin
 * @date Apr 4, 2015
 * @email ganxinvip@163.com
 */
public class FileHelper {

    /**
     * 创建文件的模式，已经存在的文件要覆盖
     */
    public final static int MODE_COVER = 1;

    /**
     * 创建文件的模式，文件已经存在则不做其它事
     */
    public final static int MODE_UNCOVER = 0;

    /**
     * 获取文件的输入流
     * 
     * @param path
     * @return
     */
    public static FileInputStream getFileInputStream(String path) {
        FileInputStream fis = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                fis = new FileInputStream(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fis;
    }

    /**
     * 获取文件的输出流
     * 
     * @param path
     * @return
     */
    public static OutputStream getFileOutputStream(String path) {
        FileOutputStream fos = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                fos = new FileOutputStream(file);
            }
        } catch (Exception e) {
            return null;
        }
        return fos;
    }

    /**
     * 获取文件的数据
     * 
     * @param path
     * @return
     */
    public static byte[] getFileData(String path) {
        byte[] data = null;// 返回的数据
        try {
            File file = new File(path);
            if (file.exists()) {
                data = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(data);
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 文件大小
     * 
     * @param path
     * @return
     */
    public static int getFileSize(String path) {
        return (int) new File(path).length();
    }

    public static long getFileSizes(File f) {
        long size = 0;
        try {
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSizes(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除文件或文件夹(包括目录下的文件)
     * 
     * @param path
     */
    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File f = new File(filePath);
            if (f.exists() && f.isDirectory()) {
                File[] delFiles = f.listFiles();
                if (delFiles != null) {
                    for (int i = 0; i < delFiles.length; i++) {
                        deleteFile(delFiles[i].getAbsolutePath());
                    }
                }
            }
            f.delete();
        } catch (Exception e) {

        }
    }

    /**
     * 删除文件
     * 
     * @param filePath
     * @param deleteParent 是否删除父目录
     */
    public static void deleteFile(String filePath, boolean deleteParent) {
        if (filePath == null) {
            return;
        }
        try {
            File f = new File(filePath);
            if (f.exists() && f.isDirectory()) {
                File[] delFiles = f.listFiles();
                if (delFiles != null) {
                    for (int i = 0; i < delFiles.length; i++) {
                        deleteFile(delFiles[i].getAbsolutePath(), deleteParent);
                    }
                }
            }
            if (deleteParent) {
                f.delete();
            } else if (f.isFile()) {
                f.delete();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 创建一个空的文件(创建文件的模式，已经存在的是否要覆盖)
     * 
     * @param path
     * @param mode
     */
    public static boolean createFile(String path, int mode) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            File file = new File(path);
            if (file.exists()) {
                if (mode == FileHelper.MODE_COVER) {
                    file.delete();
                    file.createNewFile();
                }
            } else {
                // 如果路径不存在，先创建路径
                File mFile = file.getParentFile();
                if (!mFile.exists()) {
                    mFile.mkdirs();
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 创建一个空的文件夹(创建文件夹的模式，已经存在的是否要覆盖)
     * 
     * @param path
     * @param mode
     */
    public static void createFolder(String path, int mode) {
        try {
            File file = new File(path);
            if (file.exists()) {
                if (mode == FileHelper.MODE_COVER) {
                    FileHelper.deleteFile(path);
                    file.mkdirs();
                }
            } else {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     * 
     * @param path
     * @return
     */
    public static long getSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        long size = 0;
        try {
            File file = new File(path);
            if (file.exists()) {
                size = file.length();
            }
        } catch (Exception e) {
            return 0;
        }
        return size;
    }

    /**
     * 判断文件或文件夹是否存在
     * 
     * @param path
     * @return true 文件存在
     */
    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean exist = false;
        try {
            File file = new File(path);
            exist = file.exists();
        } catch (Exception e) {
            return false;
        }
        return exist;
    }

    /**
     * 移动文件
     * 
     * @param oldFilePath 旧路径
     * @param newFilePath 新路径
     * @return
     */
    public static boolean moveFile(String oldFilePath, String newFilePath) {
        if (TextUtils.isEmpty(oldFilePath) || TextUtils.isEmpty(newFilePath)) {
            return false;
        }
        File oldFile = new File(oldFilePath);
        if (oldFile.isDirectory() || !oldFile.exists()) {
            return false;
        }
        try {
            File newFile = new File(newFilePath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(oldFile));
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int read;
            while ((read = bis.read(buf)) != -1) {
                fos.write(buf, 0, read);
            }
            fos.flush();
            fos.close();
            bis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
