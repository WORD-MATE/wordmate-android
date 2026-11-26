import 'models.dart';

/// 서버 연동 전까지 사용하는 샘플 데이터. Figma 시안의 문구를 그대로 사용한다.
class SampleData {
  SampleData._();

  static const user = User(
    name: '한국',
    schoolInfo: '2학년 1반 10번',
    title: '꾸준한 학습자',
    level: 12,
    xp: 412,
    xpToNextLevel: 500,
    todayXp: 86,
    todayXpGoal: 500,
    tier: '실버',
    streakDays: 7,
    totalCorrectWords: 23,
    battleWins: 1,
  );

  static List<AppNotification> notifications() => [
        AppNotification(1, NotificationType.battleInvite, '대결 초대', '양지우님이 대결방에 초대했어요. 코드 K7Q2MX', '55분 전'),
        AppNotification(2, NotificationType.dailySentence, '오늘의 문장', 'Small habits, repeated every day...', '11시간 전'),
        AppNotification(3, NotificationType.friendRequest, '친구 요청', '육몰겸님이 친구 요청을 보냈어요.', '2시간 전'),
      ];

  static int _id = 1;

  static Word _w(String term, String meaning,
          {String pron = '', String pos = '', String ex = '', String exKo = '', WordStatus status = WordStatus.fresh, bool starred = false}) =>
      Word(
        id: _id++,
        term: term,
        meaning: meaning,
        pronunciation: pron,
        partOfSpeech: pos,
        example: ex,
        exampleMeaning: exKo,
        status: status,
        starred: starred,
      );

  static List<Word> _suneung() => [
        _w('abandon', '버리다, 포기하다', pron: '/əˈbændən/', pos: '동사', ex: 'They had to abandon the car.', status: WordStatus.memorized),
        _w('accurate', '정확한', pron: '/ˈækjərət/', pos: '형용사', ex: 'The report was accurate.', status: WordStatus.learning),
        _w('achieve', '성취하다, 이루다', pron: '/əˈtʃiːv/', pos: '동사', ex: 'She achieved her goal.', status: WordStatus.memorized, starred: true),
        _w('adapt', '적응하다', pron: '/əˈdæpt/', pos: '동사', ex: 'Animals adapt to their environment.', status: WordStatus.memorized),
        _w('adequate', '충분한, 적절한', pron: '/ˈædɪkwət/', pos: '형용사', ex: 'We need adequate time.'),
        _w('analyze', '분석하다', pron: '/ˈænəlaɪz/', pos: '동사', ex: 'Scientists analyze the data.', status: WordStatus.learning),
        _w('anticipate', '예상하다, 기대하다', pron: '/ænˈtɪsɪpeɪt/', pos: '동사', ex: 'We anticipate a large crowd tonight.'),
        _w('attempt', '시도하다', pron: '/əˈtempt/', pos: '동사', ex: 'He attempted to climb the wall.', status: WordStatus.learning),
        _w('consequence', '결과, 영향', pron: '/ˈkɑːnsɪkwens/', pos: '명사', ex: 'Every action has a consequence.', status: WordStatus.memorized),
        _w('decline', '감소하다, 거절하다', pron: '/dɪˈklaɪn/', pos: '동사', ex: 'Sales declined last year.', status: WordStatus.memorized),
        _w('distinguish', '구별하다', pron: '/dɪˈstɪŋɡwɪʃ/', pos: '동사', ex: 'Can you distinguish the twins?', status: WordStatus.memorized),
        _w('flexible', '유연한', pron: '/ˈfleksəbl/', pos: '형용사', ex: 'Our schedule is flexible.', status: WordStatus.memorized),
        _w('benefit', '이익, 혜택', pron: '/ˈbenɪfɪt/', pos: '명사', ex: 'Exercise has many benefits.', status: WordStatus.memorized),
        _w('conclude', '결론짓다', pron: '/kənˈkluːd/', pos: '동사', ex: 'They concluded the meeting.', status: WordStatus.memorized),
        _w('emphasize', '강조하다', pron: '/ˈemfəsaɪz/', pos: '동사', ex: 'She emphasized the point.'),
        _w('essential', '필수적인', pron: '/ɪˈsenʃl/', pos: '형용사', ex: 'Water is essential for life.'),
        _w('evaluate', '평가하다', pron: '/ɪˈvæljueɪt/', pos: '동사', ex: 'Teachers evaluate students.'),
        _w('frequent', '빈번한', pron: '/ˈfriːkwənt/', pos: '형용사', ex: 'He is a frequent visitor.'),
        _w('generate', '발생시키다', pron: '/ˈdʒenəreɪt/', pos: '동사', ex: 'Wind can generate power.'),
        _w('habit', '습관', pron: '/ˈhæbɪt/', pos: '명사', ex: 'Reading is a good habit.', status: WordStatus.memorized),
        _w('identify', '확인하다, 식별하다', pron: '/aɪˈdentɪfaɪ/', pos: '동사', ex: 'Can you identify him?'),
        _w('maintain', '유지하다', pron: '/meɪnˈteɪn/', pos: '동사', ex: 'Maintain a healthy diet.', status: WordStatus.memorized),
        _w('obtain', '얻다', pron: '/əbˈteɪn/', pos: '동사', ex: 'She obtained a license.'),
        _w('shape', '형성하다, 모양', pron: '/ʃeɪp/', pos: '동사', ex: 'Habits shape who we are.', status: WordStatus.memorized),
      ];

