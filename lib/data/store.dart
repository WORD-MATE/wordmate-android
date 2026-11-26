import 'dart:math';

import 'package:flutter/foundation.dart';

import 'models.dart';
import 'sample_data.dart';

/// 앱 전역 상태. 현재는 메모리 기반이며, 서버 API가 준비되면
/// 같은 메서드 시그니처를 유지한 채 내부만 API 호출로 바꾼다.
class AppStore extends ChangeNotifier {
  AppStore({Random? random}) : _random = random ?? Random();

  final Random _random;

  bool loggedIn = false;
  User user = SampleData.user;
  final List<AppNotification> notifications = SampleData.notifications();
  final List<WordPack> packs = SampleData.packs();
  final List<DailyMission> missions = SampleData.missions();
  final DailySentence sentence = SampleData.sentence;
  final List<BattleRecord> battleRecords = SampleData.battleRecords();
  final List<RankBadge> badges = SampleData.badges;

  StudyResult? lastStudyResult;
  BattleRoom? currentRoom;
  BattleResult? lastBattleResult;

  // ---------- 인증 ----------
  void login() {
    loggedIn = true;
    notifyListeners();
  }

  void logout() {
    loggedIn = false;
    notifyListeners();
  }

  // ---------- 알림 ----------
  int get unreadCount => notifications.where((n) => !n.read).length;

  void markRead(int id) {
    notifications.firstWhere((n) => n.id == id).read = true;
    notifyListeners();
  }

  void markAllRead() {
    for (final n in notifications) {
      n.read = true;
    }
    notifyListeners();
  }

  // ---------- 단어팩 ----------
  WordPack? pack(int id) {
    for (final p in packs) {
      if (p.id == id) return p;
    }
    return null;
  }

  WordPack? get continuePack {
    final last = lastStudyResult == null ? null : pack(lastStudyResult!.packId);
    return last ?? packs.where((p) => !p.isCompleted).firstOrNull;
  }

  void toggleStar(Word word) {
    word.starred = !word.starred;
    notifyListeners();
  }

  WordPack createPack(String title, String description, List<(String, String)> words) {
    final pack = WordPack(
      id: packs.map((p) => p.id).fold(0, max) + 1,
      title: title.trim(),
      description: description.trim().isEmpty ? '내 단어팩' : description.trim(),
      category: PackCategory.all,
      isMine: true,
      words: [for (final (t, m) in words) Word(id: SampleData.nextWordId(), term: t.trim(), meaning: m.trim())],
    );
    packs.add(pack);
    notifyListeners();
    return pack;
  }

  void deletePack(WordPack pack) {
    packs.remove(pack);
    notifyListeners();
  }

  void addWord(WordPack pack, String term, String meaning) {
    pack.words.add(Word(id: SampleData.nextWordId(), term: term.trim(), meaning: meaning.trim()));
    notifyListeners();
  }

  void updateWord(Word word, String term, String meaning) {
    word
      ..term = term.trim()
      ..meaning = meaning.trim();
    notifyListeners();
  }

  void deleteWord(WordPack pack, Word word) {
    pack.words.remove(word);
    notifyListeners();
  }

  // ---------- 미션·XP ----------
  void _addXp(int amount) => user = user.copyWith(xp: user.xp + amount, todayXp: user.todayXp + amount);

  void claimMission(DailyMission mission) {
    if (!mission.isDone || mission.claimed) return;
    mission.claimed = true;
    _addXp(mission.rewardXp);
    notifyListeners();
  }

  void _progressMission(int id, int amount) {
    final m = missions.where((m) => m.id == id).firstOrNull;
    if (m != null) m.current = min(m.goal, m.current + amount);
  }

  // ---------- 학습 ----------
  void recordStudy(StudyResult result) {
    for (final a in result.answers) {
      a.word.status = a.correct ? WordStatus.memorized : WordStatus.learning;
    }
    _addXp(result.earnedXp);
    user = user.copyWith(totalCorrectWords: user.totalCorrectWords + result.correctCount);
    _progressMission(2, result.answers.length);
    lastStudyResult = result;
    notifyListeners();
  }

