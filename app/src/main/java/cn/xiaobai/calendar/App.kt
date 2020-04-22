package cn.xiaobai.calendar

import android.app.Application
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        UMConfigure.init(this, "5e80272c0cafb259dd000196", "channel", UMConfigure.DEVICE_TYPE_PHONE, null)
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        // 打开统计SDK调试模式
        UMConfigure.setLogEnabled(true)
    }
}