import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import 'study_session_screen.dart';

String resultHeadline(int accuracy) => switch (accuracy) {
      100 => '완벽해요!',
      >= 80 => '잘했어요!',
      >= 50 => '조금만 더 하면 돼요',
      _ => '다시 한 번 도전해봐요',
    };

/// 학습 결과 (Figma "종료").
class StudyResultScreen extends StatefulWidget {
  const StudyResultScreen({super.key, required this.result, required this.pack});

  final StudyResult result;
  final WordPack pack;

  @override
  State<StudyResultScreen> createState() => _StudyResultScreenState();
}

class _StudyResultScreenState extends State<StudyResultScreen> {
  bool _onlyWrong = false;

  void _retry({required bool onlyWrong}) =>
      replace(context, StudySessionScreen(pack: widget.pack, options: widget.result.options, onlyWrong: onlyWrong));

  @override
  Widget build(BuildContext context) {
    final r = widget.result;
    final list = _onlyWrong ? r.answers.where((a) => !a.correct).toList() : r.answers;
    return Scaffold(
      appBar: AppTopBar(onClose: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.only(bottom: 24),
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text('${r.packTitle} · ${r.options.mode.title}',
                  style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
              const SizedBox(height: 6),
              Text(resultHeadline(r.accuracy), style: AppText.headline1),
              Text.rich(TextSpan(children: [
                TextSpan(text: '${r.accuracy}', style: AppText.display),
                TextSpan(text: '%', style: AppText.title1),
              ])),
              Gap.h16,
              Container(
                padding: const EdgeInsets.all(16),
                decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
                child: Row(children: [
                  _Stat('맞음', '${r.correctCount}', AppColors.textPrimary),
                  _Stat('틀림', '${r.wrongCount}', AppColors.error),
                  _Stat('획득 XP', '+${r.earnedXp}', AppColors.textPrimary),
                ]),
              ),
              Gap.h16,
              if (r.wrongCount > 0) ...[
                AppButton('틀린 단어만 다시 풀기', onPressed: () => _retry(onlyWrong: true)),
                Gap.h8,
              ],
              AppButton('다시 풀기', kind: ButtonKind.secondary, onPressed: () => _retry(onlyWrong: false)),
              Center(
                child: TextButton(
                  onPressed: () => goToTab(context, Tabs.home),
                  child: Text('홈으로', style: AppText.label1.copyWith(color: AppColors.textSecondary)),
                ),
              ),
              Gap.h8,
              Row(children: [
                Expanded(child: Text('푼 단어 ${r.answers.length}', style: AppText.body2Bold)),
                Text('틀린 것만', style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
                Gap.w4,
                AppSwitch(value: _onlyWrong, onChanged: (v) => setState(() => _onlyWrong = v)),
              ]),
            ]),
          ),
          for (final a in list) ...[
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 12),
              child: Row(children: [
                ResultMark(correct: a.correct),
                Gap.w12,
                Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Text(a.word.term, style: AppText.body2Bold),
                  Text(a.word.meaning, style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
                ]),
              ]),
            ),
            const AppDivider(indent: Gap.screen),
          ],
        ],
      ),
    );
  }
}

class _Stat extends StatelessWidget {
  const _Stat(this.label, this.value, this.color);

  final String label;
  final String value;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text(label, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        Gap.h4,
        Text(value, style: AppText.title1.copyWith(color: color)),
      ]),
    );
  }
}
