import 'package:flutter_test/flutter_test.dart';
import 'package:wordmate/data/models.dart';
import 'package:wordmate/data/store.dart';
import 'package:wordmate/screens/battle/battle_game.dart';

void main() {
  test('속도 모드는 빨리 맞힐수록 점수가 높고, 정확도 모드는 100점 고정', () {
    expect(BattleGame.scoreFor(correct: true, mode: BattleMode.speed, timeFraction: 1), 150);
    expect(BattleGame.scoreFor(correct: true, mode: BattleMode.speed, timeFraction: 0.2), 110);
    expect(BattleGame.scoreFor(correct: true, mode: BattleMode.accuracy, timeFraction: 0.9), 100);
    expect(BattleGame.scoreFor(correct: false, mode: BattleMode.speed, timeFraction: 1), 0);
  });

  testWidgets('모든 문제를 풀면 결과와 기록이 저장된다', (tester) async {
    final s = AppStore();
    final room = s.createRoom(BattleSettings(pack: s.packs.first, questionCount: 3, secondsPerQuestion: 5));
    final records = s.battleRecords.length;
    final game = BattleGame(room: room, appStore: s);

    for (var i = 0; i < 3; i++) {
      final q = game.question!;
      game.select(i == 0 ? q.answerIndex : (q.answerIndex + 1) % 4);
      await tester.pump(const Duration(milliseconds: BattleGame.revealMs + 50));
    }

    expect(game.finished, isTrue);
    expect(game.correctCount, 1);
    expect(game.wrongWords.length, 2);
    expect(s.lastBattleResult!.correctCount, 1);
    expect(s.battleRecords.length, records + 1);
    game.dispose();
  });

  testWidgets('시간이 지나면 오답 처리된다', (tester) async {
    final s = AppStore();
    final room = s.createRoom(BattleSettings(pack: s.packs.first, questionCount: 2, secondsPerQuestion: 5));
    final game = BattleGame(room: room, appStore: s);
    await tester.pump(const Duration(seconds: 5, milliseconds: 100));
    expect(game.revealed, isTrue);
    expect(game.selectedIndex, isNull);
    expect(game.wrongWords.length, 1);
    game.dispose();
  });
}
