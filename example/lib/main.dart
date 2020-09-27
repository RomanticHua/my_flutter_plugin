import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:my_flutter_plugin/my_flutter_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  void _registerCustomViewEvent() {
    EventChannel("customView").receiveBroadcastStream().listen((event) {
      MyFlutterPlugin.toast(event.toString());
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await MyFlutterPlugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  _toast() {
    MyFlutterPlugin.toast("帅哥~~");
  }

  void _getDetail() async {
    String detail = await MyFlutterPlugin.getDetail(18, 'man');
    MyFlutterPlugin.toast(detail);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: Column(
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              RaisedButton(
                onPressed: _toast,
                child: Text('打印Toast'),
              ),
              RaisedButton(
                onPressed: _getDetail,
                child: Text('获取详情'),
              ),

              RaisedButton(
                onPressed: () {
                  MyFlutterPlugin.nativeToFlutter();
                },
                child: Text('nativeToFlutter'),
              ),

              // AndroidView需要放置在一个限定了宽度和高度的容器中
              Container(
                width: 100,
                height: 100,
                child: AndroidView(
                  viewType: 'jinText',
                  // 如果设置了 creationParams ，则一定要设置 creationParamsCodec
                  creationParams: {"content": '大帅哥！！'},
                  creationParamsCodec: StandardMessageCodec(),
                  onPlatformViewCreated: (id) {
                    print("getView  onPlatformViewCreated...viewId ：$id");
                    _registerCustomViewEvent();
                  },
                ),
              )
            ],
          )),
    );
  }
}
