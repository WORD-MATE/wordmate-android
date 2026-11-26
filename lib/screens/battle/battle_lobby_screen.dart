import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import 'battle_home_screen.dart';

/// 대결이 시작되면 이동할 화면. 대결 진행 커밋에서 연결한다.
void Function(BuildContext context) startBattle = (context) => Navigator.pop(context);

/// 대기실 (Figma "대기실", "내가 방장이 아닐때 대기실").
class BattleLobbyScreen extends StatefulWidget {
  const BattleLobbyScreen({super.key});

  @override
  State<BattleLobbyScreen> createState() => _BattleLobbyScreenState();
}

class _BattleLobbyScreenState extends State<BattleLobbyScreen> {
  final BattleRoom room = store.currentRoom!;
  final _timers = <Timer>[];

  BattlePlayer get me => room.players.firstWhere((p) => p.isMe);

  @override
  void initState() {
    super.initState();
    // 친구 입장 시뮬레이션: 잠시 후 한 명이 더 들어오고, 방장이 아니면 방장이 곧 시작한다.
    _timers.add(Timer(const Duration(milliseconds: 2500), () {
      if (room.players.length < room.settings.maxPlayers) {
        setState(() => room.players.add(const BattlePlayer('이서연', '2학년 2반')));
      }
      if (!me.isHost) _timers.add(Timer(const Duration(seconds: 2), () => startBattle(context)));
    }));
  }

  @override
  void dispose() {
    for (final t in _timers) {
      t.cancel();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final s = room.settings;
    return Scaffold(
      appBar: AppTopBar(title: '대기실', onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          AppCard(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text('초대 코드', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
              Gap.h8,
              CodeBoxes(code: room.code, filled: true),
              Gap.h12,
              Row(children: [
                Expanded(
                  child: AppButton('코드 복사', kind: ButtonKind.outline, height: 40, leading: Icons.copy_rounded,
                      onPressed: () async {
                    await Clipboard.setData(ClipboardData(text: room.code));
                    if (context.mounted) showToast(context, '코드를 복사했어요');
                  }),
                ),
                Gap.w8,
                Expanded(
                  child: AppButton('링크 공유', kind: ButtonKind.outline, height: 40, leading: Icons.share_rounded,
                      onPressed: () async {
                    await Clipboard.setData(ClipboardData(text: 'WORD MATE 대결에 참여해요! 코드: ${room.code}'));
                    if (context.mounted) showToast(context, '초대 문구를 복사했어요');
                  }),
                ),
              ]),
              Gap.h16,
              _InfoLine('단어팩', s.pack.title),
              _InfoLine('점수 방식', s.mode.label),
              _InfoLine('문항', '${s.questionCount}문항 · 문항당 ${s.secondsPerQuestion}초'),
            ]),
          ),
          Gap.h20,
          Row(children: [
            Text.rich(TextSpan(children: [
              TextSpan(text: '참가자 ', style: AppText.body2Bold),
              TextSpan(text: '${room.players.length}', style: AppText.body2Bold.copyWith(color: AppColors.primaryDark)),
              TextSpan(text: ' / ${s.maxPlayers}명', style: AppText.body2.copyWith(color: AppColors.textTertiary)),
            ])),
            const Spacer(),
            Text(me.isHost ? '방장이 대결을 시작할 수 있어요' : '방장이 시작하면 바로 대결이 시작돼요',
                style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ]),
          const SizedBox(height: 10),
          GridView.count(
            crossAxisCount: 2,
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            mainAxisSpacing: 8,
            crossAxisSpacing: 8,
            childAspectRatio: 1.5,
            children: [
              for (final p in room.players) _PlayerSlot(player: p),
              for (var i = room.players.length; i < 4; i++) const _PlayerSlot(),
            ],
          ),
          Gap.h16,
        ],
      ),
      bottomNavigationBar: me.isHost
          ? BottomCta('${room.players.length}명으로 시작하기',
              enabled: room.players.length >= 2, onPressed: () => startBattle(context))
          : const BottomCta('방장이 시작하길 기다리는 중', enabled: false, onPressed: null),
    );
  }
}

class _InfoLine extends StatelessWidget {
  const _InfoLine(this.label, this.value);

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
      child: Row(children: [
        SizedBox(width: 72, child: Text(label, style: AppText.caption1.copyWith(color: AppColors.textTertiary))),
        Expanded(child: Text(value, style: AppText.caption1Bold)),
      ]),
    );
  }
}

class _PlayerSlot extends StatelessWidget {
  const _PlayerSlot({this.player});

  final BattlePlayer? player;

