import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/format.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/layout.dart';
import '../battle/battle_lobby_screen.dart';
import '../study/study_setup_screen.dart';
import '../wordpack/pack_detail_screen.dart';
import 'notification_popup.dart';

/// 홈 (Figma "메인화면").
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: store,
      builder: (context, _) {
        final user = store.user;
        final pack = store.continuePack;
        final allWords = store.packs.expand((p) => p.words);
        final reviewCount = allWords.where((w) => w.status == WordStatus.memorized).length.clamp(0, 12);
        final wrongCount = allWords.where((w) => w.status == WordStatus.learning).length;
        final ranking = store.ranking(RankingScope.school, RankingPeriod.weekly);
        final me = ranking.firstWhere((e) => e.isMe);
        final nearby = ranking.where((e) => (e.rank - me.rank).abs() <= 1).toList();

        void study() => pack == null ? mainTab.value = Tabs.wordPacks : _study(context, pack);

        return Scaffold(
          appBar: LargeTopBar(
            title: 'wordmate',
            titleStyle: AppText.title1.copyWith(fontWeight: FontWeight.w800),
            actions: [
              AppIconButton(
                Icons.notifications_rounded,
                tooltip: '알림',
                badge: store.unreadCount > 0,
                onPressed: () => showNotificationPopup(context, onBattleInvite: () => _battleInvite(context)),
              ),
            ],
          ),
          body: ListView(
            padding: const EdgeInsets.only(bottom: 24),
            children: [
              _HeroCard(name: user.name, onStudy: study, onBattle: () => mainTab.value = Tabs.battle),
              _StatRow(user: user),
              const SectionHeader('오늘 할 일'),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
                child: Row(children: [
                  Expanded(child: _TodoCard('오늘의 복습', reviewCount, Icons.refresh_rounded, AppColors.info, study)),
                  Gap.w8,
                  Expanded(
                    child: _TodoCard('오답노트', wrongCount, Icons.close_rounded, AppColors.error,
                        () => pack == null ? null : _openPack(context, pack)),
                  ),
                ]),
              ),
              if (pack != null) ...[
                SectionHeader('이어서 학습', action: '전체 단어팩', onAction: () => mainTab.value = Tabs.wordPacks),
                ContinueCard(pack: pack, onTap: () => _openPack(context, pack)),
              ],
              Gap.h12,
              _SentenceCard(sentence: store.sentence, onQuiz: study),
              const SectionHeader('일일 미션'),
              _MissionCard(missions: store.missions),
              Gap.h12,
              _RankingPreview(me: me, nearby: nearby),
            ],
          ),
        );
      },
    );
  }
}

void _study(BuildContext context, WordPack pack) => push(context, StudySetupScreen(pack: pack));

void _openPack(BuildContext context, WordPack pack) => push(context, PackDetailScreen(pack: pack));

void _battleInvite(BuildContext context) => push(context, const BattleCodeScreen());

class _HeroCard extends StatelessWidget {
  const _HeroCard({required this.name, required this.onStudy, required this.onBattle});

  final String name;
  final VoidCallback onStudy;
  final VoidCallback onBattle;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      padding: const EdgeInsets.all(16),
      decoration: const BoxDecoration(gradient: AppColors.heroGradient, borderRadius: AppRadius.lg),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text.rich(
            TextSpan(children: [
              TextSpan(text: '$name님, 오늘도\n단어 '),
              const TextSpan(text: '10개', style: TextStyle(decoration: TextDecoration.underline)),
              const TextSpan(text: '만 해볼까요?'),
            ]),
            style: AppText.headline2,
          ),
          Gap.h16,
          Row(children: [
            Expanded(
              child: AppButton('학습하기',
                  kind: ButtonKind.dark, height: 44, trailing: Icons.chevron_right_rounded, onPressed: onStudy),
            ),
            Gap.w8,
            Expanded(child: AppButton('대결하기', kind: ButtonKind.mint, height: 44, onPressed: onBattle)),
          ]),
        ],
      ),
    );
  }
}

class _StatRow extends StatelessWidget {
  const _StatRow({required this.user});

