import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/speaker.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import 'pack_edit.dart';
import 'report_sheet.dart';

Color wordStatusColor(WordStatus s) => switch (s) {
      WordStatus.fresh => Palette.gray11,
      WordStatus.learning => Palette.orange06,
      WordStatus.memorized => Palette.lime01,
    };

/// 단어팩 상세 (Figma "단어", "뜻 가리기", "단어팩 신고").
class PackDetailScreen extends StatefulWidget {
  const PackDetailScreen({super.key, required this.pack});

  final WordPack pack;

  /// 학습 설정 화면 열기. 학습 기능 커밋에서 연결한다.
  static void Function(BuildContext context, WordPack pack) openStudy = (_, _) {};

  @override
  State<PackDetailScreen> createState() => _PackDetailScreenState();
}

class _PackDetailScreenState extends State<PackDetailScreen> {
  final _query = TextEditingController();
  WordStatus? _filter;
  bool _hideMeaning = false;

  WordPack get pack => widget.pack;

  @override
  void dispose() {
    _query.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: store,
      builder: (context, _) {
        final q = _query.text.trim().toLowerCase();
        final words = pack.words
            .where((w) => _filter == null || w.status == _filter)
            .where((w) => q.isEmpty || w.term.toLowerCase().contains(q) || w.meaning.contains(q))
            .toList();
        return Scaffold(
          appBar: AppTopBar(
            title: pack.title,
            onBack: () => Navigator.pop(context),
            actions: [
              if (pack.isMine)
                AppIconButton(Icons.delete_outline_rounded,
                    tooltip: '단어팩 삭제', onPressed: () => deletePackWithConfirm(context, pack))
              else
                AppIconButton(Icons.flag_outlined, tooltip: '신고', onPressed: () => showReportPackSheet(context, pack)),
            ],
          ),
          body: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: Text('${pack.sourceLabel} · ${pack.wordCount}단어 · ${pack.description}',
                  style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
            ),
            Gap.h12,
            ChipRow<WordStatus?>(
              items: const [null, ...WordStatus.values],
              selected: _filter,
              label: (s) => s?.label ?? '전체',
              count: (s) => s == null ? pack.wordCount : pack.words.where((w) => w.status == s).length,
              dot: (s) => s == null ? null : wordStatusColor(s),
              onSelect: (s) => setState(() => _filter = s),
            ),
            Gap.h12,
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: Row(children: [
                Expanded(
                  child: SearchField(controller: _query, hint: '단어나 뜻으로 찾기', onChanged: (_) => setState(() {})),
                ),
                Gap.w12,
                Text('뜻 가리기', style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
                Gap.w4,
                AppSwitch(value: _hideMeaning, onChanged: (v) => setState(() => _hideMeaning = v)),
              ]),
            ),
            Expanded(
              child: words.isEmpty
                  ? const EmptyState(icon: Icons.search_off_rounded, message: '해당하는 단어가 없어요')
                  : ListView.separated(
                      padding: const EdgeInsets.only(top: 8),
                      itemCount: words.length,
                      separatorBuilder: (_, _) => const AppDivider(indent: Gap.screen),
                      itemBuilder: (_, i) => WordRow(
                        key: ValueKey(words[i].id),
                        word: words[i],
                        hideMeaning: _hideMeaning,
                        trailing: ownerWordActions(context, pack, words[i]),
                      ),
                    ),
            ),
          ]),
          bottomNavigationBar: SafeArea(
            top: false,
            child: Padding(
              padding: const EdgeInsets.fromLTRB(Gap.screen, 12, Gap.screen, 12),
              child: Row(children: [
                Expanded(
                  child: AppButton('학습 시작',
                      enabled: pack.words.isNotEmpty, onPressed: () => PackDetailScreen.openStudy(context, pack)),
                ),
                if (pack.isMine) ...[
                  Gap.w8,
                  Expanded(
                    child: AppButton('단어 추가',
                        kind: ButtonKind.outline, leading: Icons.add_rounded, onPressed: () => addWordToPack(context, pack)),
                  ),
                ],
              ]),
            ),
          ),
        );
      },
    );
  }
}

/// 단어 한 줄: 상태 점, 단어/뜻, 발음, 즐겨찾기.
class WordRow extends StatefulWidget {
  const WordRow({super.key, required this.word, required this.hideMeaning, this.trailing = const []});

  final Word word;
  final bool hideMeaning;
  final List<Widget> trailing;

  @override
  State<WordRow> createState() => _WordRowState();
}

class _WordRowState extends State<WordRow> {
  bool _revealed = false;

  @override
  void didUpdateWidget(covariant WordRow old) {
    super.didUpdateWidget(old);
    if (old.hideMeaning != widget.hideMeaning) _revealed = false;
  }

  @override
  Widget build(BuildContext context) {
    final w = widget.word;
    final hidden = widget.hideMeaning && !_revealed;
    return InkWell(
      onTap: widget.hideMeaning ? () => setState(() => _revealed = !_revealed) : null,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(Gap.screen, 12, 12, 12),
        child: Row(children: [
          Container(
            width: 6,
            height: 6,
            decoration: BoxDecoration(color: wordStatusColor(w.status), shape: BoxShape.circle),
          ),
          Gap.w12,
          Expanded(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text(w.term, style: AppText.body1Bold),
              const SizedBox(height: 2),
              hidden
                  ? Container(
                      width: 96,
                      height: 16,
                      decoration: const BoxDecoration(color: AppColors.surfaceVariant, borderRadius: AppRadius.xs),
                    )
                  : Text(w.meaning, style: AppText.label2.copyWith(color: AppColors.textSecondary)),
            ]),
          ),
          RowAction(Icons.volume_up_rounded, '발음 듣기', AppColors.textSecondary, () => Speaker.speak(w.term)),
          RowAction(
            w.starred ? Icons.star_rounded : Icons.star_border_rounded,
            '즐겨찾기',
            w.starred ? Palette.yellow02 : AppColors.textTertiary,
            () => store.toggleStar(w),
          ),
          ...widget.trailing,
        ]),
      ),
    );
  }
}

class RowAction extends StatelessWidget {
  const RowAction(this.icon, this.tooltip, this.color, this.onTap, {super.key});

  final IconData icon;
  final String tooltip;
  final Color color;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return IconButton(
      onPressed: onTap,
      tooltip: tooltip,
      icon: Icon(icon, color: color, size: 20),
      visualDensity: VisualDensity.compact,
    );
  }
}
