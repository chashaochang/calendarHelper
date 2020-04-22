package cn.xiaobai.calendar

import android.Manifest
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.ui.core.*
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.umeng.analytics.MobclickAgent
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var calendar: Calendar
    private lateinit var log: LogState
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this
        datePickerDialog = DatePickerDialog(this@MainActivity)
        calendar = Calendar.getInstance()
        log = LogState()
        setContent {
            MaterialTheme {
                DrawContent(log, onClick = {
                    requestPermission {
                        datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                            log.value = ""
                            calendar.set(year, month, dayOfMonth, 0, 0)
                            val result = getCalendarEvent(this@MainActivity, calendar = calendar)
                            for (i in 0 until result!!.length()) {
                                val item = result[i] as JSONObject
                                val startTime = item.get("startTime") as Long
                                val endTime = item.get("endTime") as Long
                                log.value = log.value +
                                        if (i == 0) {
                                            ""
                                        } else {
                                            "\n"
                                        } +
                                        timeStamp2Date(startTime) +
                                        "-" +
                                        timeStamp2Date(endTime) +
                                        "[" +
                                        if ((endTime - startTime) / 1000 / 60 / 60 > 0) {
                                            ((endTime - startTime) / 1000 / 60 / 60).toString() + "小时"
                                        } else {
                                            ""
                                        } +
                                        if ((endTime - startTime) / 1000 / 60 % 60 > 0) {
                                            ((endTime - startTime) / 1000 / 60 % 60).toString() + "分]："
                                        } else {
                                            "]"
                                        } +
                                        item.get("eventTitle")
                            }
                        }
                        datePickerDialog.show()
                    }
                }, onClickCopy = {
                    //获取剪贴板管理器
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", log.value)
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData)
                    if (log.value.isEmpty()) {
                        Toast.makeText(this@MainActivity, "没有日程可复制", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "复制成功", Toast.LENGTH_SHORT).show()
                    }
                }, onClickSetting = {
                    startActivity(Intent(context, SettingActivity::class.java))
                })
            }
        }
        val option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val decorView = window.decorView
        decorView.systemUiVisibility = option
    }

    private fun requestPermission(success: (() -> Unit)) {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CALENDAR
        )
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            success()
        } else {
            //request permission
            ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_CALENDAR), 666
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 666) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && permissions[0] == Manifest.permission.READ_CALENDAR
            ) {
                Toast.makeText(this@MainActivity, "授权成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "授权失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        if (datePickerDialog.isShowing) {
            datePickerDialog.dismiss()
        }
        super.onDestroy()
    }

    override fun onResume() {
        MobclickAgent.onResume(this)
        super.onResume()
    }

    override fun onPause() {
        MobclickAgent.onPause(this)
        super.onPause()
    }

}

