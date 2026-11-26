import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../battle/battle_lobby_screen.dart';

/// 마이페이지 메뉴의 설정·공지·고객센터 이동. 해당 화면 커밋에서 연결한다.
void Function(BuildContext context) openSettings = (_) {};
void Function(BuildContext context) openNotice = (_) {};
void Function(BuildContext context) openInquiry = (_) {};

/// 마이페이지 (Figma "마이페이지", "학습", "활동").
class MyPageScreen extends StatelessWidget {
  const MyPageScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: LargeTopBar(
        title: '마이페이지',
        actions: [AppIconButton(Icons.notifications_rounded, tooltip: '알림', badge: true, onPressed: () {})],
      ),
      body: ListenableBuilder(
        listenable: store,
        builder: (context, _) => ListView(
          padding: const EdgeInsets.only(bottom: 24),
          children: [
            _ProfileCard(user: store.user),
            SectionHeader('랭킹 배지',
                top: 0, action: '전체 보기', onAction: () => push(context, const BadgesScreen())),
            AppCard(
              margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
              padding: const EdgeInsets.all(4),
              child: Column(children: [for (final b in store.badges.take(3)) BadgeRow(badge: b)]),
            ),
            Gap.h16,
            AppCard(
              margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
              padding: EdgeInsets.zero,
              child: Column(children: [
                MenuRow('학습',
                    icon: Icons.bar_chart_rounded,
                    trailingText: '학습 기록 · 오답노트',
                    onTap: () => mainTab.value = Tabs.wordPacks),
                MenuRow('활동',
                    icon: Icons.star_rounded,
                    trailingText: '대결 기록 · 배지',
                    onTap: () => push(context, const BattleHistoryScreen())),
                MenuRow('설정',
                    icon: Icons.settings_rounded, trailingText: '알림 · 계정', onTap: () => openSettings(context)),
                MenuRow('공지사항', icon: Icons.campaign_rounded, onTap: () => openNotice(context)),
                MenuRow('고객센터',
                    icon: Icons.help_outline_rounded, trailingText: '문의하기', onTap: () => openInquiry(context)),
              ]),
            ),
          ],
        ),
      ),
    );
  }
}

class _ProfileCard extends StatelessWidget {
  const _ProfileCard({required this.user});

  final User user;

  @override
  Widget build(BuildContext context) {
    Widget stat(String label, String value, String unit) => Expanded(
          child: Container(
            padding: const EdgeInsets.all(12),
            decoration: const BoxDecoration(color: AppColors.background, borderRadius: AppRadius.md),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(label, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
              ValueUnit(value, unit),
            ]),
          ),
        );
    return AppCard(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Stack(clipBehavior: Clip.none, children: [
            const Avatar(size: 56, background: AppColors.background),
            Positioned(
              right: -4,
              bottom: -4,
              child: Container(
                width: 20,
                height: 20,
                decoration: BoxDecoration(
                  color: AppColors.black,
                  shape: BoxShape.circle,
                  border: Border.all(color: AppColors.surface, width: 2),
                ),
                child: const Icon(Icons.edit_rounded, size: 11, color: Palette.gray01),
              ),
            ),
          ]),
          const SizedBox(width: 14),
          Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(user.name, style: AppText.title1),
            Text(user.schoolInfo, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
            Gap.h4,
            Tag('🔥 ${user.title}', background: AppColors.background),
          ]),
        ]),
        Gap.h16,
        Row(children: [
          Text('Lv.${user.level}', style: AppText.body1Bold),
          const SizedBox(width: 6),
          Tag(user.tier, background: Palette.gray06),
          const Spacer(),
          Text('XP ${user.xp}/${user.xpToNextLevel}', style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
        ]),
        Gap.h8,
        ProgressBar(value: user.xp / user.xpToNextLevel),
        const SizedBox(height: 6),
        Row(children: [
          Expanded(child: Text('누적 XP 기준', style: AppText.caption2.copyWith(color: AppColors.textTertiary))),
          Text('다음 레벨까지 ${(user.xpToNextLevel - user.xp).clamp(0, user.xpToNextLevel)} XP',
              style: AppText.caption2.copyWith(color: AppColors.textTertiary)),
        ]),
        const SizedBox(height: 14),
        Row(children: [
          stat('연속 학습', '${user.streakDays}', '일'),
          Gap.w8,
          stat('맞힌 단어', '${user.totalCorrectWords}', '개'),
          Gap.w8,
          stat('대결 승리', '${user.battleWins}', '회'),
        ]),
      ]),
    );
  }
}

Color medalColor(Medal m) => switch (m) {
      Medal.gold => AppColors.gold,
      Medal.silver => AppColors.silver,
      Medal.bronze => AppColors.bronze,
    };

class BadgeRow extends StatelessWidget {
  const BadgeRow({super.key, required this.badge});

  final RankBadge badge;

  @override
  Widget build(BuildContext context) {
    final color = medalColor(badge.medal);
    return Padding(
      padding: const EdgeInsets.all(12),
      child: Row(children: [
        Container(
          width: 36,
          height: 36,
          decoration: BoxDecoration(color: color.withValues(alpha: 0.2), shape: BoxShape.circle),
          child: Icon(Icons.military_tech_rounded, color: color, size: 22),
        ),
        Gap.w12,
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(badge.title, style: AppText.label1Bold),
            Text(badge.dateLabel, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
        ),
        if (badge.isNew) const Tag('NEW', background: AppColors.primary, color: AppColors.onPrimary),
      ]),
    );
  }
}

/// 랭킹 배지 전체 (Figma "배지").
class BadgesScreen extends StatefulWidget {
  const BadgesScreen({super.key});

  @override
  State<BadgesScreen> createState() => _BadgesScreenState();
}

class _BadgesScreenState extends State<BadgesScreen> {
  int _tab = 0;

  @override
  Widget build(BuildContext context) {
    final badges = store.badges;
    final visible = switch (_tab) {
      1 => badges.where((b) => b.title.contains('주차')).toList(),
      2 => badges.where((b) => b.title.contains('월간')).toList(),
      _ => badges,
    };
    return Scaffold(
      appBar: AppTopBar(title: '랭킹 배지', onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          AppCard(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text('보유 배지 ${badges.length}개', style: AppText.title2),
              const SizedBox(height: 10),
              Wrap(spacing: 6, children: [
                for (final (m, label) in const [(Medal.gold, '금'), (Medal.silver, '은'), (Medal.bronze, '동')])
                  Tag('$label ${badges.where((b) => b.medal == m).length}',
                      background: medalColor(m).withValues(alpha: 0.2), color: AppColors.textPrimary),
              ]),
              const SizedBox(height: 10),
              Text('주간·월간 랭킹 상위권에 들면 배지를 받아요. 배지는 마이페이지와 랭킹에 표시돼요.',
                  style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
            ]),
          ),
          Gap.h16,
          SegmentedTabs(tabs: const ['전체', '주간', '월간'], index: _tab, onSelect: (i) => setState(() => _tab = i)),
          Gap.h8,
          for (final b in visible) ...[BadgeRow(badge: b), const AppDivider()],
        ],
      ),
    );
  }
}
