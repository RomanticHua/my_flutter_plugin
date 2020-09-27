package com.example.my_flutter_plugin

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.*
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.ref.WeakReference


/**
 * MethodChannel：用于传递方法调用（method invocation，即Flutter端可以调用Platform端的方法并通过Result接口回调结果数据。
 * EventChannel: 用于数据流（event streams）的通信，即Flutter端监听Platform端的实时消息，一旦Platform端产生了数据，立即回调到Flutter端。
 */
class MyFlutterPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {


    private lateinit var channel: MethodChannel
    private var activityWeakReference: WeakReference<Activity>? = null

    private lateinit var binaryMessenger: BinaryMessenger


    /**
     * Flutter 插件绑定到 FlutterEngine
     */
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "my_flutter_plugin")
        channel.setMethodCallHandler(this)
        binaryMessenger = flutterPluginBinding.binaryMessenger
        // 注册View，codec是用于方法调用和封装结果的编解码器,决定了我们能传递什么类型的数据。
        val customViewFactory = CustomViewFactory(flutterPluginBinding.binaryMessenger, StandardMessageCodec())
        flutterPluginBinding.platformViewRegistry.registerViewFactory("jinText", customViewFactory)

    }

    /**
     * 适配Flutter老版本写法，registerWith的代码需要和 onAttachedToEngine()里面的代码保持一致。
     */
    companion object {
        val TAG: String = MyFlutterPlugin::class.java.simpleName

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "my_flutter_plugin")
            channel.setMethodCallHandler(MyFlutterPlugin())

            // 注册View
            val customViewFactory = CustomViewFactory(registrar.messenger(), StandardMessageCodec())
            registrar.platformViewRegistry().registerViewFactory("jinText", customViewFactory)

        }
    }

    /**
     * 当Flutter的方法调用时回调
     */
    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val activity = activityWeakReference?.get() ?: return
        when (call.method) {
            "getPlatformVersion" -> {
//                result.success("Android ${android.os.Build.VERSION.RELEASE}")
                result.error("1111","错误消息...","detail...")
            }
            "toast" -> {
                // 如果Flutter只传递一个值过来，则可以使用 arguments()来接受这个参数
                val msg = call.arguments<String>()
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }
            "getDetail" -> {
                // 当Flutter传递过来的是一个Map或者JSONObject的时候，采用此方式接收
                val age = call.argument<Int>("age")
                val sex = call.argument<String>("sex")
                Handler(Looper.getMainLooper()).postDelayed({
                    result.success(" age is $age , sex is $sex")
                }, 2000)
            }

            "nativeToFlutter" -> {
                val channelId = call.argument<Int>("channelId")
                val eventChannel = EventChannel(binaryMessenger, "my_event_channel$channelId")
                eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
                    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                        Log.e(TAG, "onListen...")
                        events?.let {
                            it.success("data...")
                            it.success("end...")
                            // endOfStream() 标志这个EventChannel结束
                            it.endOfStream()
                        }
                    }

                    override fun onCancel(arguments: Any?) {
                        Log.e(TAG, "onCancel..")
                    }
                })
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Flutter插件从FlutterEngine解绑
     */
    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    /**
     * 得到当前的Activity
     */
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityWeakReference = WeakReference(binding.activity)
        Log.e(TAG,"onAttachedToActivity ${binding.activity}")
    }

    /**
     * 从当前Activity解绑
     */
    override fun onDetachedFromActivity() {
        activityWeakReference = null
        Log.e(TAG,"onDetachedFromActivity")
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.e(TAG, "onDetachedFromActivityForConfigChanges")

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        Log.e(TAG, "onReattachedToActivityForConfigChanges:${binding.activity}")
    }

}