  final User user;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(Gap.screen, 12, Gap.screen, 0),
      child: IntrinsicHeight(
        child: Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
          Expanded(
            child: _StatCard('연속 학습', Icons.local_fire_department_rounded, Palette.orange06,
                ValueUnit('${user.streakDays}', '일')),
          ),
          Gap.w8,
          Expanded(
            child: _StatCard(
              user.tier,
              Icons.military_tech_rounded,
              Palette.gray12,
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text('Lv.${user.level}', style: AppText.title2),
                const SizedBox(height: 6),
                ProgressBar(value: user.xp / user.xpToNextLevel, height: 4),
              ]),
            ),
          ),
          Gap.w8,
          Expanded(
            child: _StatCard(
              '오늘 XP',
              Icons.bolt_rounded,
              AppColors.primaryDark,
              Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                ValueUnit('${user.todayXp}', '/${user.todayXpGoal}'),
                const SizedBox(height: 6),
                ProgressBar(value: user.todayXp / user.todayXpGoal, height: 4),
              ]),
            ),
          ),
        ]),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  const _StatCard(this.label, this.icon, this.iconColor, this.child);

  final String label;
  final IconData icon;
  final Color iconColor;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      padding: const EdgeInsets.all(12),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Icon(icon, size: 14, color: iconColor),
          Gap.w4,
          Flexible(child: Text(label, style: AppText.caption1.copyWith(color: AppColors.textSecondary))),
        ]),
        Gap.h8,
        child,
      ]),
    );
  }
}

class _TodoCard extends StatelessWidget {
  const _TodoCard(this.title, this.count, this.icon, this.color, this.onTap);

  final String title;
  final int count;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: onTap,
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Container(
            width: 36,
            height: 36,
            decoration: const BoxDecoration(color: Palette.gray06, borderRadius: AppRadius.sm),
            child: Icon(icon, color: color, size: 20),
          ),
          const Spacer(),
          const Icon(Icons.chevron_right_rounded, color: AppColors.textTertiary, size: 20),
        ]),
        Gap.h16,
        Text(title, style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
        ValueUnit('$count', '단어'),
      ]),
    );
  }
}

/// 이어서 학습 카드. 단어팩 진행도를 보여준다.
class ContinueCard extends StatelessWidget {
  const ContinueCard({super.key, required this.pack, required this.onTap});

  final WordPack pack;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      onTap: onTap,
      child: Column(children: [
        Row(children: [
          Expanded(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(pack.title, style: AppText.body1Bold),
              const SizedBox(height: 2),
              Text('${pack.sourceLabel} · ${pack.description}',
                  style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
            ]),
          ),
          const Icon(Icons.chevron_right_rounded, color: AppColors.textTertiary),
        ]),
        Gap.h16,
        Row(children: [
          Expanded(child: ProgressBar(value: pack.progress)),
          Gap.w12,
          Text.rich(TextSpan(children: [
            TextSpan(text: '${pack.learnedCount}', style: AppText.caption1Bold),
            TextSpan(text: ' / ${pack.wordCount}', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ])),
        ]),
      ]),
    );
  }
}

class _SentenceCard extends StatelessWidget {
  const _SentenceCard({required this.sentence, required this.onQuiz});

  final DailySentence sentence;
  final VoidCallback onQuiz;

  @override
  Widget build(BuildContext context) {
    final tokens = sentence.sentence.split(' ');
    return AppCard(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(sentence.dateLabel, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        Gap.h8,
        Text.rich(
          TextSpan(children: [
            for (var i = 0; i < tokens.length; i++) ...() {
              final token = tokens[i];
              final bare = token.replaceAll(RegExp(r'[,.!?]'), '');
              final tail = i == tokens.length - 1 ? '' : ' ';
              return sentence.underlined.contains(bare)
                  ? [
                      TextSpan(text: bare, style: const TextStyle(decoration: TextDecoration.underline)),
                      TextSpan(text: token.substring(bare.length) + tail),
                    ]
                  : [TextSpan(text: token + tail)];
            }(),
          ]),
          style: AppText.title3.copyWith(fontWeight: FontWeight.w700),
        ),
        Gap.h16,
        Align(
          alignment: Alignment.centerRight,
          child: SmallButton('퀴즈 풀기', trailing: Icons.chevron_right_rounded, onPressed: onQuiz),
        ),
      ]),
    );
  }
}

