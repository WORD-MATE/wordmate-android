import 'package:flutter/material.dart';

import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/layout.dart';
import '../../widgets/sheets.dart';

class _Notice {
  const _Notice(this.title, this.date, this.body, {this.pinned = false});

  final String title;
  final String date;
  final String body;
  final bool pinned;
}

const _notices = [
  _Notice('WORD MATE 정식 오픈 안내', '11월 1일',
      'WORD MATE가 정식으로 문을 열었어요! 매일 10분, 친구들과 단어로 겨루며 함께 성장해요.', pinned: true),
  _Notice('2학기 기말고사 대비 단어팩 업데이트', '11월 6일', '교과서 2학년 4~6단원 단어팩이 추가되었어요. 단어팩 탭에서 확인해 보세요.'),
  _Notice('주간 랭킹 배지 지급 기준 변경', '10월 28일', '주간 랭킹 배지는 이제 학교 전체 상위 10%와 반별 1등에게 지급돼요.'),
  _Notice('서버 점검 안내 (10월 20일 02:00~04:00)', '10월 18일', '안정적인 서비스를 위해 서버 점검이 진행돼요. 점검 중에는 대결 기능을 사용할 수 없어요.'),
];

/// 공지사항 (Figma "공지", "단건 조회").
class NoticeScreen extends StatefulWidget {
  const NoticeScreen({super.key});

  @override
  State<NoticeScreen> createState() => _NoticeScreenState();
}

class _NoticeScreenState extends State<NoticeScreen> {
  int? _open;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppTopBar(title: '공지사항', onBack: () => Navigator.pop(context)),
      body: ListView.separated(
        itemCount: _notices.length,
        separatorBuilder: (_, _) => const AppDivider(indent: Gap.screen),
        itemBuilder: (_, i) {
          final n = _notices[i];
          final open = _open == i;
          return InkWell(
            onTap: () => setState(() => _open = open ? null : i),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 16),
              child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                Row(children: [
                  Expanded(
                    child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                      Row(children: [
                        if (n.pinned) ...[
                          const Tag('필독', background: AppColors.primary, color: AppColors.onPrimary),
                          const SizedBox(width: 6),
                        ],
                        Flexible(child: Text(n.title, style: AppText.body2Bold)),
                      ]),
                      const SizedBox(height: 2),
                      Text(n.date, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                    ]),
                  ),
                  Icon(open ? Icons.keyboard_arrow_up_rounded : Icons.keyboard_arrow_down_rounded,
                      color: AppColors.textTertiary),
                ]),
                AnimatedSize(
                  duration: const Duration(milliseconds: 200),
                  child: open
                      ? Container(
                          width: double.infinity,
                          margin: const EdgeInsets.only(top: 12),
                          padding: const EdgeInsets.all(16),
                          decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
                          child: Text(n.body, style: AppText.label1.copyWith(color: AppColors.textSecondary)),
                        )
                      : const SizedBox(width: double.infinity),
                ),
              ]),
            ),
          );
        },
      ),
    );
  }
}

class _Inquiry {
  const _Inquiry(this.type, this.title, this.date, this.answer);

  final String type;
  final String title;
  final String date;
  final String? answer;
}

/// 고객센터 문의 (Figma "문의 유형", "작성", "보내기 버튼 활성화", "답변").
class InquiryScreen extends StatefulWidget {
  const InquiryScreen({super.key});

  @override
  State<InquiryScreen> createState() => _InquiryScreenState();
}

class _InquiryScreenState extends State<InquiryScreen> {
  static const _types = ['학습', '대결', '계정', '오류 신고', '기타'];

  final _title = TextEditingController();
  final _body = TextEditingController();
  int _tab = 0;
  String? _type;
  final _history = [
    const _Inquiry('대결', '대결 중 화면이 멈췄어요', '11월 4일',
        '불편을 드려 죄송해요. 11월 6일 업데이트에서 해당 문제를 수정했어요. 앱을 최신 버전으로 업데이트해 주세요.'),
    const _Inquiry('학습', '내 단어팩을 친구와 공유하고 싶어요', '11월 7일', null),
  ];

