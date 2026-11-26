import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import 'battle_lobby_screen.dart';

/// 대결방 만들기 1단계: 단어팩 선택 (Figma "만들기", "다른 단어팩 고르기").
class BattleCreateScreen extends StatefulWidget {
  const BattleCreateScreen({super.key});

  @override
  State<BattleCreateScreen> createState() => _BattleCreateScreenState();
}

class _BattleCreateScreenState extends State<BattleCreateScreen> {
  late WordPack? _selected = store.packs.firstOrNull;

  Future<void> _pickOther() async {
    final picked = await showAppSheet<WordPack>(context, (_) => _PackPicker(selected: _selected));
    if (picked != null) setState(() => _selected = picked);
  }

  @override
  Widget build(BuildContext context) {
    final featured = {...store.packs.take(4), ?_selected}.toList();
    return Scaffold(
      appBar: AppTopBar(title: '대결방 만들기', onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          Row(children: [
            Expanded(child: Text('단어팩', style: AppText.label2Bold.copyWith(color: AppColors.textSecondary))),
            Text('대결에서 출제할 단어팩을 골라주세요', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
          const SizedBox(height: 10),
          for (final p in featured) ...[
            PackRadioRow(pack: p, selected: p == _selected, onTap: () => setState(() => _selected = p)),
            Gap.h8,
          ],
          AppButton('다른 단어팩 고르기 ${store.packs.length}',
              kind: ButtonKind.secondary, height: 44, onPressed: _pickOther),
        ],
      ),
      bottomNavigationBar: BottomCta(
        '다음',
        enabled: _selected != null,
        onPressed: () => push(context, BattleSettingsScreen(pack: _selected!)),
      ),
    );
  }
}

class PackRadioRow extends StatelessWidget {
  const PackRadioRow({super.key, required this.pack, required this.selected, required this.onTap});

  final WordPack pack;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: selected ? AppColors.surfacePressed : AppColors.surface,
      shape: RoundedRectangleBorder(
        borderRadius: AppRadius.md,
        side: BorderSide(color: selected ? AppColors.primary : Colors.transparent, width: 1.5),
      ),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Row(children: [
            Expanded(
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Text(pack.title, style: AppText.label1Bold),
                Text('${pack.sourceLabel} · ${pack.wordCount}단어',
                    style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
              ]),
            ),
            CheckCircle(checked: selected),
          ]),
        ),
      ),
    );
  }
}

class _PackPicker extends StatefulWidget {
  const _PackPicker({required this.selected});

  final WordPack? selected;

  @override
  State<_PackPicker> createState() => _PackPickerState();
}

class _PackPickerState extends State<_PackPicker> {
  final _query = TextEditingController();
  int _filter = 0;

  @override
  void dispose() {
    _query.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final q = _query.text.trim().toLowerCase();
    final visible = store.packs
        .where((p) => _filter == 0 || p.isMine == (_filter == 2))
        .where((p) => q.isEmpty || p.title.toLowerCase().contains(q))
        .toList();
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('단어팩 고르기', style: AppText.title2),
        Gap.h12,
        SearchField(controller: _query, hint: '단어팩 이름으로 검색', onChanged: (_) => setState(() {})),
        const SizedBox(height: 10),
        Wrap(spacing: 6, children: [
          for (final (i, label) in const ['전체', '기본 단어팩', '내 단어팩'].indexed)
            AppChip(label, selected: i == _filter, onTap: () => setState(() => _filter = i)),
        ]),
        const SizedBox(height: 10),
        ConstrainedBox(
          constraints: const BoxConstraints(maxHeight: 360),
          child: ListView.separated(
            shrinkWrap: true,
            itemCount: visible.length,
            separatorBuilder: (_, _) => Gap.h8,
            itemBuilder: (_, i) => PackRadioRow(
              pack: visible[i],
              selected: visible[i] == widget.selected,
              onTap: () => Navigator.pop(context, visible[i]),
            ),
          ),
        ),
      ],
    );
  }
}

/// 대결방 만들기 2단계: 대결 설정 (Figma "만들기 - 대결 설정").
class BattleSettingsScreen extends StatefulWidget {
  const BattleSettingsScreen({super.key, required this.pack});

  final WordPack pack;

  @override
  State<BattleSettingsScreen> createState() => _BattleSettingsScreenState();
}

class _BattleSettingsScreenState extends State<BattleSettingsScreen> {
  BattleMode _mode = BattleMode.speed;
  int _count = 10;
  int _seconds = 10;
  double _players = 8;

  @override
  Widget build(BuildContext context) {
    Widget label(String t) => Padding(
          padding: const EdgeInsets.only(bottom: 10),
          child: Text(t, style: AppText.label2Bold.copyWith(color: AppColors.textSecondary)),
        );
    return Scaffold(
      appBar: AppTopBar(title: '대결방 만들기', onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          Text('${widget.pack.title} · ${widget.pack.wordCount}단어',
              style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          Gap.h20,
          label('점수 방식'),
          SegmentedTabs(
            tabs: [for (final m in BattleMode.values) m.label],
            index: _mode.index,
            onSelect: (i) => setState(() => _mode = BattleMode.values[i]),
          ),
          Gap.h24,
          label('문항 수'),
          Wrap(spacing: 6, children: [
            for (final c in const [10, 20, 30])
              AppChip('$c문항', selected: c == _count, onTap: () => setState(() => _count = c)),
          ]),
          Gap.h24,
          label('문항당 시간'),
          Wrap(spacing: 6, children: [
            for (final s in const [5, 10, 15])
              AppChip('$s초', selected: s == _seconds, onTap: () => setState(() => _seconds = s)),
          ]),
          Gap.h24,
          Container(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 4),
            decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
            child: Column(children: [
              Row(children: [
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text('최대 인원', style: AppText.body2Bold),
                    Text('2명부터 30명까지 참여할 수 있어요',
                        style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                  ]),
                ),
                Text('${_players.round()}명', style: AppText.title2),
              ]),
              SliderTheme(
                data: SliderThemeData(
                  activeTrackColor: AppColors.primary,
                  inactiveTrackColor: Palette.gray06,
                  thumbColor: Palette.gray01,
                  overlayColor: AppColors.primary.withValues(alpha: 0.15),
                  tickMarkShape: SliderTickMarkShape.noTickMark,
                  trackHeight: 6,
                ),
                child: Slider(
                  value: _players,
                  min: 2,
                  max: 30,
                  divisions: 28,
                  onChanged: (v) => setState(() => _players = v),
                ),
              ),
            ]),
          ),
        ],
      ),
      bottomNavigationBar: BottomCta('방 만들기', onPressed: () {
        store.createRoom(BattleSettings(
          pack: widget.pack,
          mode: _mode,
          questionCount: _count,
          secondsPerQuestion: _seconds,
          maxPlayers: _players.round(),
        ));
        Navigator.of(context)
          ..pop()
          ..pushReplacement(MaterialPageRoute(builder: (_) => const BattleLobbyScreen()));
      }),
    );
  }
}
