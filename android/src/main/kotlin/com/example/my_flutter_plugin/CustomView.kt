package com.example.my_flutter_plugin

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.platform.PlatformView


/**
 * 1，实现原生组件PlatformView提供原生view
 * 2，创建PlatformViewFactory用于生成PlatformView
 * 3，创建FlutterPlugin用于注册原生组件
 */
class CustomView constructor(private val context: Context,
                             content: String,
                             binaryMessenger: BinaryMessenger) : PlatformView {

    private var textView: TextView

    companion object {
        val TAG: String = CustomView::class.java.simpleName;
    }

    /**
     * EventChannel 为Native端主动想向Flutter端发送事件
     */
    private val eventChannel = EventChannel(binaryMessenger, "customView")
    private var events: EventChannel.EventSink? = null

    init {
        // 好像需要先初始化原生的EventChannel，再初始化 Flutter 的EventChannel
        eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                this@CustomView.events = events
            }

            override fun onCancel(arguments: Any?) {
                this@CustomView.events = null
            }
        })

        Handler().postDelayed({
            events?.success("1秒后的事件")
        }, 1000);

        textView = TextView(context)
        textView.setBackgroundColor(Color.RED)
        textView.setTextColor(Color.WHITE)
        textView.text = content
        textView.setOnClickListener {
            Toast.makeText(context, "点击了。。。", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * getView()会多次调用，因此将View的创建放在初始化里面，这里返回一个View.
     */
    override fun getView(): View {
        return textView
    }


    // 当View销毁时，去释放资源
    override fun dispose() {

    }


}