  bool get _canSend => _type != null && _title.text.trim().isNotEmpty && _body.text.trim().length >= 10;

  @override
  void dispose() {
    _title.dispose();
    _body.dispose();
    super.dispose();
  }

  void _send() {
    setState(() {
      _history.insert(0, _Inquiry(_type!, _title.text.trim(), '방금', null));
      _type = null;
      _title.clear();
      _body.clear();
      _tab = 1;
    });
    FocusScope.of(context).unfocus();
    showToast(context, '문의가 접수되었어요');
  }

  @override
  Widget build(BuildContext context) {
    Widget label(String t) => Padding(
          padding: const EdgeInsets.only(bottom: 8),
          child: Text(t, style: AppText.label2Bold.copyWith(color: AppColors.textSecondary)),
        );
    return Scaffold(
      appBar: AppTopBar(title: '고객센터', onBack: () => Navigator.pop(context)),
      body: Column(children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
          child: SegmentedTabs(
            tabs: ['문의하기', '내 문의 ${_history.length}'],
            index: _tab,
            onSelect: (i) => setState(() => _tab = i),
          ),
        ),
        Expanded(
          child: _tab == 0
              ? ListView(padding: const EdgeInsets.all(Gap.screen), children: [
                  label('문의 유형'),
                  Wrap(spacing: 6, runSpacing: 6, children: [
                    for (final t in _types) AppChip(t, selected: t == _type, onTap: () => setState(() => _type = t)),
                  ]),
                  Gap.h20,
                  label('제목'),
                  AppTextField(
                      controller: _title, hint: '제목을 입력해 주세요', maxLength: 40, onChanged: (_) => setState(() {})),
                  Gap.h20,
                  label('내용'),
                  AppTextField(
                    controller: _body,
                    hint: '어떤 점이 궁금하거나 불편했는지 자세히 적어주세요 (10자 이상)',
                    maxLines: 8,
                    minLines: 6,
                    maxLength: 1000,
                    onChanged: (_) => setState(() {}),
                  ),
                  Align(
                    alignment: Alignment.centerRight,
                    child: Padding(
                      padding: const EdgeInsets.only(top: 4),
                      child: Text('${_body.text.length}/1000',
                          style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
                    ),
                  ),
                ])
              : ListView.separated(
                  padding: const EdgeInsets.only(top: 8),
                  itemCount: _history.length,
                  separatorBuilder: (_, _) => const AppDivider(indent: Gap.screen),
                  itemBuilder: (_, i) => _InquiryRow(inquiry: _history[i]),
                ),
        ),
      ]),
      bottomNavigationBar: _tab == 0 ? BottomCta('보내기', enabled: _canSend, onPressed: _send) : null,
    );
  }
}

class _InquiryRow extends StatelessWidget {
  const _InquiryRow({required this.inquiry});

  final _Inquiry inquiry;

  @override
  Widget build(BuildContext context) {
    final answered = inquiry.answer != null;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 14),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(children: [
          Tag(inquiry.type),
          const SizedBox(width: 6),
          Expanded(child: Text(inquiry.title, style: AppText.body2Bold, overflow: TextOverflow.ellipsis)),
          Tag(answered ? '답변 완료' : '답변 대기',
              background: answered ? AppColors.primary : AppColors.surfaceVariant,
              color: answered ? AppColors.onPrimary : AppColors.textSecondary),
        ]),
        Padding(
          padding: const EdgeInsets.only(top: 2),
          child: Text(inquiry.date, style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
        ),
        if (answered)
          Container(
            width: double.infinity,
            margin: const EdgeInsets.only(top: 10),
            padding: const EdgeInsets.all(14),
            decoration: const BoxDecoration(color: AppColors.surface, borderRadius: AppRadius.md),
            child: Text('워드메이트 · ${inquiry.answer}', style: AppText.label2.copyWith(color: AppColors.textSecondary)),
          ),
      ]),
    );
  }
}
