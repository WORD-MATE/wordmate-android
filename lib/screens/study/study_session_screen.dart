import 'dart:math';

import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../utils/nav.dart';
import '../../utils/speaker.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';
import 'study_result_screen.dart';
import 'study_session.dart';

/// 학습 진행 (Figma "플래시 카드", "4지선다", "4지선다 맞음/틀림", "직접 입력", "직접 입력 틀림",
/// "중간에 나가려할때 바텀시트").
class StudySessionScreen extends StatefulWidget {
  const StudySessionScreen({super.key, required this.pack, required this.options, this.onlyWrong = false});

  final WordPack pack;
  final StudyOptions options;
  final bool onlyWrong;

  @override
  State<StudySessionScreen> createState() => _StudySessionScreenState();
}

class _StudySessionScreenState extends State<StudySessionScreen> {
  late final _session = StudySession(pack: widget.pack, options: widget.options, onlyWrong: widget.onlyWrong)
    ..addListener(_onChange);
  final _input = TextEditingController();

  void _onChange() {
    if (_session.finished) {
      replace(context, StudyResultScreen(result: _session.result!, pack: widget.pack));
    } else if (_session.answeredCorrect == null) {
      _input.clear();
    }
  }

  @override
  void dispose() {
    _session.dispose();
    _input.dispose();
    super.dispose();
  }

  Future<void> _confirmQuit() async {
    final keep = await showConfirmSheet(
      context,
      title: '학습을 멈출까요?',
      description: '지금까지 푼 문제는 저장되지 않아요.\n다음에 처음부터 다시 시작해요.',
      confirmText: '이어하기',
      cancelText: '그만하기',
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
      child: ListenableBuilder(
        listenable: _session,
        builder: (context, _) {
          final s = _session;
          final q = s.current;
          return Scaffold(
            appBar: PreferredSize(
              preferredSize: const Size.fromHeight(56),
              child: SafeArea(
                child: SizedBox(
                  height: 56,
                  child: Row(children: [
                    const SizedBox(width: 4),
                    AppIconButton(Icons.close_rounded, tooltip: '학습 종료', onPressed: _confirmQuit),
                    Expanded(child: ProgressBar(value: s.progress)),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Text('${min(s.index + 1, s.questions.length)}/${s.questions.length}',
                          style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                    ),
                  ]),
                ),
              ),
            ),
            body: q == null
                ? const SizedBox.shrink()
                : Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
                      child: Text('${widget.pack.title} · ${widget.options.mode.title}',
                          style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                    ),
                    Gap.h16,
                    Expanded(
                      child: switch (widget.options.mode) {
                        StudyMode.flashcard => _Flashcard(session: s, question: q),
                        StudyMode.multipleChoice => _MultipleChoice(session: s, question: q),
                        StudyMode.typing => _Typing(session: s, question: q, input: _input),
                      },
                    ),
                  ]),
          );
        },
      ),
    );
  }
}

class _PromptCard extends StatelessWidget {
  const _PromptCard({required this.session, required this.question});

  final StudySession session;
  final StudyQuestion question;

  @override
  Widget build(BuildContext context) {
    final enToKo = session.enToKo;
    final label = switch (session.options.mode) {
      StudyMode.typing => enToKo ? '뜻을 입력하세요' : '영어 단어를 입력하세요',
      _ => enToKo ? '알맞은 뜻을 고르세요' : '알맞은 단어를 고르세요',
    };
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(horizontal: Gap.screen),
      padding: const EdgeInsets.symmetric(vertical: 28, horizontal: 16),
      decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.lg),
      child: Column(children: [
        Text(label, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        Gap.h8,
        Row(mainAxisAlignment: MainAxisAlignment.center, children: [
          Flexible(child: Text(question.prompt, textAlign: TextAlign.center, style: AppText.wordLarge)),
          if (enToKo)
            IconButton(
              tooltip: '발음 듣기',
              onPressed: () => Speaker.speak(question.word.term),
              icon: const Icon(Icons.volume_up_rounded, color: AppColors.textSecondary),
            ),
        ]),
        if (enToKo && question.word.pronunciation.isNotEmpty)
          Text(question.word.pronunciation, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
      ]),
    );
  }
}

