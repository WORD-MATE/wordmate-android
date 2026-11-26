class User {
  const User({
    required this.name,
    required this.schoolInfo,
    required this.title,
    required this.level,
    required this.xp,
    required this.xpToNextLevel,
    required this.todayXp,
    required this.todayXpGoal,
    required this.tier,
    required this.streakDays,
    required this.totalCorrectWords,
    required this.battleWins,
  });

  final String name;
  final String schoolInfo;
  final String title;
  final int level;
  final int xp;
  final int xpToNextLevel;
  final int todayXp;
  final int todayXpGoal;
  final String tier;
  final int streakDays;
  final int totalCorrectWords;
  final int battleWins;

  User copyWith({int? xp, int? todayXp, int? battleWins, int? totalCorrectWords}) => User(
        name: name,
        schoolInfo: schoolInfo,
        title: title,
        level: level,
        xp: xp ?? this.xp,
        xpToNextLevel: xpToNextLevel,
        todayXp: todayXp ?? this.todayXp,
        todayXpGoal: todayXpGoal,
        tier: tier,
        streakDays: streakDays,
        totalCorrectWords: totalCorrectWords ?? this.totalCorrectWords,
        battleWins: battleWins ?? this.battleWins,
      );
}

enum NotificationType { battleInvite, dailySentence, friendRequest }

class AppNotification {
  AppNotification(this.id, this.type, this.title, this.body, this.timeAgo, {this.read = false});

  final int id;
  final NotificationType type;
  final String title;
  final String body;
  final String timeAgo;
  bool read;
}

enum PackCategory {
  all('전체'),
  suneung('수능'),
  textbook('교과서'),
  it('IT'),
  toeic('토익');

  const PackCategory(this.label);
  final String label;
}

enum WordStatus {
  fresh('새 단어'),
  learning('학습 중'),
  memorized('외운 단어');

  const WordStatus(this.label);
  final String label;
}

class Word {
  Word({
    required this.id,
    required this.term,
    required this.meaning,
    this.pronunciation = '',
    this.partOfSpeech = '',
    this.example = '',
    this.exampleMeaning = '',
    this.status = WordStatus.fresh,
    this.starred = false,
  });

  final int id;
  String term;
  String meaning;
  final String pronunciation;
  final String partOfSpeech;
  final String example;
  final String exampleMeaning;
  WordStatus status;
  bool starred;
}

class WordPack {
  WordPack({
    required this.id,
    required this.title,
    required this.description,
    required this.category,
    required this.words,
    this.isMine = false,
  });

  final int id;
  final String title;
  final String description;
  final PackCategory category;
  final List<Word> words;
  final bool isMine;

  String get sourceLabel => isMine ? '내 단어팩' : '기본 단어팩';
  int get wordCount => words.length;
  int get learnedCount => words.where((w) => w.status == WordStatus.memorized).length;
  double get progress => words.isEmpty ? 0 : learnedCount / words.length;
  bool get isCompleted => words.isNotEmpty && learnedCount == words.length;
}

enum StudyMode {
  flashcard('플래시카드', '카드를 넘기며 뜻을 떠올려요. 가볍게 훑어보기 좋아요.', 'XP +1 / 단어', 1),
  multipleChoice('4지선다', '4개의 보기 중 알맞은 뜻을 골라요. 가장 기본적인 학습이에요.', '정답당 XP +2', 2),
  typing('직접 입력', '단어를 보고 뜻을 직접 입력해요. 정확히 외웠는지 확인할 수 있어요.', '정답당 XP +3', 3);

  const StudyMode(this.title, this.description, this.xpHint, this.xpPerCorrect);
  final String title;
  final String description;
  final String xpHint;
  final int xpPerCorrect;
}

enum QuizDirection {
  enToKo('영어 보고 뜻 맞히기'),
  koToEn('뜻 보고 영어 맞히기');

  const QuizDirection(this.label);
  final String label;
}

class StudyOptions {
  const StudyOptions({
    this.mode = StudyMode.multipleChoice,
    this.direction = QuizDirection.enToKo,
    this.questionCount = 10,
    this.shuffle = true,
  });

  final StudyMode mode;
  final QuizDirection direction;
  final int questionCount;
  final bool shuffle;
}

