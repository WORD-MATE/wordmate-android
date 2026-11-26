import 'package:flutter/material.dart';

import '../../data/store.dart';
import '../../utils/format.dart';
import '../../utils/nav.dart';
import '../../utils/theme.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import '../study/study_session_screen.dart';
import 'battle_game.dart';
import 'battle_result_screen.dart';

const _reactions = ['👍', '🔥', '👏', '😂', '😮', '😭'];

/// 실제 대결 페이지 (Figma "실제 대결 페이지", "틀림").
class BattlePlayScreen extends StatefulWidget {
  const BattlePlayScreen({super.key});

  @override
  State<BattlePlayScreen> createState() => _BattlePlayScreenState();
}

class _BattlePlayScreenState extends State<BattlePlayScreen> {
  late final _game = BattleGame(room: store.currentRoom!)..addListener(_onChange);

  void _onChange() {
    if (_game.finished) {
      _game.removeListener(_onChange);
      replace(context, const BattleResultScreen());
    }
  }

  @override
  void dispose() {
    _game.dispose();
    super.dispose();
  }

  Future<void> _confirmQuit() async {
    final keep = await showConfirmSheet(
      context,
      title: '대결을 나갈까요?',
      description: '지금 나가면 이번 대결 기록이 남지 않아요.',
      confirmText: '계속하기',
      cancelText: '나가기',
    );
    if (!keep && mounted) Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, _) {
        if (!didPop) _confirmQuit();
      },
      child: Scaffold(
        body: SafeArea(
          child: ListenableBuilder(
            listenable: _game,
            builder: (context, _) {
              final g = _game;
              final q = g.question;
              if (q == null) return const SizedBox.shrink();
              final urgent = g.timeFraction < 0.3;
              return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Padding(
                  padding: const EdgeInsets.fromLTRB(Gap.screen, 14, Gap.screen, 14),
                  child: Row(children: [
                    const Tag('대결 중', background: AppColors.primary, color: AppColors.onPrimary),
                    const SizedBox(width: 10),
                    Expanded(
                      child: ProgressBar(
                        value: g.timeFraction,
                        height: 8,
                        color: urgent ? AppColors.error : AppColors.primary,
                      ),
                    ),
                    const SizedBox(width: 10),
                    Text('${(g.remainingMs / 1000).ceil()}',
                        style: AppText.title2.copyWith(color: urgent ? AppColors.error : AppColors.textPrimary)),
                  ]),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
                  child: Row(children: [
                    Text('문제 ${g.index + 1}/${g.questions.length}',
                        style: AppText.label2.copyWith(color: AppColors.textSecondary)),
                    const Spacer(),
                    if (g.lastGain > 0)
                      Text('+${g.lastGain} ', style: AppText.label2Bold.copyWith(color: AppColors.primaryDark)),
                    Text('${formatNumber(g.score)}점', style: AppText.label1Bold),
                  ]),
                ),
                Gap.h16,
                Container(
                  width: double.infinity,
                  margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
                  padding: const EdgeInsets.symmetric(vertical: 32, horizontal: 16),
                  decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.lg),
                  child: Column(children: [
                    Text('알맞은 단어를 고르세요', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                    Gap.h8,
                    Text(q.prompt, textAlign: TextAlign.center, style: AppText.wordLarge),
                  ]),
                ),
                Gap.h12,
                Expanded(
                  child: ListView(children: [
                    for (var i = 0; i < q.choices.length; i++)
                      ChoiceRow(
                        text: q.choices[i],
                        state: !g.revealed
                            ? ChoiceState.idle
                            : i == q.answerIndex
                                ? ChoiceState.correct
                                : i == g.selectedIndex
                                    ? ChoiceState.wrong
                                    : ChoiceState.dimmed,
                        onTap: g.revealed ? null : () => g.select(i),
                      ),
                    if (g.revealed)
                      Padding(
                        padding: const EdgeInsets.fromLTRB(Gap.screen, 8, Gap.screen, 0),
                        child: Text(
                          g.selectedIndex == q.answerIndex
                              ? '정답! +${g.lastGain}점'
                              : g.selectedIndex == null
                                  ? '시간 초과! 정답은 ${q.choices[q.answerIndex]}'
                                  : '아쉬워요, 오답노트에 담았어요',
                          style: AppText.label1Bold.copyWith(
                              color: g.selectedIndex == q.answerIndex ? AppColors.primaryDark : AppColors.error),
                        ),
                      ),
                  ]),
                ),
                Center(
                  child: AnimatedScale(
                    scale: g.reaction == null ? 0 : 1,
                    duration: const Duration(milliseconds: 200),
                    child: Text(g.reaction ?? ' ', style: const TextStyle(fontSize: 40)),
                  ),
                ),
                Center(
                  child: Text('친구들에게 반응 보내기',
                      style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 12),
                  child: Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
                    for (final e in _reactions)
                      Material(
                        color: AppColors.surface,
                        shape: const CircleBorder(),
                        clipBehavior: Clip.antiAlias,
                        child: InkWell(
                          onTap: () => g.react(e),
                          child: SizedBox(
                            width: 44,
                            height: 44,
                            child: Center(child: Text(e, style: const TextStyle(fontSize: 20))),
                          ),
                        ),
                      ),
                  ]),
                ),
              ]);
            },
          ),
        ),
      ),
    );
  }
}
