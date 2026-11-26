import 'dart:math';

import 'package:flutter_test/flutter_test.dart';
import 'package:wordmate/data/models.dart';
import 'package:wordmate/data/store.dart';
import 'package:wordmate/screens/study/study_session.dart';

void main() {
  group('직접 입력 채점', () {
    test('쉼표로 구분된 뜻 중 하나만 맞아도 정답', () {
      expect(StudySession.isTypedCorrect('버리다', '버리다, 포기하다'), isTrue);
      expect(StudySession.isTypedCorrect(' 포기하다 ', '버리다, 포기하다'), isTrue);
    });
    test('영어 정답은 대소문자와 공백을 무시', () {
      expect(StudySession.isTypedCorrect('Abandon ', 'abandon'), isTrue);
    });
    test('빈 입력이나 다른 뜻은 오답', () {
      expect(StudySession.isTypedCorrect('', '구별하다'), isFalse);
      expect(StudySession.isTypedCorrect('구분', '구별하다'), isFalse);
    });
  });

  test('4지선다를 끝까지 풀면 결과가 저장되고 정답당 XP 2를 받는다', () {
    final s = AppStore();
    final pack = s.packs.first;
    final xp = s.user.xp;
    final session = StudySession(
      pack: pack,
      options: const StudyOptions(questionCount: 5),
      appStore: s,
      random: Random(1),
    );
    expect(session.questions.length, 5);
    for (final q in session.questions) {
      expect(q.choices.length, 4);
      expect(q.choices, contains(q.answer));
      session.select(q.choices.indexOf(q.answer));
      session.next();
    }
    expect(session.finished, isTrue);
    expect(session.result!.accuracy, 100);
    expect(s.user.xp, xp + 10);
    expect(s.lastStudyResult, same(session.result));
  });

  test('틀린 단어만 다시 풀기는 직전에 틀린 단어만 출제한다', () {
    final s = AppStore();
    final pack = s.packs.first;
    final first = StudySession(pack: pack, options: const StudyOptions(questionCount: 4), appStore: s, random: Random(2));
    final wrong = <int>{};
    for (var i = 0; i < first.questions.length; i++) {
      final q = first.questions[i];
      final pick = i.isEven ? q.choices.indexOf(q.answer) : q.choices.indexWhere((c) => c != q.answer);
      if (i.isOdd) wrong.add(q.word.id);
      first.select(pick);
      first.next();
    }
    final retry = StudySession(
      pack: pack,
      options: const StudyOptions(questionCount: 4),
      onlyWrong: true,
      appStore: s,
    );
    expect(retry.questions.map((q) => q.word.id).toSet(), wrong);
  });
}