  @override
  Widget build(BuildContext context) {
    final p = player;
    return Container(
      decoration: BoxDecoration(
        color: p?.isMe == true ? AppColors.surfacePressed : AppColors.surface,
        borderRadius: AppRadius.md,
        border: p == null ? Border.all(color: AppColors.divider) : null,
      ),
      child: Stack(children: [
        Center(
          child: p == null
              ? Column(mainAxisSize: MainAxisSize.min, children: [
                  const Icon(Icons.person_outline_rounded, size: 28, color: Palette.gray11),
                  Text('빈 자리', style: AppText.caption1.copyWith(color: AppColors.textDisabled)),
                ])
              : Column(mainAxisSize: MainAxisSize.min, children: [
                  Avatar(background: p.isMe ? AppColors.background : Palette.gray06),
                  const SizedBox(height: 6),
                  Text(p.isMe ? '${p.name} (나)' : p.name, style: AppText.label2Bold),
                  Text(p.schoolInfo, style: AppText.caption2.copyWith(color: AppColors.textTertiary)),
                ]),
        ),
        if (p?.isHost == true)
          const Positioned(
            top: 8,
            left: 8,
            child: Tag('방장', background: AppColors.primary, color: AppColors.onPrimary),
          ),
      ]),
    );
  }
}

/// 6칸 코드 박스. [filled] 가 false 면 입력 중 상태로 다음 칸을 강조한다.
class CodeBoxes extends StatelessWidget {
  const CodeBoxes({super.key, required this.code, required this.filled});

  final String code;
  final bool filled;

  @override
  Widget build(BuildContext context) {
    return Row(children: [
      for (var i = 0; i < 6; i++) ...[
        if (i > 0) const SizedBox(width: 6),
        Expanded(
          child: AspectRatio(
            aspectRatio: 0.85,
            child: Container(
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: filled || i < code.length ? AppColors.background : AppColors.surfaceVariant,
                borderRadius: AppRadius.sm,
                border: Border.all(
                  color: !filled && i == code.length ? AppColors.textPrimary : AppColors.border,
                  width: !filled && i == code.length ? 2 : 1,
                ),
              ),
              child: Text(i < code.length ? code[i] : '', style: AppText.headline1),
            ),
          ),
        ),
      ],
    ]);
  }
}

/// 참가 코드 입력 (Figma "코드 입력").
class BattleCodeScreen extends StatefulWidget {
  const BattleCodeScreen({super.key});

  @override
  State<BattleCodeScreen> createState() => _BattleCodeScreenState();
}

class _BattleCodeScreenState extends State<BattleCodeScreen> {
  final _controller = TextEditingController();
  final _focus = FocusNode();
  String? _error;

  @override
  void dispose() {
    _controller.dispose();
    _focus.dispose();
    super.dispose();
  }

  void _join() {
    if (store.joinRoom(_controller.text) == null) {
      setState(() => _error = '코드를 다시 확인해 주세요');
      return;
    }
    replace(context, const BattleLobbyScreen());
  }

  @override
  Widget build(BuildContext context) {
    final code = _controller.text;
    return Scaffold(
      appBar: AppTopBar(title: '코드로 참가', onBack: () => Navigator.pop(context)),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Gap.h8,
          Text('참가 코드를 입력하세요', style: AppText.headline2),
          Gap.h4,
          Text('친구나 선생님에게 받은 6자리 코드', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
          Gap.h24,
          GestureDetector(
            onTap: _focus.requestFocus,
            child: Stack(children: [
              CodeBoxes(code: code, filled: false),
              // 보이지 않는 입력창이 키보드 입력을 받는다.
              Positioned.fill(
                child: Opacity(
                  opacity: 0,
                  child: TextField(
                    controller: _controller,
                    focusNode: _focus,
                    autofocus: true,
                    maxLength: 6,
                    showCursor: false,
                    textCapitalization: TextCapitalization.characters,
                    inputFormatters: [
                      FilteringTextInputFormatter.allow(RegExp('[a-zA-Z0-9]')),
                      TextInputFormatter.withFunction((_, v) => v.copyWith(text: v.text.toUpperCase())),
                    ],
                    onChanged: (_) => setState(() => _error = null),
                  ),
                ),
              ),
            ]),
          ),
          if (_error != null) ...[
            Gap.h8,
            Text(_error!, style: AppText.caption1.copyWith(color: AppColors.error)),
          ],
        ]),
      ),
      bottomNavigationBar: Padding(
        padding: EdgeInsets.only(bottom: MediaQuery.viewInsetsOf(context).bottom),
        child: BottomCta('참가하기',
            enabled: code.length == 6,
            kind: code.length == 6 ? ButtonKind.primary : ButtonKind.secondary,
            onPressed: _join),
      ),
    );
  }
}

/// 대결 기록 전체 (Figma "대결 기록").
class BattleHistoryScreen extends StatelessWidget {
  const BattleHistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(title: '대결 기록', onBack: () => Navigator.pop(context)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Text('내 대결 기록', style: AppText.title2),
          ),
          BattleStatsCard(records: store.battleRecords),
          Gap.h16,
          for (final r in store.battleRecords) ...[BattleRecordRow(record: r), Gap.h8],
          Gap.h24,
        ],
      ),
    );
  }
}