class _Feedback extends StatelessWidget {
  const _Feedback({required this.correct, required this.question});

  final bool correct;
  final StudyQuestion question;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Icon(correct ? Icons.check_circle_rounded : Icons.error_outline_rounded,
              size: 18, color: correct ? AppColors.primaryDark : AppColors.error),
          const SizedBox(width: 6),
          Text(correct ? '정답이에요' : '아쉬워요, 오답노트에 담았어요',
              style: AppText.label1Bold.copyWith(color: correct ? AppColors.textPrimary : AppColors.error)),
        ]),
        if (!correct)
          Padding(
            padding: const EdgeInsets.only(left: 24, top: 2),
            child: Text('정답: ${question.answer}', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
          ),
        if (question.word.example.isNotEmpty)
          Padding(
            padding: const EdgeInsets.only(left: 24, top: 2),
            child: Text(question.word.example, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
          ),
      ]),
    );
  }
}

class _Flashcard extends StatelessWidget {
  const _Flashcard({required this.session, required this.question});

  final StudySession session;
  final StudyQuestion question;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Expanded(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
          child: GestureDetector(
            onTap: session.flip,
            child: TweenAnimationBuilder<double>(
              tween: Tween(end: session.flipped ? pi : 0),
              duration: const Duration(milliseconds: 350),
              builder: (context, angle, _) {
                final back = angle > pi / 2;
                return Transform(
                  alignment: Alignment.center,
                  transform: Matrix4.identity()
                    ..setEntry(3, 2, 0.001)
                    ..rotateY(angle),
                  child: Container(
                    width: double.infinity,
                    decoration: BoxDecoration(
                      color: AppColors.surface,
                      borderRadius: AppRadius.xl,
                      border: Border.all(color: AppColors.divider),
                    ),
                    child: Transform(
                      alignment: Alignment.center,
                      transform: Matrix4.identity()..rotateY(back ? pi : 0),
                      child: Stack(children: [
                        Center(
                          child: Padding(
                            padding: const EdgeInsets.all(24),
                            child: Column(mainAxisSize: MainAxisSize.min, children: [
                              Text(back ? '뜻' : '단어', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                              Gap.h8,
                              Text(back ? question.answer : question.prompt,
                                  textAlign: TextAlign.center, style: AppText.wordLarge),
                              Gap.h8,
                              Text(back ? question.word.example : '탭해서 뜻 확인',
                                  textAlign: TextAlign.center,
                                  style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                            ]),
                          ),
                        ),
                        if (!back)
                          Positioned(
                            top: 8,
                            right: 8,
                            child: AppIconButton(Icons.volume_up_rounded,
                                tooltip: '발음 듣기',
                                color: AppColors.textSecondary,
                                onPressed: () => Speaker.speak(question.word.term)),
                          ),
                      ]),
                    ),
                  ),
                );
              },
            ),
          ),
        ),
      ),
      SafeArea(
        top: false,
        child: Padding(
          padding: const EdgeInsets.all(Gap.screen),
          child: Row(children: [
            Expanded(
              child: AppButton('몰라요', kind: ButtonKind.secondary, onPressed: () => session.markKnown(false)),
            ),
            Gap.w8,
            Expanded(child: AppButton('알아요', onPressed: () => session.markKnown(true))),
          ]),
        ),
      ),
    ]);
  }
}

class _MultipleChoice extends StatelessWidget {
  const _MultipleChoice({required this.session, required this.question});

  final StudySession session;
  final StudyQuestion question;

