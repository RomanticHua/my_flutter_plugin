import 'dart:async';
import 'package:flutter/services.dart';

class MyFlutterPlugin {
  static const MethodChannel _channel =
      const MethodChannel('my_flutter_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // 参数是单个值，直接传递到Native端
  static void toast(String message) => _channel.invokeMethod('toast', message);

  static Future<String> getDetail(int age, String sex) async {
    // 参数多个值，采用Map来传递
    var arguments = {'age': age, 'sex': sex};
    String detail = await _channel.invokeMethod('getDetail', arguments);
    return detail;
  }

  static int channelId = 1;

  static void nativeToFlutter() {
    // 为了保证每次channel的name不一样，这里采用一个变量自增的方式。
    channelId++;

    // 这里有点很奇怪，因为在Native对应的方法里面也注册了一个EventChannel，这里需要先调用方法，再创建EventChannel
    _channel.invokeMethod('nativeToFlutter', {"channelId": channelId});

    // 在Flutter和Native两端都创建一个EventChannel实例，在Flutter端接受事件，在Native端发送事件
    EventChannel('my_event_channel$channelId')
        .receiveBroadcastStream()
        .listen((event) {
      toast('flutter ... event :$event');
    });
  }
}