class StudyAnswer {
  const StudyAnswer(this.word, this.correct);
  final Word word;
  final bool correct;
}

class StudyResult {
  const StudyResult({required this.packId, required this.packTitle, required this.options, required this.answers, required this.earnedXp});

  final int packId;
  final String packTitle;
  final StudyOptions options;
  final List<StudyAnswer> answers;
  final int earnedXp;

  int get correctCount => answers.where((a) => a.correct).length;
  int get wrongCount => answers.length - correctCount;
  int get accuracy => answers.isEmpty ? 0 : correctCount * 100 ~/ answers.length;
}

class DailySentence {
  const DailySentence(this.dateLabel, this.sentence, this.underlined, this.translation);
  final String dateLabel;
  final String sentence;
  final List<String> underlined;
  final String translation;
}

class DailyMission {
  DailyMission(this.id, this.title, this.current, this.goal, this.rewardXp, {this.claimed = false});

  final int id;
  final String title;
  int current;
  final int goal;
  final int rewardXp;
  bool claimed;

  bool get isDone => current >= goal;
}

class RankingEntry {
  const RankingEntry(this.rank, this.name, this.schoolInfo, this.xp, this.delta, {this.isMe = false});

  final int rank;
  final String name;
  final String schoolInfo;
  final int xp;
  final int delta;
  final bool isMe;

  RankingEntry copyWith({int? rank, String? name, int? xp}) =>
      RankingEntry(rank ?? this.rank, name ?? this.name, schoolInfo, xp ?? this.xp, delta, isMe: isMe);
}

enum RankingScope {
  school('학교 전체'),
  grade('1학년'),
  klass('2학년 1반'),
  friends('친구');

  const RankingScope(this.label);
  final String label;
}

enum RankingPeriod {
  weekly('주간'),
  monthly('월간'),
  all('전체');

  const RankingPeriod(this.label);
  final String label;
}

enum BattleMode {
  speed('빠른 쪽이 점수 더 받기'),
  accuracy('정확히 맞히면 점수');

  const BattleMode(this.label);
  final String label;
}

class BattleSettings {
  const BattleSettings({
    required this.pack,
    this.mode = BattleMode.speed,
    this.questionCount = 10,
    this.secondsPerQuestion = 10,
    this.maxPlayers = 8,
  });

  final WordPack pack;
  final BattleMode mode;
  final int questionCount;
  final int secondsPerQuestion;
  final int maxPlayers;
}

class BattlePlayer {
  const BattlePlayer(this.name, this.schoolInfo, {this.isHost = false, this.isMe = false});
  final String name;
  final String schoolInfo;
  final bool isHost;
  final bool isMe;
}

class BattleRoom {
  BattleRoom({required this.code, required this.settings, required this.players});
  final String code;
  final BattleSettings settings;
  final List<BattlePlayer> players;
}

class BattleQuestion {
  const BattleQuestion(this.prompt, this.choices, this.answerIndex, this.word);
  final String prompt;
  final List<String> choices;
  final int answerIndex;
  final Word word;
}

class BattleStanding {
  const BattleStanding(this.rank, this.name, this.detail, this.score, {this.isMe = false});
  final int rank;
  final String name;
  final String detail;
  final int score;
  final bool isMe;
}

class BattleResult {
  const BattleResult({
    required this.packId,
    required this.packTitle,
    required this.myRank,
    required this.myScore,
    required this.correctCount,
    required this.earnedXp,
    required this.standings,
    required this.wrongWords,
  });

  final int packId;
  final String packTitle;
  final int myRank;
  final int myScore;
  final int correctCount;
  final int earnedXp;
  final List<BattleStanding> standings;
  final List<Word> wrongWords;
}

class BattleRecord {
  const BattleRecord(this.id, this.packTitle, this.dateLabel, this.playerCount, this.rank, this.score);
  final int id;
  final String packTitle;
  final String dateLabel;
  final int playerCount;
  final int rank;
  final int score;
}

enum Medal { gold, silver, bronze }

class RankBadge {
  const RankBadge(this.title, this.dateLabel, this.medal, {this.isNew = false});
  final String title;
  final String dateLabel;
  final Medal medal;
  final bool isNew;
}
