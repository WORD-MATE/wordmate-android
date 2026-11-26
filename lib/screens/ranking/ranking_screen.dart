import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/format.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';

/// 랭킹 (Figma "랭킹 페이지").
class RankingScreen extends StatefulWidget {
  const RankingScreen({super.key});

  @override
  State<RankingScreen> createState() => _RankingScreenState();
}

class _RankingScreenState extends State<RankingScreen> {
  RankingScope _scope = RankingScope.school;
  RankingPeriod _period = RankingPeriod.weekly;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: LargeTopBar(
        title: '랭킹',
        actions: [AppIconButton(Icons.notifications_rounded, tooltip: '알림', badge: true, onPressed: () {})],
      ),
      body: ListenableBuilder(
        listenable: store,
        builder: (context, _) {
          final entries = store.ranking(_scope, _period);
          final me = entries.where((e) => e.isMe).firstOrNull;
          return Column(children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: SegmentedTabs(
                tabs: [for (final s in RankingScope.values) s.label],
                index: _scope.index,
                onSelect: (i) => setState(() => _scope = RankingScope.values[i]),
              ),
            ),
            Gap.h8,
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: Row(children: [
                for (final p in RankingPeriod.values) ...[
                  AppChip(p.label, selected: p == _period, onTap: () => setState(() => _period = p)),
                  const SizedBox(width: 6),
                ],
              ]),
            ),
            Expanded(
              child: ListView(children: [
                _PodiumCard(top: entries.take(3).toList()),
                for (final e in entries.skip(3)) _RankRow(entry: e),
                Gap.h12,
              ]),
            ),
            if (me != null)
              Padding(
                padding: const EdgeInsets.fromLTRB(12, 8, 12, 8),
                child: _RankRow(entry: me, pinned: true),
              ),
          ]);
        },
      ),
    );
  }
}

class _PodiumCard extends StatelessWidget {
  const _PodiumCard({required this.top});

  final List<RankingEntry> top;

  @override
  Widget build(BuildContext context) {
    final order = [if (top.length > 1) top[1], top[0], if (top.length > 2) top[2]];
    return Container(
      margin: const EdgeInsets.all(Gap.screen),
      padding: const EdgeInsets.fromLTRB(16, 20, 16, 20),
      decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.lg),
      child: Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
        for (final e in order)
          Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4),
              child: Column(children: [
                Avatar(size: e.rank == 1 ? 48 : 40, background: AppColors.background),
                const SizedBox(height: 6),
                Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                  Flexible(child: Text(e.name, style: AppText.label2Bold, overflow: TextOverflow.ellipsis)),
                  if (e.rank == 1) const Icon(Icons.local_fire_department_rounded, size: 14, color: Palette.orange06),
                ]),
                Text(e.schoolInfo, style: AppText.caption2.copyWith(color: AppColors.textTertiary)),
                Text('${formatNumber(e.xp)} XP', style: AppText.caption1Bold.copyWith(color: AppColors.textSecondary)),
                const SizedBox(height: 6),
                Container(
                  height: switch (e.rank) { 1 => 88.0, 2 => 52.0, _ => 36.0 },
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: e.rank == 1 ? AppColors.primary : Palette.gray06,
                    borderRadius: AppRadius.sm,
                  ),
                  child: Text('${e.rank}',
                      style: AppText.headline2
                          .copyWith(color: e.rank == 1 ? AppColors.onPrimary : AppColors.textSecondary)),
                ),
              ]),
            ),
          ),
      ]),
    );
  }
}

class _RankRow extends StatelessWidget {
  const _RankRow({required this.entry, this.pinned = false});

  final RankingEntry entry;
  final bool pinned;

  @override
  Widget build(BuildContext context) {
    final e = entry;
    final (icon, color) = e.delta > 0
        ? (Icons.arrow_drop_up_rounded, AppColors.info)
        : e.delta < 0
            ? (Icons.arrow_drop_down_rounded, AppColors.error)
            : (Icons.remove_rounded, AppColors.textDisabled);
    return Container(
      margin: EdgeInsets.symmetric(horizontal: pinned ? 0 : Gap.screen),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(color: pinned ? Palette.gray06 : null, borderRadius: AppRadius.md),
      child: Row(children: [
        SizedBox(width: 32, child: Text('${e.rank}', style: AppText.label1Bold)),
        Avatar(size: 36, background: pinned ? AppColors.background : Palette.gray06),
        const SizedBox(width: 10),
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(children: [
              Text(e.name, style: AppText.label1Bold),
              if (e.isMe) ...[
                Gap.w4,
                const Tag('나', background: AppColors.primary, color: AppColors.onPrimary),
              ],
            ]),
            Text(e.schoolInfo, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
        ),
        Icon(icon, size: 18, color: color),
        if (e.delta != 0) Text('${e.delta.abs()}', style: AppText.caption1Bold.copyWith(color: color)),
        Gap.w12,
        Text(formatNumber(e.xp), style: AppText.label1Bold),
        Text(' XP', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
      ]),
    );
  }
}