  static WordPack _simple(int id, String title, String desc, PackCategory cat, List<(String, String)> terms, int learned,
      {bool mine = false}) {
    var i = 0;
    return WordPack(
      id: id,
      title: title,
      description: desc,
      category: cat,
      isMine: mine,
      words: [
        for (final (t, m) in terms) _w(t, m, status: (i++ < learned) ? WordStatus.memorized : WordStatus.fresh),
      ],
    );
  }

  static List<WordPack> packs() {
    _id = 1;
    return [
      WordPack(id: 1, title: '수능 필수 영단어 Day 12', description: '고2 수준 핵심 어휘 24개', category: PackCategory.suneung, words: _suneung()),
      _simple(2, '교과서 2학년 3단원', 'Lesson 3. The Power of Habits', PackCategory.textbook, [
        ('routine', '일상'), ('repeat', '반복하다'), ('quietly', '조용히'), ('improve', '향상시키다'),
        ('effort', '노력'), ('progress', '진전'), ('goal', '목표'), ('daily', '매일의'),
      ], 8),
      _simple(3, 'IT 기술 영어', '개발 문서에 자주 나오는 표현', PackCategory.it, [
        ('deploy', '배포하다'), ('compile', '컴파일하다'), ('debug', '디버그하다'), ('framework', '프레임워크'),
        ('latency', '지연 시간'), ('repository', '저장소'), ('query', '질의'), ('cache', '캐시'),
        ('thread', '스레드'), ('variable', '변수'),
      ], 1),
      _simple(4, '토익 빈출 동사', '비즈니스 상황 필수 동사 30개', PackCategory.toeic, [
        ('negotiate', '협상하다'), ('submit', '제출하다'), ('postpone', '연기하다'), ('attend', '참석하다'),
        ('reimburse', '변제하다'), ('renew', '갱신하다'), ('launch', '출시하다'), ('assign', '배정하다'),
      ], 0),
      _simple(5, '중간고사 대비', '시험 범위 핵심 단어', PackCategory.textbook, [
        ('abandon', '버리다'), ('accurate', '정확한'), ('achieve', '성취하다'), ('adapt', '적응하다'),
        ('adequate', '충분한'), ('analyze', '분석하다'), ('anticipate', '예상하다'), ('attempt', '시도하다'),
      ], 3, mine: true),
      _simple(6, '헷갈리는 단어 모음', 'affect·effect 같은 혼동 어휘', PackCategory.suneung, [
        ('affect', '영향을 미치다'), ('effect', '효과'), ('adopt', '채택하다'), ('adapt', '적응하다'),
      ], 1, mine: true),
    ];
  }

  static int nextWordId() => _id++;

  static const sentence = DailySentence(
    '오늘의 문장 · 11월 8일',
    'Small habits, repeated every day, quietly shape who we become.',
    ['habits', 'repeated', 'shape'],
    '매일 반복되는 작은 습관이 조용히 우리를 만들어 간다.',
  );

  static List<DailyMission> missions() => [
        DailyMission(1, '대결 1회 참여', 1, 1, 15),
        DailyMission(2, '단어 20개 학습', 14, 20, 20),
        DailyMission(3, '오늘의 문장 퀴즈', 0, 1, 10),
      ];

  static const _names = ['양지우', '김도윤', '이서연', '박하준', '최유나', '정민재', '강예린', '윤시우', '장서윤', '임건우'];
  static const _deltas = [3, -1, 0, 2, -4, 1];

  static List<RankingEntry> ranking(int seed) => [
        for (var rank = 1; rank <= 30; rank++)
          rank == 23
              ? RankingEntry(23, '한국', '2학년 1반', 0, 5, isMe: true)
              : RankingEntry(
                  rank,
                  _names[(rank + seed) % _names.length],
                  '${rank % 3 + 1}학년 ${rank % 5 + 1}반',
                  1654 - rank * 54 + seed * 7,
                  _deltas[(rank + seed) % _deltas.length],
                ),
      ];

  static List<BattleRecord> battleRecords() => [
        const BattleRecord(1, '수능 필수 영단어 Day 12', '오늘 · 8명 참여', 8, 1, 1320),
        const BattleRecord(2, '교과서 2학년 3단원', '어제 · 4명 참여', 4, 2, 1116),
        const BattleRecord(3, 'IT 기술 영어', '11월 5일 · 6명 참여', 6, 3, 1050),
        const BattleRecord(4, '토익 빈출 동사', '11월 2일 · 5명 참여', 5, 4, 890),
      ];

  static const badges = [
    RankBadge('10월 3주차 주간 상위 10%', '학교 전체 · 10월 21일', Medal.silver),
    RankBadge('10월 3주차 주간 1등', '2학년 1반 · 10월 21일', Medal.gold, isNew: true),
    RankBadge('10월 2주차 주간 상위 10%', '학교 전체 · 10월 14일', Medal.silver),
    RankBadge('9월 월간 3등', '2학년 1반 · 9월 30일', Medal.bronze),
    RankBadge('9월 4주차 주간 상위 10%', '학교 전체 · 9월 28일', Medal.silver),
  ];
}
