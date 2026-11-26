import 'package:flutter/material.dart';

final navigatorKey = GlobalKey<NavigatorState>();

/// 하단 탭 인덱스. 다른 화면에서 탭을 바꿀 때 사용한다.
final mainTab = ValueNotifier<int>(0);

class Tabs {
  Tabs._();
  static const home = 0;
  static const wordPacks = 1;
  static const battle = 2;
  static const ranking = 3;
  static const my = 4;
}

Future<T?> push<T>(BuildContext context, Widget screen) =>
    Navigator.of(context).push<T>(MaterialPageRoute(builder: (_) => screen));

Future<T?> replace<T>(BuildContext context, Widget screen) =>
    Navigator.of(context).pushReplacement<T, Object?>(MaterialPageRoute(builder: (_) => screen));

/// 메인 탭 화면까지 돌아가고 [tab] 을 선택한다.
void goToTab(BuildContext context, int tab) {
  mainTab.value = tab;
  Navigator.of(context).popUntil((route) => route.isFirst);
}
