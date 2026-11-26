import 'package:flutter/services.dart';

/// Android 기본 TextToSpeech로 영어 단어 발음을 재생한다 (MainActivity.kt 의 채널과 짝).
class Speaker {
  Speaker._();

  static const _channel = MethodChannel('wordmate/tts');

  static Future<void> speak(String text) async {
    try {
      await _channel.invokeMethod('speak', {'text': text});
    } on PlatformException {
      // TTS 엔진이 없는 기기에서는 조용히 무시한다.
    } on MissingPluginException {
      // 테스트 환경 등 채널이 없을 때.
    }
  }
}
