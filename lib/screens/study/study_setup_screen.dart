import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import 'study_session_screen.dart';

/// 학습 설정 (Figma "학습 설정", "학습 설정 - 출제 옵션").
class StudySetupScreen extends StatefulWidget {
  const StudySetupScreen({super.key, required this.pack});

  final WordPack pack;

  @override
  State<StudySetupScreen> createState() => _StudySetupScreenState();
}

class _StudySetupScreenState extends State<StudySetupScreen> {
  int _step = 0;
  StudyMode _mode = StudyMode.multipleChoice;
  QuizDirection _direction = QuizDirection.enToKo;
  bool _shuffle = true;
  late final List<int> _counts = {10, 20, widget.pack.wordCount}.where((c) => c <= widget.pack.wordCount).toList();
  late int _count = _counts.first;

  void _back() => _step == 1 ? setState(() => _step = 0) : Navigator.pop(context);

  void _next() {
    if (_step == 0 && _mode != StudyMode.flashcard) {
      setState(() => _step = 1);
      return;
    }
    final options = StudyOptions(
      mode: _mode,
      direction: _direction,
      questionCount: _step == 0 ? widget.pack.wordCount : _count,
      shuffle: _shuffle,
    );
    replace(context, StudySessionScreen(pack: widget.pack, options: options));
  }

  @override
  Widget build(BuildContext context) {
    final pack = widget.pack;
    return PopScope(
      canPop: _step == 0,
      onPopInvokedWithResult: (didPop, _) {
        if (!didPop) setState(() => _step = 0);
      },
      child: Scaffold(
        appBar: AppTopBar(title: '학습 설정', onBack: _back),
        body: ListView(
          padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
          children: [
            Text('${pack.title} · ${pack.wordCount}단어', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
            Gap.h20,
            if (_step == 0) ...[
              const _SectionLabel('학습 방식'),
              for (final m in StudyMode.values) ...[
                _ModeCard(mode: m, selected: m == _mode, onTap: () => setState(() => _mode = m)),
                Gap.h8,
              ],
            ] else ...[
              const _SectionLabel('출제 방향'),
              SegmentedTabs(
                tabs: [for (final d in QuizDirection.values) d.label],
                index: _direction.index,
                onSelect: (i) => setState(() => _direction = QuizDirection.values[i]),
              ),
              Gap.h24,
              const _SectionLabel('문항 수'),
              Wrap(spacing: 6, children: [
                for (final c in _counts)
                  AppChip(c == pack.wordCount ? '전체 $c' : '$c문항',
                      selected: c == _count, onTap: () => setState(() => _count = c)),
              ]),
              Gap.h24,
              Container(
                padding: const EdgeInsets.all(16),
                decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
                child: Row(children: [
                  Expanded(
                    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                      Text('순서 섞기', style: AppText.body2Bold),
                      Text('단어 순서를 무작위로 출제해요',
                          style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                    ]),
                  ),
                  AppSwitch(value: _shuffle, onChanged: (v) => setState(() => _shuffle = v)),
                ]),
              ),
            ],
          ],
        ),
        bottomNavigationBar: BottomCta(_step == 0 ? '다음' : '$_count문항 시작하기', onPressed: _next),
      ),
    );
  }
}

class _SectionLabel extends StatelessWidget {
  const _SectionLabel(this.text);

  final String text;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.only(bottom: 10),
        child: Text(text, style: AppText.label2Bold.copyWith(color: AppColors.textSecondary)),
      );
}

class _ModeCard extends StatelessWidget {
  const _ModeCard({required this.mode, required this.selected, required this.onTap});

  final StudyMode mode;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final (icon, color, bg) = switch (mode) {
      StudyMode.flashcard => (Icons.style_rounded, AppColors.info, const Color(0xFFE6F0FF)),
      StudyMode.multipleChoice => (Icons.gps_fixed_rounded, Palette.red02, const Color(0xFFFFE9EB)),
      StudyMode.typing => (Icons.edit_rounded, Palette.orange06, const Color(0xFFFFF1E0)),
    };
    return Material(
      color: selected ? AppColors.surfaceVariant : AppColors.surface,
      shape: RoundedRectangleBorder(
        borderRadius: AppRadius.lg,
        side: BorderSide(color: selected ? AppColors.primary : Colors.transparent, width: 1.5),
      ),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Expanded(
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Container(
                  width: 36,
                  height: 36,
                  decoration: BoxDecoration(color: bg, borderRadius: AppRadius.sm),
                  child: Icon(icon, color: color, size: 20),
                ),
                const SizedBox(height: 10),
                Text(mode.title, style: AppText.body1Bold),
                const SizedBox(height: 2),
                Text(mode.description, style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
                const SizedBox(height: 6),
                Text(mode.xpHint, style: AppText.caption1Bold.copyWith(color: AppColors.primaryDark)),
              ]),
            ),
            Gap.w12,
            CheckCircle(checked: selected),
          ]),
        ),
      ),
    );
  }
}
