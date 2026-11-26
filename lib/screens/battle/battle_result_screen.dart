import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/format.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import '../study/study_session_screen.dart';
import '../wordpack/pack_detail_screen.dart';
import '../wordpack/report_sheet.dart';

/// 대결 결과 (Figma "결과", "신고").
class BattleResultScreen extends StatelessWidget {
  const BattleResultScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final r = store.lastBattleResult!;
    return Scaffold(
      appBar: AppTopBar(title: '대결 결과', onClose: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 24),
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 8),
            child: Text(r.myRank == 1 ? '1위! 축하해요' : '${r.myRank}위! 수고했어요', style: AppText.headline1),
          ),
          _Podium(top: r.standings.take(3).toList()),
          Container(
            margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
            padding: const EdgeInsets.all(16),
            decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
            child: Row(children: [
              _Stat('내 점수', formatNumber(r.myScore), ''),
              _Stat('정답', '${r.correctCount}', '개'),
              _Stat('획득 XP', '+${r.earnedXp}', ''),
            ]),
          ),
          Center(
            child: TextButton.icon(
              onPressed: () async {
                await Clipboard.setData(ClipboardData(
                    text: "WORD MATE '${r.packTitle}' 대결에서 ${r.myRank}위! (${formatNumber(r.myScore)}점)"));
                if (context.mounted) showToast(context, '결과를 복사했어요. 친구에게 붙여넣어 공유해 보세요');
              },
              icon: const Icon(Icons.ios_share_rounded, size: 16, color: AppColors.textSecondary),
              label: Text('결과 공유하기', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 8),
            child: Text('전체 순위', style: AppText.body2Bold),
          ),
          for (final s in r.standings) _StandingRow(standing: s),
          Gap.h12,
          AppCard(
            margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
            radius: AppRadius.md,
            onTap: r.wrongWords.isEmpty ? null : () => push(context, BattleWrongWordsScreen(result: r)),
            child: Row(children: [
              const ResultMark(correct: false),
              const SizedBox(width: 10),
              Expanded(child: Text('틀린 단어', style: AppText.label1Bold)),
              Text('${r.wrongWords.length}개', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
              const Icon(Icons.chevron_right_rounded, color: AppColors.textTertiary),
            ]),
          ),
        ],
      ),
    );
  }
}

class _Podium extends StatelessWidget {
  const _Podium({required this.top});

  final List<BattleStanding> top;

  @override
  Widget build(BuildContext context) {
    // 2위 - 1위 - 3위 순서로 배치
    final order = [if (top.length > 1) top[1], top[0], if (top.length > 2) top[2]];
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
      child: Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
        for (final s in order)
          Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4),
              child: Column(children: [
                Avatar(size: s.rank == 1 ? 48 : 40),
                const SizedBox(height: 6),
                Text(s.isMe ? '${s.name} (나)' : s.name, style: AppText.label2Bold),
                Text(formatNumber(s.score), style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                const SizedBox(height: 6),
                Container(
                  height: switch (s.rank) { 1 => 88.0, 2 => 56.0, _ => 40.0 },
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: s.rank == 1 ? AppColors.primary : AppColors.surfaceVariant,
                    borderRadius: AppRadius.sm,
                  ),
                  child: Text('${s.rank}',
                      style: AppText.headline2
                          .copyWith(color: s.rank == 1 ? AppColors.onPrimary : AppColors.textSecondary)),
                ),
              ]),
            ),
          ),
      ]),
    );
  }
}

class _Stat extends StatelessWidget {
  const _Stat(this.label, this.value, this.unit);

  final String label;
  final String value;
  final String unit;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(label, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        unit.isEmpty ? Text(value, style: AppText.title1) : ValueUnit(value, unit, style: AppText.title1),
      ]),
    );
  }
}

class _StandingRow extends StatelessWidget {
  const _StandingRow({required this.standing});

  final BattleStanding standing;

  @override
  Widget build(BuildContext context) {
    final s = standing;
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 2),
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(color: s.isMe ? Palette.gray06 : null, borderRadius: AppRadius.sm),
      child: Row(children: [
        SizedBox(width: 24, child: Text('${s.rank}', style: AppText.label1Bold)),
        Avatar(size: 32, background: s.isMe ? AppColors.background : Palette.gray06),
        const SizedBox(width: 10),
        Expanded(
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(s.isMe ? '${s.name} (나)' : s.name, style: AppText.label1Bold),
            Text(s.detail, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
        ),
        Text(formatNumber(s.score), style: AppText.label1Bold),
        if (s.isMe)
          const SizedBox(width: 40)
        else
          IconButton(
            tooltip: '신고',
            visualDensity: VisualDensity.compact,
            icon: const Icon(Icons.flag_outlined, size: 18, color: AppColors.textTertiary),
            onPressed: () => showReportSheet(
              context,
              title: '${s.name}님을 신고할까요?',
              description: '신고 내용은 검토 후 처리되며, 신고한 사실은 상대에게 알려지지 않아요.',
              reasons: const ['부적절한 닉네임', '불쾌한 반응 반복', '부정행위가 의심돼요', '기타'],
            ),
          ),
      ]),
    );
  }
}

/// 대결에서 틀린 단어 (Figma "틀린 단어").
class BattleWrongWordsScreen extends StatelessWidget {
  const BattleWrongWordsScreen({super.key, required this.result});

  final BattleResult result;

  @override
  Widget build(BuildContext context) {
    final pack = store.pack(result.packId);
    return Scaffold(
      appBar: AppTopBar(title: '틀린 단어', onBack: () => Navigator.pop(context)),
      body: ListView(children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 8),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text('틀린 단어 ${result.wrongWords.length}개', style: AppText.headline2),
            Text('틀린 단어는 오답노트에 자동으로 담겨요', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
          ]),
        ),
        const AppDivider(),
        for (final w in result.wrongWords) ...[
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 14),
            child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Padding(padding: EdgeInsets.only(top: 2), child: ResultMark(correct: false, size: 20)),
              Gap.w12,
              Expanded(
                child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(w.term, style: AppText.body1Bold),
                  Text(w.meaning, style: AppText.label2.copyWith(color: AppColors.textSecondary)),
                  if (w.example.isNotEmpty) ...[
                    Gap.h4,
                    Text(w.example, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                  ],
                ]),
              ),
            ]),
          ),
          const AppDivider(indent: Gap.screen),
        ],
      ]),
      bottomNavigationBar: BottomCta(
        '오답노트로 가기',
        onPressed: pack == null ? () => Navigator.pop(context) : () => replace(context, PackDetailScreen(pack: pack)),
      ),
    );
  }
}
