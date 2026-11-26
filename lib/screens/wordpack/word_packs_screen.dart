import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import 'pack_detail_screen.dart';

/// 단어팩 탭 (Figma "단어팩 기본 단어팩", "단어팩 내 단어팩", "검색 결과 없음").
class WordPacksScreen extends StatefulWidget {
  const WordPacksScreen({super.key});

  @override
  State<WordPacksScreen> createState() => _WordPacksScreenState();
}

class _WordPacksScreenState extends State<WordPacksScreen> {
  final _query = TextEditingController();
  int _tab = 0;
  PackCategory _category = PackCategory.all;

  @override
  void dispose() {
    _query.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: LargeTopBar(
        title: '단어팩',
        actions: [AppIconButton(Icons.notifications_rounded, tooltip: '알림', badge: true, onPressed: () {})],
      ),
      body: ListenableBuilder(
        listenable: store,
        builder: (context, _) {
          final q = _query.text.trim().toLowerCase();
          final visible = store.packs
              .where((p) => p.isMine == (_tab == 1))
              .where((p) => _category == PackCategory.all || p.category == _category)
              .where((p) => q.isEmpty || p.title.toLowerCase().contains(q))
              .toList();
          return Column(children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: SegmentedTabs(
                tabs: const ['기본 단어팩', '내 단어팩'],
                index: _tab,
                onSelect: (i) => setState(() => _tab = i),
              ),
            ),
            Gap.h12,
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
              child: SearchField(controller: _query, hint: '단어팩 이름으로 검색', onChanged: (_) => setState(() {})),
            ),
            Gap.h12,
            ChipRow<PackCategory>(
              items: PackCategory.values,
              selected: _category,
              label: (c) => c.label,
              onSelect: (c) => setState(() => _category = c),
            ),
            Expanded(
              child: ListView(
                padding: const EdgeInsets.all(Gap.screen),
                children: [
                  if (_tab == 1) ...[
                    AppButton('새 단어팩', height: 44, leading: Icons.add_rounded, onPressed: () => openCreatePack(context)),
                    Gap.h8,
                  ],
                  if (visible.isEmpty) const EmptyState(icon: Icons.bookmark_rounded, message: '검색 결과가 없어요'),
                  for (final pack in visible) ...[
                    PackCard(pack: pack, onTap: () => push(context, PackDetailScreen(pack: pack))),
                    Gap.h8,
                  ],
                ],
              ),
            ),
          ]);
        },
      ),
    );
  }
}

/// 새 단어팩 만들기 화면으로 이동한다. 내 단어팩 기능 커밋에서 연결한다.
void Function(BuildContext context) openCreatePack = (_) {};

class PackCard extends StatelessWidget {
  const PackCard({super.key, required this.pack, required this.onTap});

  final WordPack pack;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: onTap,
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Tag('${pack.wordCount}단어'),
          if (pack.isCompleted) ...[
            Gap.w4,
            const Tag('완료', background: AppColors.primary, color: AppColors.onPrimary),
          ],
        ]),
        const SizedBox(height: 10),
        Text(pack.title, style: AppText.body1Bold),
        const SizedBox(height: 2),
        Text(pack.description, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        const SizedBox(height: 14),
        Row(children: [
          Expanded(
            child: ProgressBar(value: pack.progress, color: pack.isCompleted ? Palette.lime02 : AppColors.primary),
          ),
          const SizedBox(width: 10),
          Text('${(pack.progress * 100).round()}%', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        ]),
      ]),
    );
  }
}
