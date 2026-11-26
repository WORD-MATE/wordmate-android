import 'package:flutter_test/flutter_test.dart';
import 'package:wordmate/data/models.dart';
import 'package:wordmate/data/store.dart';
import 'package:wordmate/utils/format.dart';

void main() {
  test('랭킹은 XP 내림차순이고 순위가 1부터 이어진다', () {
    final s = AppStore();
    final list = s.ranking(RankingScope.school, RankingPeriod.weekly);
    for (var i = 0; i < list.length; i++) {
      expect(list[i].rank, i + 1);
      if (i > 0) expect(list[i - 1].xp >= list[i].xp, isTrue);
    }
    expect(list.where((e) => e.isMe).single.xp, s.user.xp);
  });

  test('학습 결과가 단어 상태와 XP에 반영된다', () {
    final s = AppStore();
    final pack = s.packs.first;
    final right = pack.words[4];
    final wrong = pack.words[5];
    final before = s.user.xp;
    s.recordStudy(StudyResult(
      packId: pack.id,
      packTitle: pack.title,
      options: const StudyOptions(),
      answers: [StudyAnswer(right, true), StudyAnswer(wrong, false)],
      earnedXp: 2,
    ));
    expect(right.status, WordStatus.memorized);
    expect(wrong.status, WordStatus.learning);
    expect(s.user.xp, before + 2);
  });

  test('완료한 미션만 한 번 XP를 받는다', () {
    final s = AppStore();
    final done = s.missions.first;
    final before = s.user.xp;
    s.claimMission(done);
    s.claimMission(done);
    expect(s.user.xp, before + done.rewardXp);
    s.claimMission(s.missions[1]);
    expect(s.user.xp, before + done.rewardXp);
  });

  test('대결 문제는 정답을 포함한 보기 4개를 가진다', () {
    final s = AppStore();
    final room = s.createRoom(BattleSettings(pack: s.packs.first));
    expect(room.code.length, 6);
    for (final q in s.battleQuestions(room)) {
      expect(q.choices.length, 4);
      expect(q.choices[q.answerIndex], q.word.term);
    }
  });

  test('숫자에 천 단위 쉼표를 넣는다', () {
    expect(formatNumber(1320), '1,320');
    expect(formatNumber(412), '412');
    expect(formatNumber(1234567), '1,234,567');
  });
}
