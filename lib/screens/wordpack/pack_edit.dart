import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import 'pack_detail_screen.dart';

/// 단어 추가/수정 시트 (Figma "수정 모달"). 확인하면 (단어, 뜻).
Future<(String, String)?> showWordEditSheet(
  BuildContext context, {
  required String title,
  required String confirmText,
  String term = '',
  String meaning = '',
}) {
  return showAppSheet<(String, String)>(
    context,
    (_) => _WordEditForm(title: title, confirmText: confirmText, term: term, meaning: meaning),
  );
}

class _WordEditForm extends StatefulWidget {
  const _WordEditForm({required this.title, required this.confirmText, required this.term, required this.meaning});

  final String title;
  final String confirmText;
  final String term;
  final String meaning;

  @override
  State<_WordEditForm> createState() => _WordEditFormState();
}

class _WordEditFormState extends State<_WordEditForm> {
  late final _term = TextEditingController(text: widget.term);
  late final _meaning = TextEditingController(text: widget.meaning);

  bool get _valid => _term.text.trim().isNotEmpty && _meaning.text.trim().isNotEmpty;

  @override
  void dispose() {
    _term.dispose();
    _meaning.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(widget.title, style: AppText.title2),
        Gap.h20,
        _Label('영어'),
        AppTextField(controller: _term, hint: '예) abandon', autofocus: true, onChanged: (_) => setState(() {})),
        const SizedBox(height: 14),
        _Label('뜻'),
        AppTextField(controller: _meaning, hint: '예) 버리다, 포기하다', onChanged: (_) => setState(() {})),
        Gap.h24,
        Row(children: [
          Expanded(child: AppButton('취소', kind: ButtonKind.secondary, onPressed: () => Navigator.pop(context))),
          Gap.w8,
          Expanded(
            child: AppButton(
              widget.confirmText,
              enabled: _valid,
              onPressed: () => Navigator.pop(context, (_term.text.trim(), _meaning.text.trim())),
            ),
          ),
        ]),
      ],
    );
  }
}

class _Label extends StatelessWidget {
  const _Label(this.text);

  final String text;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.only(bottom: 6),
        child: Text(text, style: AppText.caption1Bold.copyWith(color: AppColors.textSecondary)),
      );
}

/// 내 단어팩 상세의 단어별 수정/삭제 버튼.
List<Widget> ownerWordActions(BuildContext context, WordPack pack, Word word) {
  if (!pack.isMine) return const [];
  return [
    RowAction(Icons.edit_outlined, '수정', AppColors.textSecondary, () async {
      final r = await showWordEditSheet(context,
          title: '단어 수정', confirmText: '저장', term: word.term, meaning: word.meaning);
      if (r != null) store.updateWord(word, r.$1, r.$2);
    }),
    RowAction(Icons.delete_outline_rounded, '삭제', AppColors.textSecondary, () async {
      final ok = await showConfirmSheet(context,
          title: "'${word.term}'를 삭제할까요?",
          description: '이 단어의 학습 기록도 함께 삭제돼요.',
          confirmText: '삭제',
          confirmKind: ButtonKind.danger);
      if (ok) store.deleteWord(pack, word);
    }),
  ];
}

Future<void> addWordToPack(BuildContext context, WordPack pack) async {
  final r = await showWordEditSheet(context, title: '단어 추가', confirmText: '추가');
  if (r != null) store.addWord(pack, r.$1, r.$2);
}

Future<void> deletePackWithConfirm(BuildContext context, WordPack pack) async {
  final ok = await showConfirmSheet(context,
      title: "'${pack.title}'을 삭제할까요?",
      description: '단어 ${pack.wordCount}개와 학습 기록이 모두 사라지고 되돌릴 수 없어요.',
      confirmText: '삭제',
      confirmKind: ButtonKind.danger);
  if (!ok || !context.mounted) return;
  Navigator.pop(context);
  store.deletePack(pack);
}

/// 새 단어팩 만들기 (Figma "추가 페이지").
class PackCreateScreen extends StatefulWidget {
  const PackCreateScreen({super.key});

  @override
  State<PackCreateScreen> createState() => _PackCreateScreenState();
}

class _PackCreateScreenState extends State<PackCreateScreen> {
  final _title = TextEditingController();
  final _desc = TextEditingController();
  final _term = TextEditingController();
  final _meaning = TextEditingController();
  final _words = <(String, String)>[];

  @override
  void dispose() {
    for (final c in [_title, _desc, _term, _meaning]) {
      c.dispose();
    }
    super.dispose();
  }

  bool get _canAdd => _term.text.trim().isNotEmpty && _meaning.text.trim().isNotEmpty;

  void _add() {
    if (!_canAdd) return;
    setState(() {
      _words.add((_term.text.trim(), _meaning.text.trim()));
      _term.clear();
      _meaning.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(title: '새 단어팩', onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          const _Label('단어팩 이름'),
          AppTextField(controller: _title, hint: '예) 중간고사 대비', onChanged: (_) => setState(() {})),
          Gap.h16,
          const _Label('설명 (선택)'),
          AppTextField(controller: _desc, hint: '어떤 단어를 모았는지 적어주세요'),
          const SizedBox(height: 28),
          Row(children: [
            Expanded(child: Text('단어 추가', style: AppText.title3)),
            Text('${_words.length}개', style: AppText.label2.copyWith(color: AppColors.textTertiary)),
          ]),
          Gap.h12,
          AppCard(
            child: Column(children: [
              AppTextField(controller: _term, hint: '영어 단어', onChanged: (_) => setState(() {})),
              Gap.h8,
              AppTextField(
                controller: _meaning,
                hint: '뜻',
                onChanged: (_) => setState(() {}),
                onSubmitted: (_) => _add(),
              ),
              Gap.h12,
              AppButton('목록에 추가',
                  kind: ButtonKind.dark, height: 44, leading: Icons.add_rounded, enabled: _canAdd, onPressed: _add),
            ]),
          ),
          Gap.h12,
          for (var i = 0; i < _words.length; i++)
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 4),
              child: Row(children: [
                SizedBox(
                  width: 24,
                  child: Text('${i + 1}', style: AppText.label2.copyWith(color: AppColors.textTertiary)),
                ),
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text(_words[i].$1, style: AppText.body2Bold),
                    Text(_words[i].$2, style: AppText.label2.copyWith(color: AppColors.textSecondary)),
                  ]),
                ),
                RowAction(Icons.close_rounded, '삭제', AppColors.textTertiary, () => setState(() => _words.removeAt(i))),
              ]),
            ),
          Gap.h24,
        ],
      ),
      bottomNavigationBar: BottomCta(
        '만들기',
        enabled: _title.text.trim().isNotEmpty,
        onPressed: () {
          final pack = store.createPack(_title.text, _desc.text, _words);
          replace(context, PackDetailScreen(pack: pack));
        },
      ),
    );
  }
}