class _MissionCard extends StatelessWidget {
  const _MissionCard({required this.missions});

  final List<DailyMission> missions;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      padding: EdgeInsets.zero,
      child: Column(children: [
        for (var i = 0; i < missions.length; i++) ...[
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(children: [
              _MissionCheck(done: missions[i].isDone),
              Gap.w12,
              Expanded(
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Row(children: [
                    Expanded(child: Text(missions[i].title, style: AppText.label1Bold)),
                    Text('${missions[i].current}/${missions[i].goal}',
                        style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                  ]),
                  const SizedBox(height: 6),
                  ProgressBar(value: missions[i].current / missions[i].goal, height: 4),
                ]),
              ),
              Gap.w12,
              _MissionAction(mission: missions[i]),
            ]),
          ),
          if (i != missions.length - 1) const AppDivider(indent: 16),
        ],
      ]),
    );
  }
}

class _MissionCheck extends StatelessWidget {
  const _MissionCheck({required this.done});

  final bool done;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 22,
      height: 22,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: done ? AppColors.primary : AppColors.background,
        border: Border.all(color: done ? AppColors.primary : AppColors.border, width: 1.5),
      ),
      child: done ? const Icon(Icons.check_rounded, size: 14, color: AppColors.onPrimary) : null,
    );
  }
}

class _MissionAction extends StatelessWidget {
  const _MissionAction({required this.mission});

  final DailyMission mission;

  @override
  Widget build(BuildContext context) {
    if (mission.isDone && !mission.claimed) {
      return SmallButton('+${mission.rewardXp} XP 받기', onPressed: () => store.claimMission(mission));
    }
    return SizedBox(
      width: 64,
      child: Text(
        mission.claimed ? '받음' : '+${mission.rewardXp} XP',
        textAlign: TextAlign.end,
        style: AppText.caption1Bold.copyWith(color: mission.claimed ? AppColors.textTertiary : AppColors.textSecondary),
      ),
    );
  }
}

class _RankingPreview extends StatelessWidget {
  const _RankingPreview({required this.me, required this.nearby});

  final RankingEntry me;
  final List<RankingEntry> nearby;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      child: Column(children: [
        Row(children: [
          Text('${me.rank}위', style: AppText.headline1),
          Gap.w8,
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: const BoxDecoration(color: Color(0xFFE6F0FF), borderRadius: AppRadius.xs),
            child: Row(mainAxisSize: MainAxisSize.min, children: [
              const Icon(Icons.arrow_upward_rounded, size: 12, color: AppColors.info),
              Text('${me.delta}', style: AppText.caption1Bold.copyWith(color: AppColors.info)),
            ]),
          ),
          const Spacer(),
          InkWell(
            onTap: () => mainTab.value = Tabs.ranking,
            borderRadius: AppRadius.sm,
            child: Padding(
              padding: const EdgeInsets.all(4),
              child: Row(children: [
                Text('전체 랭킹', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
                const Icon(Icons.chevron_right_rounded, size: 18, color: AppColors.textSecondary),
              ]),
            ),
          ),
        ]),
        Gap.h12,
        for (final e in nearby)
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: BoxDecoration(color: e.isMe ? Palette.gray06 : null, borderRadius: AppRadius.sm),
            child: Row(children: [
              SizedBox(width: 28, child: Text('${e.rank}', style: AppText.label1Bold)),
              Avatar(size: 28, background: e.isMe ? AppColors.background : Palette.gray06),
              const SizedBox(width: 10),
              Expanded(child: Text(e.isMe ? '나' : e.name, style: AppText.label1)),
              Text('${formatNumber(e.xp)} XP', style: AppText.label1Bold),
              SizedBox(
                width: 44,
                child: Text(
                  e.isMe ? '' : (e.xp > me.xp ? '+${e.xp - me.xp}' : '${e.xp - me.xp}'),
                  textAlign: TextAlign.end,
                  style: AppText.caption1.copyWith(color: AppColors.textTertiary),
                ),
              ),
            ]),
          ),
      ]),
    );
  }
}
