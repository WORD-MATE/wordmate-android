import 'dart:async';

import 'package:flutter/foundation.dart';

import '../../data/models.dart';
import '../../data/store.dart';

/// 대결 한 판의 진행 상태 (문항 타이머, 점수, 오답).
class BattleGame extends ChangeNotifier {
  BattleGame({required this.room, AppStore? appStore}) : _store = appStore ?? store {
    questions = _store.battleQuestions(room);
    remainingMs = totalMs;
    _startTimer();
  }

  static const tickMs = 100;
  static const revealMs = 1200;

  final BattleRoom room;
  final AppStore _store;

  late final List<BattleQuestion> questions;
  int index = 0;
  late int remainingMs;
  int? selectedIndex;
  bool revealed = false;
  int score = 0;
  int lastGain = 0;
  int correctCount = 0;
  final wrongWords = <Word>[];
  String? reaction;
  bool finished = false;

  Timer? _timer;
  Timer? _revealTimer;
  Timer? _reactionTimer;

  BattleQuestion? get question => index < questions.length ? questions[index] : null;
  int get totalMs => room.settings.secondsPerQuestion * 1000;
  double get timeFraction => remainingMs / totalMs;

  void _startTimer() {
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(milliseconds: tickMs), (t) {
      remainingMs = (remainingMs - tickMs).clamp(0, totalMs);
      if (remainingMs == 0) {
        t.cancel();
        _reveal(null);
      } else {
        notifyListeners();
      }
    });
  }

  void select(int i) {
    if (!revealed) _reveal(i);
  }

  /// 속도 모드는 남은 시간에 비례해 최대 50점을 더 준다.
  static int scoreFor({required bool correct, required BattleMode mode, required double timeFraction}) {
    if (!correct) return 0;
    if (mode == BattleMode.accuracy) return 100;
    return 100 + (50 * timeFraction).floor();
  }

  void _reveal(int? selected) {
    _timer?.cancel();
    final q = question;
    if (q == null) return;
    final correct = selected == q.answerIndex;
    lastGain = scoreFor(correct: correct, mode: room.settings.mode, timeFraction: timeFraction);
    selectedIndex = selected;
    revealed = true;
    score += lastGain;
    if (correct) {
      correctCount++;
    } else {
      wrongWords.add(q.word);
    }
    notifyListeners();
    _revealTimer = Timer(const Duration(milliseconds: revealMs), _next);
  }

  void _next() {
    if (index + 1 >= questions.length) {
      _store.finishBattle(room, score, correctCount, List.of(wrongWords));
      finished = true;
      notifyListeners();
      return;
    }
    index++;
    selectedIndex = null;
    revealed = false;
    lastGain = 0;
    remainingMs = totalMs;
    notifyListeners();
    _startTimer();
  }

  void react(String emoji) {
    _reactionTimer?.cancel();
    reaction = emoji;
    notifyListeners();
    _reactionTimer = Timer(const Duration(milliseconds: 1200), () {
      reaction = null;
      notifyListeners();
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    _revealTimer?.cancel();
    _reactionTimer?.cancel();
    super.dispose();
  }
}