@Composable
fun DrawContent(
        log: LogState,
        onClick: (() -> Unit)?,
        onClickCopy: (() -> Unit)?,
        onClickSetting: (() -> Unit)?
) {
    Surface(color = Color(0xfff4f4f4)) {
        Column(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
                children = {
                    Text(
                            modifier = Modifier.padding(
                                    start = 0.dp,
                                    top = 50.dp,
                                    end = 0.dp,
                                    bottom = 15.dp
                            ),
                            text = "日历助手",
                            style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                            )
                    )
                    Text(
                            modifier = Modifier.padding(
                                    start = 0.dp,
                                    top = 0.dp,
                                    end = 0.dp,
                                    bottom = 15.dp
                            ),
                            text = "选择日期导出当日的日程",
                            style = TextStyle(
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                            )
                    )
                    Surface(Modifier.clip(RoundedCornerShape(8.dp))) {
                        Button(modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp),
                                onClick = onClick!!,
                                backgroundColor = Color(54, 138, 244),
                                padding = InnerPadding(16.dp),
                                text = {
                                    Text(
                                            text = "选择日期",
                                            style = TextStyle(
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                    )
                                })
                    }
                    Spacer(Modifier.preferredHeight(15.dp))
                    Surface(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)) +
                                    Modifier.fillMaxWidth()
                    ) {
                        VerticalScroller(
                                modifier = Modifier.preferredHeight(300.dp)
                                        + Modifier.padding(10.dp)
                        ) {
                            Text(
                                    style = TextStyle(fontSize = 12.sp),
                                    text = log.value
                            )
                        }
                    }
                    Spacer(Modifier.preferredHeight(15.dp))
                    Surface(Modifier.clip(RoundedCornerShape(8.dp))) {
                        Button(
                                modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp),
                                backgroundColor = Color(54, 138, 244),
                                padding = InnerPadding(16.dp),
                                text = {
                                    Text(
                                            text = "复制到剪贴板",
                                            style = TextStyle(
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                    )
                                }, onClick = onClickCopy!!
                        )
                    }
                    Spacer(Modifier.preferredHeight(15.dp))
                    Surface(Modifier.clip(RoundedCornerShape(8.dp))) {
                        Button(
                                modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp),
                                backgroundColor = Color(54, 138, 244),
                                padding = InnerPadding(16.dp),
                                text = {
                                    Text(
                                            text = "格式设置",
                                            style = TextStyle(
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                    )
                                }, onClick = onClickSetting!!
                        )
                    }
                })
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Surface(color = Color(0xfff4f4f4)) {
            Column(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
                    children = {
                        Text(
                                modifier = Modifier.padding(
                                        start = 0.dp,
                                        top = 50.dp,
                                        end = 0.dp,
                                        bottom = 15.dp
                                ),
                                text = "日历助手",
                                style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                )
                        )
                        Text(
                                modifier = Modifier.padding(
                                        start = 0.dp,
                                        top = 0.dp,
                                        end = 0.dp,
                                        bottom = 15.dp
                                ),
                                text = "选择日期导出当日的日程",
                                style = TextStyle(
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                )
                        )
                        Surface(Modifier.clip(RoundedCornerShape(20))) {
                            Button(modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp) + Modifier.gravity(Alignment.CenterHorizontally),
                                    onClick = {},
                                    backgroundColor = Color(54, 138, 244),
                                    padding = InnerPadding(16.dp),
                                    text = {
                                        Text(
                                                text = "选择日期",
                                                style = TextStyle(
                                                        color = Color.White,
                                                        textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                    })
                        }
                        Spacer(Modifier.preferredHeight(10.dp))
                        Surface(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)) +
                                        Modifier.fillMaxWidth()
                        ) {
                            VerticalScroller(
                                    modifier = Modifier.preferredHeight(300.dp)
                                            + Modifier.padding(10.dp)
                            ) {
                                Text(
                                        style = TextStyle(fontSize = 12.sp),
                                        text = "log.value"
                                )
                            }
                        }
                        Spacer(Modifier.preferredHeight(10.dp))
                        Surface(Modifier.clip(RoundedCornerShape(20))) {
                            Button(modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp),
                                    backgroundColor = Color(54, 138, 244),
                                    padding = InnerPadding(16.dp),
                                    text = {
                                        Text(
                                                text = "复制到剪贴板",
                                                style = TextStyle(
                                                        color = Color.White,
                                                        textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                    }, onClick = {}
                            )
                        }
                        Spacer(Modifier.preferredHeight(15.dp))
                        Surface(Modifier.clip(RoundedCornerShape(8.dp))) {
                            Button(modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp),
                                    backgroundColor = Color(54, 138, 244),
                                    padding = InnerPadding(16.dp),
                                    text = {
                                        Text(
                                                text = "格式设置",
                                                style = TextStyle(
                                                        color = Color.White,
                                                        textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                    }, onClick = {}
                            )
                        }
                    })
        }
    }
}

@Model
class LogState(var value: String = "")