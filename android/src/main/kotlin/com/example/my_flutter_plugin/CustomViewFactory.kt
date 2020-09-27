package com.example.my_flutter_plugin

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class CustomViewFactory(private val binaryMessenger: BinaryMessenger,
                        messageCodec: MessageCodec<Any>) : PlatformViewFactory(messageCodec) {

    /**
     * 创建View的工厂，用来接受Flutter端的参数并创建View
     */
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val map = args as Map<String, Any>
        return CustomView(context!!, map["content"].toString(), binaryMessenger)
    }
}