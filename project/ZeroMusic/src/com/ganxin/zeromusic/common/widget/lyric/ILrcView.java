package com.ganxin.zeromusic.common.widget.lyric;

import java.util.List;


public interface ILrcView {
    
    /**
     *  设置显示歌词
     */
    void setLrc(List<LrcRow> lrcRows);
    
    /**
     * 设置歌词的行数
     */
    void seekLrc(int position);
    
    /**
     * 设置高亮度显示
     * 
     */
    void seekLrcToTime(long time);
    
    void setListener(LrcViewListener l);
    
    public static interface LrcViewListener {
        
        /**
         * when lyric line was seeked by user
         */
        void onLrcSeeked(int newPosition, LrcRow row);
    }
}
