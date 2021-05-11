package com.jizhi.geektime.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.*;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * 文件回调存储
 *
 * @author jizhi7
 * @since 1.0
 **/
public class FileFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    // 默认存储的文件位置
    private static final File CACHE_FALLBACK_DIRECTORY = new File(".cache/fallback/");

    private final Logger logger = Logger.getLogger(getClass().getName());

    public FileFallbackStorage() {
        super(Integer.MAX_VALUE);
        // 创建文件夹
        makeCacheFallbackDirectory();
    }

    private void makeCacheFallbackDirectory() {
        // 如果文件夹不存在，且创建不成功
        if (!CACHE_FALLBACK_DIRECTORY.exists() && !CACHE_FALLBACK_DIRECTORY.mkdirs()) {
            throw new RuntimeException(format("The fallback directory[path:%s] can't be created!"));
        }
    }

    /**
     * 加载缓存对象
     *
     * @param key 缓存key
     * @return
     * @throws CacheLoaderException
     */
    @Override
    public Object load(Object key) throws CacheLoaderException {
        // 获取 缓存 key 对应的文件
        File storageFile = toStorageFile(key);

        // 如果文件不存在 或 不能读取
        if (!storageFile.exists() || !storageFile.canRead()) {
            logger.warning(format("The storage file[path:%s] does not exist or can't be read, " +
                    "thus the value can't be loaded.", storageFile.getAbsolutePath()));
            return null;
        }

        // 从文件中反序列化出对象
        Object value = null;
        try (FileInputStream fileInputStream = new FileInputStream(storageFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
            value = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 将缓存的key转换为对应的一个文件
     *
     * @param key
     * @return
     */
    File toStorageFile(Object key) {
        return new File(CACHE_FALLBACK_DIRECTORY, key.toString() + ".dat");
    }

    /**
     * 写缓存数据
     *
     * @param entry
     * @throws CacheWriterException
     */
    @Override
    public void write(Cache.Entry<?, ?> entry) throws CacheWriterException {
        Object key = entry.getKey();
        Object value = entry.getValue();

        // key 就是文件名
        File storageFile = toStorageFile(key);

        // 判断文件是否存在，是否可写
        if (storageFile.exists() && !storageFile.canWrite()) {
            logger.warning(format("The storage file[path:%s] can't be written, " +
                    "thus the entry will not be stored.", storageFile.getAbsolutePath()));
            return;
        }

        // 将 value 序列化，写入文件中
        try (FileOutputStream fileOutputStream = new FileOutputStream(storageFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除缓存对象
     *
     * @param key
     * @throws CacheWriterException
     */
    @Override
    public void delete(Object key) throws CacheWriterException {
        File storageFile = toStorageFile(key);
        storageFile.delete();
    }

    /**
     * 销毁的时候。要将整个缓存文件夹删除掉
     */
    @Override
    public void destroy() {
        destroyCacheFallbackDirectory();
    }

    /**
     * 删除文件夹下的所有文件
     */
    private void destroyCacheFallbackDirectory() {
        if (CACHE_FALLBACK_DIRECTORY.exists()) {
            // 遍历文件删除
            for (File storageFile : CACHE_FALLBACK_DIRECTORY.listFiles()) {
                storageFile.delete();
            }
        }
    }

}
