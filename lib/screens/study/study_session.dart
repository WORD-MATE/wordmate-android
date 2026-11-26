import 'dart:math';

import 'package:flutter/foundation.dart';

import '../../data/models.dart';
import '../../data/store.dart';

class StudyQuestion {
  const StudyQuestion(this.word, this.prompt, this.answer, this.choices);

  final Word word;

  /// 화면에 크게 보여줄 문제 (영어 또는 뜻).
  final String prompt;

  /// 정답 텍스트.
  final String answer;

  /// 4지선다 보기. 다른 모드에서는 비어 있다.
  final List<String> choices;
}

/// 학습 한 판의 진행 상태.
class StudySession extends ChangeNotifier {
  StudySession({
    required this.pack,
    required this.options,
    bool onlyWrong = false,
    AppStore? appStore,
    Random? random,
  })  : _store = appStore ?? store,
        _random = random ?? Random() {
    final wrongIds = _store.lastStudyResult?.answers.where((a) => !a.correct).map((a) => a.word.id).toSet();
    var source = onlyWrong && wrongIds != null ? pack.words.where((w) => wrongIds.contains(w.id)).toList() : [...pack.words];
    if (options.shuffle) source.shuffle(_random);
    source = source.take(options.questionCount).toList();
    questions = [for (final w in source) _build(w)];
  }

  final WordPack pack;
  final StudyOptions options;
  final AppStore _store;
  final Random _random;

  late final List<StudyQuestion> questions;
  int index = 0;
  final answers = <StudyAnswer>[];

  /// 현재 문제에 답했는지, 맞았는지. null 이면 아직 답하지 않음.
  bool? answeredCorrect;
  int? selectedIndex;
  bool flipped = false;
  bool finished = false;
  StudyResult? result;

  StudyQuestion? get current => index < questions.length ? questions[index] : null;
  double get progress => questions.isEmpty ? 0 : (index + 1) / questions.length;
  bool get enToKo => options.direction == QuizDirection.enToKo;

  StudyQuestion _build(Word w) {
    final prompt = enToKo ? w.term : w.meaning;
    final answer = enToKo ? w.meaning : w.term;
    var choices = <String>[];
    if (options.mode == StudyMode.multipleChoice) {
      final distractors = pack.words
          .where((o) => o.id != w.id)
          .map((o) => enToKo ? o.meaning : o.term)
          .where((t) => t != answer)
          .toSet()
          .toList()
        ..shuffle(_random);
      choices = [...distractors.take(3), answer]..shuffle(_random);
    }
    return StudyQuestion(w, prompt, answer, choices);
  }

  void flip() {
    flipped = !flipped;
    notifyListeners();
  }

  /// 플래시카드: 알아요 / 몰라요.
  void markKnown(bool known) {
    final q = current;
    if (q == null) return;
    answers.add(StudyAnswer(q.word, known));
    next();
  }

  void select(int i) {
    final q = current;
    if (q == null || answeredCorrect != null) return;
    selectedIndex = i;
    _answer(q, q.choices[i] == q.answer);
  }

  void submitTyped(String input) {
    final q = current;
    if (q == null || answeredCorrect != null || input.trim().isEmpty) return;
    _answer(q, isTypedCorrect(input, q.answer));
  }

  void _answer(StudyQuestion q, bool correct) {
    answeredCorrect = correct;
    answers.add(StudyAnswer(q.word, correct));
    notifyListeners();
  }

  void next() {
    if (index + 1 >= questions.length) {
      _finish();
    } else {
      index++;
      answeredCorrect = null;
      selectedIndex = null;
      flipped = false;
    }
    notifyListeners();
  }

  void _finish() {
    result = StudyResult(
      packId: pack.id,
      packTitle: pack.title,
      options: options,
      answers: List.unmodifiable(answers),
      earnedXp: answers.where((a) => a.correct).length * options.mode.xpPerCorrect,
    );
    _store.recordStudy(result!);
    finished = true;
  }

  static String _normalize(String s) => s.toLowerCase().replaceAll(RegExp(r'[\s.,·~!?()]'), '');

  /// 쉼표·슬래시로 구분된 뜻 중 하나와 일치하면 정답.
  static bool isTypedCorrect(String input, String answer) {
    final typed = _normalize(input);
    if (typed.isEmpty) return false;
    return _normalize(answer) == typed || answer.split(RegExp('[,/;]')).map(_normalize).contains(typed);
  }
}
