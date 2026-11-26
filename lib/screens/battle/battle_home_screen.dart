import 'dart:async';

import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/format.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import 'battle_create_screen.dart';
import 'battle_lobby_screen.dart';

/// 대결 탭 (Figma "대결", "빠른대결").
class BattleHomeScreen extends StatelessWidget {
  const BattleHomeScreen({super.key});

  Future<void> _quickMatch(BuildContext context) async {
    final matched = await showAppSheet<bool>(context, (_) => const _QuickMatch());
    if (matched == true && context.mounted) {
      store.createRoom(BattleSettings(pack: store.packs.first));
      push(context, const BattleLobbyScreen());
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: LargeTopBar(
        title: '대결',
        actions: [AppIconButton(Icons.notifications_rounded, tooltip: '알림', badge: true, onPressed: () {})],
      ),
      body: ListenableBuilder(
        listenable: store,
        builder: (context, _) => ListView(
          padding: const EdgeInsets.only(bottom: 24),
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: Column(children: [
                _EntryCard(
                  title: '빠른 대결',
                  description: '비슷한 실력의 친구와 바로 대결해요',
                  icon: Icons.bolt_rounded,
                  color: AppColors.primary,
                  iconBackground: AppColors.black,
                  iconColor: AppColors.primary,
                  onTap: () => _quickMatch(context),
                ),
                Gap.h8,
                _EntryCard(
                  title: '대결방 만들기',
                  description: '단어팩을 골라 친구들을 초대해요',
                  icon: Icons.add_rounded,
                  onTap: () => push(context, const BattleCreateScreen()),
                ),
                Gap.h8,
                _EntryCard(
                  title: '코드로 참가',
                  description: '친구에게 받은 6자리 코드를 입력해요',
                  icon: Icons.groups_rounded,
                  onTap: () => push(context, const BattleCodeScreen()),
                ),
              ]),
            ),
            SectionHeader('내 대결 기록',
                action: '전체 보기', onAction: () => push(context, const BattleHistoryScreen())),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: BattleStatsCard(records: store.battleRecords),
            ),
            const SectionHeader('최근 대결'),
            for (final r in store.battleRecords.take(2))
              Padding(
                padding: const EdgeInsets.fromLTRB(Gap.screen, 0, Gap.screen, 8),
                child: BattleRecordRow(record: r),
              ),
          ],
        ),
      ),
    );
  }
}

class _EntryCard extends StatelessWidget {
  const _EntryCard({
    required this.title,
    required this.description,
    required this.icon,
    required this.onTap,
    this.color = AppColors.surface,
    this.iconBackground = AppColors.background,
    this.iconColor = AppColors.textPrimary,
  });

  final String title;
  final String description;
  final IconData icon;
  final VoidCallback onTap;
  final Color color;
  final Color iconBackground;
  final Color iconColor;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      color: color,
      onTap: onTap,
      child: Row(children: [
        Container(
          width: 40,
          height: 40,
          decoration: BoxDecoration(color: iconBackground, borderRadius: AppRadius.md),
          child: Icon(icon, color: iconColor, size: 22),
        ),
        Gap.w12,
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(title, style: AppText.body1Bold),
            Text(description, style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
          ]),
        ),
        const Icon(Icons.chevron_right_rounded, color: AppColors.textSecondary),
      ]),
    );
  }
}

class BattleStatsCard extends StatelessWidget {
  const BattleStatsCard({super.key, required this.records});

  final List<BattleRecord> records;

  @override
  Widget build(BuildContext context) {
    final wins = records.where((r) => r.rank == 1).length;
    final rate = records.isEmpty ? 0 : wins * 100 ~/ records.length;
    Widget stat(String label, String value, String unit) => Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(label, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
            Gap.h4,
            ValueUnit(value, unit, style: AppText.title1),
          ]),
        );
    return AppCard(
      child: Row(children: [
        stat('대결', '${records.length}', '회'),
        stat('승률', '$rate', '%'),
        stat('평균 정답률', '78', '%'),
      ]),
    );
  }
}

class BattleRecordRow extends StatelessWidget {
  const BattleRecordRow({super.key, required this.record});

  final BattleRecord record;

  @override
  Widget build(BuildContext context) {
    final (bg, fg) = switch (record.rank) {
      1 => (AppColors.primary, AppColors.onPrimary),
      2 => (Palette.gray06, AppColors.textPrimary),
      3 => (const Color(0xFFFFE6CC), Palette.orange07),
      _ => (AppColors.surfaceVariant, AppColors.textSecondary),
    };
    return AppCard(
      radius: AppRadius.md,
      padding: const EdgeInsets.all(14),
      child: Row(children: [
        Container(
          width: 36,
          height: 36,
          alignment: Alignment.center,
          decoration: BoxDecoration(color: bg, borderRadius: AppRadius.sm),
          child: Text('${record.rank}위', style: AppText.caption1Bold.copyWith(color: fg)),
        ),
        Gap.w12,
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(record.packTitle, style: AppText.label1Bold, maxLines: 1, overflow: TextOverflow.ellipsis),
            Text(record.dateLabel, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
        ),
        Text('${formatNumber(record.score)}점', style: AppText.label1Bold),
      ]),
    );
  }
}

/// 빠른 대결 매칭 중 시트. 3초 뒤 매칭되면 true 로 닫힌다.
class _QuickMatch extends StatefulWidget {
  const _QuickMatch();

  @override
  State<_QuickMatch> createState() => _QuickMatchState();
}

class _QuickMatchState extends State<_QuickMatch> {
  int _seconds = 0;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _timer = Timer.periodic(const Duration(seconds: 1), (t) {
      setState(() => _seconds++);
      if (_seconds >= 3) {
        t.cancel();
        Navigator.pop(context, true);
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(mainAxisSize: MainAxisSize.min, children: [
      Text('상대를 찾고 있어요', style: AppText.title2),
      Gap.h20,
      Text('$_seconds', style: AppText.score),
      Gap.h8,
      const Tag('비슷한 레벨의 친구를 찾는 중 · 평균 10초'),
      Gap.h24,
      AppButton('매칭 취소', kind: ButtonKind.secondary, onPressed: () => Navigator.pop(context, false)),
    ]);
  }
}