  @override
  Widget build(BuildContext context) {
    final answered = session.answeredCorrect != null;
    return Column(children: [
      Expanded(
        child: ListView(children: [
          _PromptCard(session: session, question: question),
          Gap.h16,
          for (var i = 0; i < question.choices.length; i++)
            ChoiceRow(
              number: i + 1,
              text: question.choices[i],
              state: !answered
                  ? ChoiceState.idle
                  : question.choices[i] == question.answer
                      ? ChoiceState.correct
                      : i == session.selectedIndex
                          ? ChoiceState.wrong
                          : ChoiceState.dimmed,
              onTap: answered ? null : () => session.select(i),
            ),
        ]),
      ),
      if (answered) ...[
        _Feedback(correct: session.answeredCorrect!, question: question),
        BottomCta('다음', onPressed: session.next),
      ],
    ]);
  }
}

enum ChoiceState { idle, correct, wrong, dimmed }

/// 보기 한 줄. 대결 화면에서도 쓴다.
class ChoiceRow extends StatelessWidget {
  const ChoiceRow({super.key, this.number, required this.text, required this.state, required this.onTap});

  final int? number;
  final String text;
  final ChoiceState state;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final highlighted = state == ChoiceState.correct || state == ChoiceState.wrong;
    final fg = switch (state) {
      ChoiceState.dimmed => AppColors.textDisabled,
      ChoiceState.wrong => AppColors.error,
      _ => AppColors.textPrimary,
    };
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 4),
      child: Material(
        color: highlighted ? AppColors.surfacePressed : AppColors.surface,
        borderRadius: AppRadius.md,
        clipBehavior: Clip.antiAlias,
        child: InkWell(
          onTap: onTap,
          child: ConstrainedBox(
            constraints: const BoxConstraints(minHeight: 52),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              child: Row(children: [
                if (number != null) ...[
                  Text('$number',
                      style: AppText.label1Bold.copyWith(
                          color: state == ChoiceState.dimmed ? AppColors.textDisabled : AppColors.textTertiary)),
                  const SizedBox(width: 14),
                ],
                Expanded(child: Text(text, style: AppText.body2Bold.copyWith(color: fg))),
                if (state == ChoiceState.correct) const ResultMark(correct: true),
                if (state == ChoiceState.wrong) const ResultMark(correct: false),
              ]),
            ),
          ),
        ),
      ),
    );
  }
}

class ResultMark extends StatelessWidget {
  const ResultMark({super.key, required this.correct, this.size = 22});

  final bool correct;
  final double size;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(shape: BoxShape.circle, color: correct ? AppColors.primary : AppColors.error),
      child: Icon(correct ? Icons.check_rounded : Icons.close_rounded,
          size: size * 0.64, color: correct ? AppColors.onPrimary : Palette.gray01),
    );
  }
}

class _Typing extends StatefulWidget {
  const _Typing({required this.session, required this.question, required this.input});

  final StudySession session;
  final StudyQuestion question;
  final TextEditingController input;

  @override
  State<_Typing> createState() => _TypingState();
}

class _TypingState extends State<_Typing> {
  @override
  Widget build(BuildContext context) {
    final s = widget.session;
    final answered = s.answeredCorrect != null;
    return Column(children: [
      Expanded(
        child: ListView(children: [
          _PromptCard(session: s, question: widget.question),
          Gap.h16,
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
            child: AppTextField(
              controller: widget.input,
              hint: s.enToKo ? '뜻을 입력하세요' : '단어를 입력하세요',
              enabled: !answered,
              error: s.answeredCorrect == false,
              textInputAction: TextInputAction.done,
              onChanged: (_) => setState(() {}),
              onSubmitted: s.submitTyped,
            ),
          ),
          if (s.answeredCorrect == false)
            Padding(
              padding: const EdgeInsets.fromLTRB(24, 10, 24, 0),
              child: Text('정답 ${widget.question.answer}', style: AppText.label1Bold),
            ),
        ]),
      ),
      if (answered) ...[
        _Feedback(correct: s.answeredCorrect!, question: widget.question),
        BottomCta('다음', onPressed: s.next),
      ] else
        BottomCta('확인',
            kind: ButtonKind.secondary,
            enabled: widget.input.text.trim().isNotEmpty,
            onPressed: () => s.submitTyped(widget.input.text)),
    ]);
  }
}
