package cn.xiaobai.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.core.drawClip
import androidx.ui.core.setContent
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.vector.VectorPainter
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Check
import androidx.ui.material.icons.filled.KeyboardArrowRight
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.umeng.analytics.MobclickAgent

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DrawContent(navigation = {
                    finish()
                })
            }
        }
        val option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val decorView = window.decorView
        decorView.systemUiVisibility = option
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
fun DrawContent(navigation : () -> Unit) {
    Scaffold(
            topAppBar = {
                TopAppBar(
                        title = { Text(text = "格式设置") },
                        navigationIcon = {
                            IconButton(onClick = {
                                navigation()
                            }) {
                                Icon(Icons.Filled.ArrowBack)
                            }
                        },
                        backgroundColor = Color.White
                )
            },
            bodyContent = {
                val selectItem = state{ 0 }
                Surface(color = Color(0xfff4f4f4)) {
                    Column {
                        Spacer(Modifier.preferredHeight(10.dp))
                        Surface(color = Color.White) {
                            Clickable(onClick = {
                                selectItem.value = 0
                            }) {
                                RadioCell(text = "默认", checked = selectItem.value == 0)
                            }
                        }
                        Divider(color = Color(0xfff2f2f2))
                        Surface(color = Color.White) {
                            Clickable(onClick = {
                                selectItem.value = 1
                            }) {
                                RadioCell(text = "自定义", checked = selectItem.value == 1)
                            }
                        }
                        Divider(color = Color(0xfff2f2f2))
                    }
                }
            }
    )
}

@Preview
@Composable
fun DefaultView() {
    MaterialTheme {
        Scaffold(
                topAppBar = {
                    TopAppBar(
                            title = { Text(text = "格式设置") },
                            navigationIcon = {
                                IconButton(onClick = {  }) {
                                    Icon(Icons.Filled.ArrowBack)
                                }
                            },
                            backgroundColor = Color.White
                    )
                },
                bodyContent = {
                    Surface(color = Color(0xfff4f4f4)) {
                        Column {
                            Spacer(Modifier.preferredHeight(10.dp))
                            Surface(color = Color.White) {
                                RadioCell(text = "默认", checked = true)
                            }
                            Divider(color = Color(0xfff2f2f2))
                            Surface(color = Color.White) {
                                RadioCell(text = "自定义", checked = false)
                            }
                            Divider(color = Color(0xfff2f2f2))
                        }
                    }
                }
        )
    }
}

var selectIndex = 0

fun selectItem(index: Int) {
    selectIndex = index
}

@Composable
fun RadioCell(text: String, checked: Boolean) {
    Row(modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(50.dp) + Modifier.padding(15.dp)) {
        Text(text = text, style = TextStyle(fontSize = 16.sp),modifier = Modifier.weight(1f))
        if (checked) {
            Icon(Icons.Filled.Check, tint = Color(0xff07c160))
        }
    }
}