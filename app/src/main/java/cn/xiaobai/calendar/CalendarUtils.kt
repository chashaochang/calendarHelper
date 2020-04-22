package cn.xiaobai.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/** Events table columns  */
val eventsColumns = arrayOf(
    CalendarContract.Events._ID,
    CalendarContract.Events.CALENDAR_ID,
    CalendarContract.Events.TITLE,
    CalendarContract.Events.DESCRIPTION,
    CalendarContract.Events.EVENT_LOCATION,
    CalendarContract.Events.DTSTART,
    CalendarContract.Events.DTEND,
    CalendarContract.Events.EVENT_TIMEZONE,
    CalendarContract.Events.HAS_ALARM,
    CalendarContract.Events.ALL_DAY,
    CalendarContract.Events.AVAILABILITY,
    CalendarContract.Events.ACCESS_LEVEL,
    CalendarContract.Events.STATUS
)


@SuppressLint("MissingPermission")
fun getCalendarEvent(context: Context, calendar: Calendar): JSONArray? {
    val eventsUri: Uri = CalendarContract.Events.CONTENT_URI
    var startTime: Long?
    var endTime: Long?
    var eventTitle: String
    var description: String
    var location: String
    val arr = JSONArray()
    val dtStart = calendar.time.time
    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1)
    val dtEnd = calendar.time.time
    Log.i("getCalendarEvent", "dtStart: $dtStart")
    Log.i("getCalendarEvent", "dtEnd: $dtEnd")
    var eventCursor: Cursor? = null
    try {
        eventCursor = context.contentResolver.query(
            eventsUri,
            eventsColumns,
            "dtstart>=$dtStart and dtend<=$dtEnd",
            null,
            "dtstart"
        )
        while (eventCursor!!.moveToNext()) {
            val json = JSONObject()
            eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"))
            description = eventCursor.getString(eventCursor.getColumnIndex("description"))
            location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"))
            startTime = eventCursor.getString(eventCursor.getColumnIndex("dtstart")).toLong()
            endTime = eventCursor.getString(eventCursor.getColumnIndex("dtend")).toLong()
            try {
                json.put("eventTitle", eventTitle)
                json.put("description", description)
                json.put("location", location)
                json.put("startTime", startTime)
                json.put("endTime", endTime)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            arr.put(json)
        }
    } finally {
        eventCursor?.close()
    }

    return arr
}

/**
 * 时间戳转换为字符串
 *
 * @param time:时间戳
 * @return
 */
fun timeStamp2Date(time: Long): String? {
    val format = "ah:mm"
    val sdf = SimpleDateFormat(format, Locale.CHINA)
    return sdf.format(Date(time))
}