  // ---------- 랭킹 ----------
  List<RankingEntry> ranking(RankingScope scope, RankingPeriod period) {
    final list = SampleData.ranking(scope.index * 3 + period.index)
        .map((e) => e.isMe ? e.copyWith(name: user.name, xp: user.xp) : e)
        .toList()
      ..sort((a, b) => b.xp.compareTo(a.xp));
    return [for (var i = 0; i < list.length; i++) list[i].copyWith(rank: i + 1)];
  }

  // ---------- 대결 ----------
  static const _codeChars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';

  BattleRoom createRoom(BattleSettings settings) {
    final code = List.generate(6, (_) => _codeChars[_random.nextInt(_codeChars.length)]).join();
    currentRoom = BattleRoom(code: code, settings: settings, players: [
      BattlePlayer(user.name, '2학년 1반', isHost: true, isMe: true),
      const BattlePlayer('양지우', '2학년 1반'),
    ]);
    return currentRoom!;
  }

  BattleRoom? joinRoom(String code) {
    if (code.length != 6) return null;
    currentRoom = BattleRoom(code: code.toUpperCase(), settings: BattleSettings(pack: packs.first), players: [
      const BattlePlayer('양지우', '2학년 1반', isHost: true),
      const BattlePlayer('김도윤', '2학년 3반'),
      BattlePlayer(user.name, '2학년 1반', isMe: true),
    ]);
    return currentRoom;
  }

  List<BattleQuestion> battleQuestions(BattleRoom room) {
    final pool = room.settings.pack.words.isEmpty ? packs.first.words : room.settings.pack.words;
    final allTerms = packs.expand((p) => p.words).map((w) => w.term).toSet();
    final picked = [...pool]..shuffle(_random);
    return [
      for (final word in picked.take(room.settings.questionCount))
        () {
          final distractors = ({...pool.map((w) => w.term), ...allTerms}..remove(word.term)).toList()..shuffle(_random);
          final choices = [...distractors.take(3), word.term]..shuffle(_random);
          return BattleQuestion(word.meaning, choices, choices.indexOf(word.term), word);
        }(),
    ];
  }

  BattleResult finishBattle(BattleRoom room, int myScore, int correct, List<Word> wrongWords) {
    final others = [
      for (final p in room.players.where((p) => !p.isMe)) (p.name, max(0, myScore + _random.nextInt(650) - 400)),
    ];
    final ranked = [...others, (user.name, myScore)]..sort((a, b) => b.$2.compareTo(a.$2));
    final standings = [
      for (var i = 0; i < ranked.length; i++)
        BattleStanding(
          i + 1,
          ranked[i].$1,
          ranked[i].$1 == user.name ? '정답 $correct개' : '정답 ${min(ranked[i].$2 ~/ 140, room.settings.questionCount)}개',
          ranked[i].$2,
          isMe: ranked[i].$1 == user.name,
        ),
    ];
    final myRank = standings.firstWhere((s) => s.isMe).rank;
    final earnedXp = switch (myRank) { 1 => 30, 2 => 20, 3 => 15, _ => 10 };
    _addXp(earnedXp);
    if (myRank == 1) user = user.copyWith(battleWins: user.battleWins + 1);
    _progressMission(1, 1);
    for (final w in wrongWords) {
      w.status = WordStatus.learning;
    }
    battleRecords.insert(
      0,
      BattleRecord(battleRecords.length + 1, room.settings.pack.title, '방금 · ${room.players.length}명 참여',
          room.players.length, myRank, myScore),
    );
    lastBattleResult = BattleResult(
      packId: room.settings.pack.id,
      packTitle: room.settings.pack.title,
      myRank: myRank,
      myScore: myScore,
      correctCount: correct,
      earnedXp: earnedXp,
      standings: standings,
      wrongWords: wrongWords,
    );
    notifyListeners();
    return lastBattleResult!;
  }
}

/// 앱 전체에서 공유하는 스토어 인스턴스.
final store = AppStore();
